<!-- blog-frontend/src/components/blog/CommentSection.vue -->
<!-- 文章评论区组件 -->
<template>
  <div class="comment-section">
    <!-- 评论区标题 -->
    <div class="comment-header">
      <h3 class="comment-title">评论区</h3>
      <span class="comment-count">{{ comments.length }} 条评论</span>
    </div>

    <!-- 评论表单 -->
    <div class="comment-form-container" v-if="showForm">
      <form @submit.prevent="handleSubmitComment" class="comment-form">
        <div class="form-group">
          <label for="author" class="form-label">昵称</label>
          <input
            type="text"
            id="author"
            v-model="commentForm.author"
            class="form-input"
            placeholder="请输入您的昵称"
            :disabled="submitting"
            maxlength="20"
          />
        </div>
        
        <div class="form-group">
          <label for="email" class="form-label">邮箱（可选）</label>
          <input
            type="email"
            id="email"
            v-model="commentForm.authorEmail"
            class="form-input"
            placeholder="请输入您的邮箱，不会公开"
            :disabled="submitting"
            maxlength="50"
          />
        </div>
        
        <div class="form-group">
          <label for="content" class="form-label">评论内容</label>
          <textarea
            id="content"
            v-model="commentForm.content"
            class="form-textarea"
            placeholder="写下您的想法..."
            rows="4"
            :disabled="submitting"
            maxlength="500"
          ></textarea>
          <div class="char-count">{{ commentForm.content.length }}/500</div>
        </div>
        
        <div class="form-actions">
          <button
            type="button"
            @click="cancelReply"
            class="btn-cancel"
            v-if="isReplying"
          >
            取消回复
          </button>
          <button
            type="submit"
            class="btn-submit"
            :disabled="submitting || !commentForm.content.trim() || !commentForm.author.trim()"
          >
            <span v-if="submitting">提交中...</span>
            <span v-else>{{ isReplying ? '回复评论' : '发布评论' }}</span>
          </button>
        </div>
      </form>
    </div>

    <!-- 发表评论按钮 -->
    <button
      @click="toggleCommentForm"
      class="btn-add-comment"
      v-if="!showForm"
    >
      <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
        <path d="M19 13h-6v6h-2v-6H5v-2h6V5h2v6h6v2z"/>
      </svg>
      发表评论
    </button>

    <!-- 评论错误提示 -->
    <div v-if="error" class="comment-error">
      {{ error }}
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="comment-loading">
      <div class="loading-spinner"></div>
      <p>加载评论中...</p>
    </div>

    <!-- 评论列表 -->
    <div v-else-if="comments.length > 0" class="comment-list">
      <div
        v-for="comment in comments"
        :key="comment.id"
        class="comment-item"
      >
        <!-- 评论主体 -->
        <div class="comment-main">
          <div class="comment-author">
            <div class="author-avatar">{{ getAuthorInitial(comment.author) }}</div>
            <div class="author-info">
              <h4 class="author-name">{{ comment.author }}</h4>
              <span class="comment-date">{{ formatCommentDate(comment.createTime) }}</span>
            </div>
          </div>
          
          <div class="comment-content">{{ comment.content }}</div>
          
          <div class="comment-actions">
            <button
              @click="toggleCommentLike(comment.id)"
              class="action-btn like-btn"
              :class="{ liked: comment.isLiked }"
            >
              <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
              </svg>
              <span>{{ comment.likeCount || 0 }}</span>
            </button>
            
            <button
              @click="replyToComment(comment)"
              class="action-btn reply-btn"
            >
              <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                <path d="M20 2H4c-1.1 0-1.99.9-1.99 2L2 22l4-4h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm-2 12H6v-2h12v2zm0-3H6V9h12v2zm0-3H6V6h12v2z"/>
              </svg>
              回复
            </button>
            
            <button
              @click="handleDeleteComment(comment.id)"
              class="action-btn delete-btn"
              v-if="authStore.isAuthenticated && (authStore.user?.role === 'ADMIN' || authStore.user?.role === 'ROLE_ADMIN')"
            >
              <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/>
              </svg>
              删除
            </button>
          </div>
        </div>
        
        <!-- 回复表单 -->
        <div class="reply-form-container" v-if="replyingTo === comment.id">
          <form @submit.prevent="handleSubmitReply(comment.id)" class="reply-form">
            <div class="form-group">
              <textarea
                v-model="replyForm.content"
                class="form-textarea"
                placeholder="写下您的回复..."
                rows="3"
                :disabled="submitting"
                maxlength="300"
              ></textarea>
              <div class="char-count">{{ replyForm.content.length }}/300</div>
            </div>
            
            <div class="form-actions">
              <button
                type="button"
                @click="cancelReply"
                class="btn-cancel"
              >
                取消
              </button>
              <button
                type="submit"
                class="btn-submit"
                :disabled="submitting || !replyForm.content.trim()"
              >
                <span v-if="submitting">提交中...</span>
                <span v-else>回复</span>
              </button>
            </div>
          </form>
        </div>
        
        <!-- 回复列表 -->
        <div v-if="comment.replies && comment.replies.length > 0" class="replies-list">
          <div
            v-for="reply in comment.replies"
            :key="reply.id"
            class="reply-item"
          >
            <div class="comment-author">
              <div class="author-avatar">{{ getAuthorInitial(reply.author) }}</div>
              <div class="author-info">
                <h4 class="author-name">{{ reply.author }}</h4>
                <span class="comment-date">{{ formatCommentDate(reply.createTime) }}</span>
              </div>
            </div>
            
            <div class="comment-content">{{ reply.content }}</div>
            
            <div class="comment-actions">
              <button
                @click="toggleCommentLike(reply.id, comment.id)"
                class="action-btn like-btn"
                :class="{ liked: reply.isLiked }"
              >
                <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
                </svg>
                <span>{{ reply.likeCount || 0 }}</span>
              </button>
              
              <button
                @click="handleDeleteComment(reply.id, comment.id)"
                class="action-btn delete-btn"
                v-if="authStore.isAuthenticated && (authStore.user?.role === 'ADMIN' || authStore.user?.role === 'ROLE_ADMIN')"
              >
                <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/>
                </svg>
                删除
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 无评论提示 -->
    <div v-else class="no-comments">
      <svg width="48" height="48" viewBox="0 0 24 24" fill="currentColor">
        <path d="M20 2H4c-1.1 0-2 .9-2 2v18l4-4h14c1.1 0 2-.9 2-2V4c0-1.1-.9-2-2-2zm0 14H6l-2 2V4h16v12z"/>
      </svg>
      <p>暂无评论，快来发表第一条评论吧！</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useComments } from '@/composables/useComments'
