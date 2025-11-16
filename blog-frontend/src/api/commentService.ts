// blog-frontend/src/api/commentService.ts
// 评论相关的API服务
import axios from 'axios'
import type { Comment, CreateCommentDTO, CommentListResponse, CommentResponse, LikeResponse } from '@/types/comment'

// 创建axios实例
const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器 - 添加token等认证信息
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器 - 统一处理错误
api.interceptors.response.use(
  (response) => {
    return response.data
  },
  (error) => {
    // 处理常见错误
    if (error.response?.status === 401) {
      // Token过期或无效，清除token并可能重定向到登录页
      localStorage.removeItem('token')
      // 这里可以触发登录重定向
    }
    return Promise.reject(error)
  }
)

/**
 * 获取文章评论列表
 * @param articleId 文章ID
 * @returns 评论列表
 */
export const getCommentsByArticleId = async (articleId: string): Promise<CommentListResponse> => {
  try {
    const response = await api.get(`/comments/article/${articleId}`)
    return response as unknown as CommentListResponse
  } catch (error) {
    console.error('获取文章评论失败:', error)
    throw error
  }
}

/**
 * 创建评论
 * @param comment 创建评论的数据
 * @returns 创建的评论
 */
export const createComment = async (comment: CreateCommentDTO): Promise<CommentResponse> => {
  try {
    const response = await api.post('/comments', comment)
    return response as unknown as CommentResponse
  } catch (error) {
    console.error('创建评论失败:', error)
    throw error
  }
}

/**
 * 删除评论
 * @param commentId 评论ID
 * @returns 删除结果
 */
export const deleteComment = async (commentId: string): Promise<CommentResponse> => {
  try {
    const response = await api.delete(`/comments/${commentId}`)
    return response as unknown as CommentResponse
  } catch (error) {
    console.error('删除评论失败:', error)
    throw error
  }
}

/**
 * 点赞评论
 * @param commentId 评论ID
 * @returns 点赞结果
 */
export const likeComment = async (commentId: string): Promise<LikeResponse> => {
  try {
    const response = await api.post(`/comments/${commentId}/like`)
    return response as unknown as LikeResponse
  } catch (error) {
    console.error('点赞评论失败:', error)
    throw error
  }
}

/**
 * 取消点赞评论
 * @param commentId 评论ID
 * @returns 取消点赞结果
 */
export const unlikeComment = async (commentId: string): Promise<LikeResponse> => {
  try {
    const response = await api.delete(`/comments/${commentId}/like`)
    return response as unknown as LikeResponse
  } catch (error) {
    console.error('取消点赞评论失败:', error)
    throw error
  }
}