// blog-frontend/src/composables/useComments.ts
// 评论相关的组合式 API，用于在 Vue 组件中管理评论数据
import { ref } from 'vue'
import type { Ref } from 'vue'
import type { Comment, CreateCommentDTO } from '@/types/comment'
import {
  getCommentsByArticleId,
  createComment as createCommentApi,
  deleteComment as deleteCommentApi,
  likeComment as likeCommentApi,
  unlikeComment as unlikeCommentApi
} from '@/api/commentService'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

export const useComments = () => {
  const comments: Ref<Comment[]> = ref([])
  const loading = ref(false)
  const error = ref<string | null>(null)
  const submitting = ref(false)
  const authStore = useAuthStore()
  const router = useRouter()

  // 获取文章评论列表
  const fetchComments = async (articleId: string) => {
    // 检查articleId是否为空
    if (!articleId || articleId.trim() === '') {
      console.error('Invalid articleId:', articleId);
      loading.value = false;
      return { success: false, error: '文章ID无效' };
    }

    loading.value = true
    error.value = null
    try {
      const response = await getCommentsByArticleId(articleId)
      if (response.code === 200) {
        // 处理评论数据，确保replies数组存在
        const processComments = (commentList: Comment[] | undefined): Comment[] => {
          if (!commentList) return []
          return commentList.map(comment => ({
            ...comment,
            replies: comment.replies ? processComments(comment.replies) : []
          }))
        }
        comments.value = processComments(response.data || [])
        return { success: true, data: comments.value }
      } else {
        error.value = response.message || '获取评论列表失败'
        return { success: false, error: error.value }
      }
    } catch (err: any) {
      error.value = err.message || '获取评论列表失败'
      console.error('Failed to fetch comments:', err)
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  // 创建评论
  const createComment = async (commentData: CreateCommentDTO) => {
    // 检查内容是否为空
    if (!commentData.content || commentData.content.trim() === '') {
      return { success: false, error: '评论内容不能为空' }
    }
    
    // 检查作者信息是否为空
    if (!commentData.author || commentData.author.trim() === '') {
      return { success: false, error: '请输入您的昵称' }
    }

    submitting.value = true
    error.value = null
    try {
      const response = await createCommentApi(commentData)
      if (response.code === 200) {
        // 将新评论添加到列表中
        if (commentData.parentId) {
          // 如果是回复评论，找到父评论并添加到replies中
          const findAndAddReply = (commentList: Comment[]) => {
            for (let i = 0; i < commentList.length; i++) {
              if (commentList[i].id === commentData.parentId) {
                if (!commentList[i].replies) {
                  commentList[i].replies = []
                }
                commentList[i].replies!.push(response.data)
                return true
              }
              if (commentList[i].replies && findAndAddReply(commentList[i].replies || [])) {
                return true
              }
            }
            return false
          }
          findAndAddReply(comments.value)
        } else {
          // 如果是顶级评论，直接添加到评论列表开头
          comments.value.unshift(response.data)
        }
        return { success: true, data: response.data }
      } else {
        error.value = response.message || '创建评论失败'
        return { success: false, error: error.value }
      }
    } catch (err: any) {
      error.value = err.message || '创建评论失败'
      console.error('Failed to create comment:', err)
      return { success: false, error: error.value }
    } finally {
      submitting.value = false
    }
  }

  // 删除评论
  const deleteComment = async (commentId: string, parentId?: string) => {
    // 检查用户是否登录
    if (!authStore.isAuthenticated) {
      router.push('/login')
      return { success: false, error: '请先登录' }
    }

    // 检查用户权限
    const userRole = authStore.user?.role
    const hasAdminRole = userRole === 'ADMIN' || userRole === 'ROLE_ADMIN'

    if (!hasAdminRole) {
      return { success: false, error: '权限不足，只有管理员可以删除评论' }
    }

    loading.value = true
    error.value = null
    try {
      const response = await deleteCommentApi(commentId)
      if (response.code === 200) {
        // 从评论列表中移除评论
        if (parentId) {
          // 如果是回复评论，从父评论的replies中移除
          const findAndRemoveReply = (commentList: Comment[]) => {
            for (let i = 0; i < commentList.length; i++) {
              if (commentList[i].id === parentId && commentList[i].replies) {
                commentList[i].replies = commentList[i].replies!.filter(
                  reply => reply.id !== commentId
                )
                return true
              }
              if (commentList[i].replies && findAndRemoveReply(commentList[i].replies || [])) {
                return true
              }
            }
            return false
          }
          findAndRemoveReply(comments.value || [])
        } else {
          // 如果是顶级评论，直接从评论列表中移除
          comments.value = (comments.value || []).filter(comment => comment.id !== commentId)

        }
        return { success: true }
      } else {
        error.value = response.message || '删除评论失败'
        return { success: false, error: error.value }
      }
    } catch (err: any) {
      error.value = err.message || '删除评论失败'
      console.error('Failed to delete comment:', err)
      return { success: false, error: error.value }
    } finally {
      loading.value = false
    }
  }

  // 切换评论点赞状态
  const toggleCommentLike = async (commentId: string, parentId?: string) => {
    // 查找要更新的评论
    const findCommentToUpdate = (commentList: Comment[]): Comment | null => {
      for (let comment of commentList) {
        if (comment.id === commentId) {
          return comment
        }
        if (comment.replies) {
          const found = findCommentToUpdate(comment.replies)
          if (found) return found
        }
      }
      return null
    }

    const commentToUpdate = findCommentToUpdate(comments.value)
    if (!commentToUpdate) {
      return { success: false, error: '评论不存在' }
    }

    // 乐观更新UI
    const newIsLiked = !commentToUpdate.isLiked
    const newLikeCount = newIsLiked 
      ? (commentToUpdate.likeCount || 0) + 1 
      : Math.max(0, (commentToUpdate.likeCount || 0) - 1)

    commentToUpdate.isLiked = newIsLiked
    commentToUpdate.likeCount = newLikeCount

    try {
      if (newIsLiked) {
        await likeCommentApi(commentId)
      } else {
        await unlikeCommentApi(commentId)
      }
      return { success: true }
    } catch (err) {
      // 回滚乐观更新
      commentToUpdate.isLiked = !newIsLiked
      commentToUpdate.likeCount = newIsLiked 
        ? (commentToUpdate.likeCount || 0) - 1 
        : (commentToUpdate.likeCount || 0) + 1
      
      console.error('点赞操作失败:', err)
      return { success: false, error: '点赞操作失败' }
    }
  }

  // 格式化日期显示
  const formatCommentDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    })
  }

  return {
    comments,
    loading,
    error,
    submitting,
    fetchComments,
    createComment,
    deleteComment,
    toggleCommentLike,
    formatCommentDate
  }
}