import { useAuthStore } from '@/stores/auth'
import type { Comment, CreateCommentDTO } from '@/types/comment'

// Props
interface Props {
  articleId: string
}

const props = defineProps<Props>()

// Composables
const authStore = useAuthStore()
const {
  comments,
  loading,
  error,
  submitting,
  fetchComments,
  createComment,
  deleteComment,
  toggleCommentLike,
  formatCommentDate
} = useComments()

// 响应式数据
const showForm = ref(false)
const isReplying = ref(false)
const replyingTo = ref<string>('')
const commentForm = ref<CreateCommentDTO>({
  content: '',
  author: '',
  authorEmail: '',
  articleId: props.articleId
})
const replyForm = ref<{
  content: string
}>({
  content: ''
})

// 获取作者首字母
const getAuthorInitial = (authorName: string): string => {
  return authorName.charAt(0).toUpperCase()
}

// 切换评论表单显示
const toggleCommentForm = () => {
  showForm.value = !showForm.value
  if (showForm.value) {
    // 如果用户已登录，自动填充作者信息
    if (authStore.user) {
      commentForm.value.author = authStore.user.username || ''
      commentForm.value.authorEmail = authStore.user.email || ''
    }
  }
}

// 回复评论
const replyToComment = (comment: Comment) => {
  isReplying.value = true
  replyingTo.value = comment.id
  replyForm.value.content = ''
  // 滚动到回复表单
  setTimeout(() => {
    const formElement = document.querySelector(`.reply-form-container`)
    if (formElement) {
      formElement.scrollIntoView({ behavior: 'smooth', block: 'center' })
    }
  }, 100)
}

// 取消回复
const cancelReply = () => {
  isReplying.value = false
  replyingTo.value = ''
  replyForm.value.content = ''
}

// 提交主评论
const handleSubmitComment = async () => {
  const result = await createComment(commentForm.value)
  if (result.success) {
    // 清空表单
    commentForm.value.content = ''
    showForm.value = false
    // 可以显示成功提示
  } else {
    alert(result.error || '评论失败')
  }
}

// 提交回复
const handleSubmitReply = async (parentId: string) => {
  const replyData: CreateCommentDTO = {
    content: replyForm.value.content,
    author: commentForm.value.author,
    authorEmail: commentForm.value.authorEmail,
    articleId: props.articleId,
    parentId
  }
  
  const result = await createComment(replyData)
  if (result.success) {
    // 清空回复表单并关闭
    cancelReply()
    // 可以显示成功提示
  } else {
    alert(result.error || '回复失败')
  }
}

// 删除评论
const handleDeleteComment = async (commentId: string, parentId?: string) => {
  if (confirm('确定要删除这条评论吗？')) {
    const result = await deleteComment(commentId, parentId)
    if (!result.success) {
      alert(result.error || '删除失败')
    }
  }
}

// 组件挂载时获取评论
onMounted(async () => {
  await fetchComments(props.articleId)
})
</script>

