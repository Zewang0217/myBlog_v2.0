// blog-frontend/src/api/articleLikeService.ts
// 文章点赞相关的API服务
import axios from 'axios'

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
 * 点赞文章响应结构
 */
export interface LikeResponse {
  code: number;
  message: string;
  data?: any;
}

/**
 * 点赞文章
 * @param articleId 文章ID
 * @returns 点赞结果
 */
export const likeArticle = async (articleId: string): Promise<LikeResponse> => {
  try {
    const response = await api.post(`/likes/article/${articleId}`)
    return response as unknown as LikeResponse
  } catch (error) {
    console.error('点赞文章失败:', error)
    throw error
  }
}

/**
 * 取消点赞文章
 * @param articleId 文章ID
 * @returns 取消点赞结果
 */
export const unlikeArticle = async (articleId: string): Promise<LikeResponse> => {
  try {
    const response = await api.delete(`/likes/article/${articleId}`)
    return response as unknown as LikeResponse
  } catch (error) {
    console.error('取消点赞文章失败:', error)
    throw error
  }
}