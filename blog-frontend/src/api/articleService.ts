// blog-frontend/src/api/articleService.ts
// 文章服务模块，封装文章相关的 API 调用
import apiClient from './apiClient'

// 导入文章相关的类型定义
import type { Article, CreateArticleDTO } from '@/types/article'
import type { ApiResponse } from '@/types/api'

// 获取所有文章列表
export const getArticles = (): Promise<ApiResponse<Article[]>> => {
  return apiClient.get<ApiResponse<Article[]>>('/article/list')
  .then(response => response.data);
}

// 获取已发布的文章列表
export const getPublishedArticles = (): Promise<ApiResponse<Article[]>> => {
  return apiClient.get<ApiResponse<Article[]>>('/article/published')
  .then(response => response.data);
}

// 获取草稿列表
export const getDraftArticles = (): Promise<ApiResponse<Article[]>> => {
  return apiClient.get<ApiResponse<Article[]>>('/article/drafts')
  .then(response => response.data);
}

// 根据 ID 获取文章详情
export const getArticleById = (id: number): Promise<ApiResponse<Article>> => {
  return apiClient.get<ApiResponse<Article>>(`/article/${id}`)
  .then(response => response.data);
}

// 创建新文章
export const createArticle = (article: CreateArticleDTO): Promise<ApiResponse<Article>> => {
  return apiClient.post<ApiResponse<Article>>('/article/new', article)
  .then(response => response.data);
}

// 更新文章
export const updateArticle = (id: number, article: CreateArticleDTO): Promise<ApiResponse<Article>> => {
  return apiClient.post<ApiResponse<Article>>(`/article/edit/${id}`, article)
  .then(response => response.data);
}

// 发布文章
export const publishArticle = (id: number): Promise<ApiResponse<Article>> => {
  return apiClient.post<ApiResponse<Article>>(`/article/${id}/publish`)
  .then(response => response.data);
}

// 删除文章
export const deleteArticle = (id: number): Promise<ApiResponse<void>> => {
  return apiClient.post<ApiResponse<void>>(`/article/delete/${id}`)
  .then(response => response.data);
}