<style scoped>
.comment-section {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  --primary-color: #42b983;
  --primary-color-hover: #359c6d;
  --accent-color: #ff5722;
  --accent-color-hover: #e64a19;
  --text-primary: #2c3e50;
  --text-secondary: #7f8c8d;
}

.comment-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px solid #f0f0f0;
}

.comment-title {
  margin: 0;
  color: var(--primary-color);
  font-size: 1.8em;
  font-weight: 700;
}

.comment-count {
  color: var(--text-secondary);
  font-size: 16px;
  font-weight: 500;
}

.btn-add-comment {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  background: var(--primary-color);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
  transition: all 0.3s ease;
  margin-bottom: 20px;
}

.btn-add-comment:hover {
  background: var(--primary-color-hover);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(66, 185, 131, 0.3);
}

.comment-form-container,
.reply-form-container {
  background: #f8f9fa;
  border-radius: 10px;
  padding: 24px;
  margin-bottom: 30px;
  border: 1px solid #e9ecef;
}

.reply-form-container {
  margin-left: 40px;
  margin-bottom: 20px;
}

.comment-form,
.reply-form {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.form-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.form-label {
  font-weight: 600;
  color: var(--text-primary);
  font-size: 14px;
}

.form-input,
.form-textarea {
  padding: 12px 16px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  font-size: 16px;
  transition: all 0.3s ease;
  background: white;
}

.form-input:focus,
.form-textarea:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(66, 185, 131, 0.1);
}

.form-textarea {
  resize: vertical;
  min-height: 100px;
  font-family: inherit;
}

.char-count {
  text-align: right;
  font-size: 12px;
  color: var(--text-secondary);
  margin-top: 4px;
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  align-items: center;
}

.btn-cancel,
.btn-submit {
  padding: 10px 20px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn-cancel {
  background: #e9ecef;
  color: var(--text-primary);
}

.btn-cancel:hover {
  background: #dee2e6;
}

.btn-submit {
  background: var(--primary-color);
  color: white;
}

.btn-submit:hover:not(:disabled) {
  background: var(--primary-color-hover);
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(66, 185, 131, 0.3);
}

.btn-submit:disabled {
  background: #adb5bd;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.comment-error {
  background: #fee;
  color: #c00;
  padding: 12px 16px;
  border-radius: 6px;
  margin-bottom: 20px;
  border: 1px solid #fcc;
}

.comment-loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: var(--text-secondary);
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid var(--primary-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 10px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.comment-list {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.comment-item {
  background: #fafafa;
  border-radius: 12px;
  padding: 24px;
  border: 1px solid #f0f0f0;
  transition: all 0.3s ease;
}

.comment-item:hover {
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.05);
  transform: translateY(-2px);
}

.comment-author {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.author-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  background: var(--primary-color);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  font-weight: 600;
  flex-shrink: 0;
}

.author-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.author-name {
  margin: 0;
  color: var(--text-primary);
  font-size: 18px;
  font-weight: 600;
}

.comment-date {
  color: var(--text-secondary);
  font-size: 14px;
}

.comment-content {
  color: var(--text-primary);
  line-height: 1.8;
  font-size: 16px;
  margin-bottom: 20px;
  word-break: break-word;
}

.comment-actions {
  display: flex;
  gap: 20px;
  align-items: center;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 8px 12px;
  background: none;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  color: var(--text-secondary);
  transition: all 0.3s ease;
}

.action-btn:hover {
  background: rgba(0, 0, 0, 0.05);
  color: var(--primary-color);
}

.like-btn.liked {
  color: var(--accent-color);
}

.like-btn.liked:hover {
  color: var(--accent-color-hover);
  background: rgba(255, 87, 34, 0.05);
}

.delete-btn:hover {
  color: #e53935;
  background: rgba(229, 57, 53, 0.05);
}

.replies-list {
  margin-top: 20px;
  padding-left: 40px;
  border-left: 3px solid #e9ecef;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.reply-item {
  background: white;
  border-radius: 8px;
  padding: 16px;
  border: 1px solid #e9ecef;
}

.no-comments {
  text-align: center;
  padding: 60px 20px;
  color: var(--text-secondary);
}

.no-comments svg {
  opacity: 0.5;
  margin-bottom: 16px;
}

.no-comments p {
  margin: 0;
  font-size: 16px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .comment-section {
    padding: 16px;
  }
  
  .comment-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .comment-form-container,
  .reply-form-container {
    padding: 20px;
  }
  
  .reply-form-container {
    margin-left: 20px;
  }
  
  .comment-item {
    padding: 20px;
  }
  
  .author-avatar {
    width: 40px;
    height: 40px;
    font-size: 18px;
  }
  
  .author-name {
    font-size: 16px;
  }
  
  .comment-content {
    font-size: 15px;
  }
  
  .replies-list {
    padding-left: 20px;
  }
}
</style>