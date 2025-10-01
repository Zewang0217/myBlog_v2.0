// blog-frontend/src/api/articleService.ts
// 文章服务模块，封装文章相关的 API 调用
import apiClient from './apiClient'

// 导入文章相关的类型定义
import type { Article, CreateArticleDTO } from '@/types/article'
import type { ApiResponse } from '@/types/api'
import type { Category } from '@/types/article'

// 获取所有文章列表
export const getArticles = (): Promise<ApiResponse<Article[]>> => {
  return apiClient.get<ApiResponse<Article[]>>('/api/article/list')
  .then(response => response.data)
}

// 获取已发布的文章列表
export const getPublishedArticles = (): Promise<ApiResponse<Article[]>> => {
  return apiClient.get<ApiResponse<Article[]>>('/api/article/published')
  .then(response => response.data)
}

// 获取草稿列表
export const getDraftArticles = (): Promise<ApiResponse<Article[]>> => {
  return apiClient.get<ApiResponse<Article[]>>('/api/article/drafts')
  .then(response => response.data)
}

// 根据 ID 获取文章详情
export const getArticleById = (id: string | number): Promise<ApiResponse<Article>> => {
  // console.log('准备发送请求，文章ID:', id);
  return apiClient.get<ApiResponse<Article>>(`/api/article/${id}`)
  .then(response => response.data)
}

// 创建新文章
export const createArticle = (article: CreateArticleDTO): Promise<ApiResponse<Article>> => {
  return apiClient.post<ApiResponse<Article>>('/api/article/new', article)
  .then(response => response.data)
}

// 更新文章
export const updateArticle = (id: string, article: CreateArticleDTO): Promise<ApiResponse<Article>> => {
  return apiClient.post<ApiResponse<Article>>(`/api/article/edit/${id}`, article)
  .then(response => response.data)
}

// 发布文章
export const publishArticle = (id: string): Promise<ApiResponse<Article>> => {
  return apiClient.post<ApiResponse<Article>>(`/api/article/${id}/publish`)
  .then(response => response.data)
}

// 删除文章
export const deleteArticle = (id: string): Promise<ApiResponse<void>> => {
  return apiClient.post<ApiResponse<void>>(`/api/article/delete/${id}`)
  .then(response => response.data)
}

// 获取分类列表
export const getCategories = (): Promise<ApiResponse<Category[]>> => {
  return apiClient.get<ApiResponse<Category[]>>('/api/category')
  .then(response => response.data)
}

// 创建分类
export const createCategory = (category: Omit<Category, 'id' | 'createTime' | 'updateTime'>): Promise<ApiResponse<Category>> => {
  return apiClient.post<ApiResponse<Category>>('/api/category', category)
  .then(response => response.data)
}

// 删除分类
export const deleteCategory = (id: string): Promise<ApiResponse<void>> => {
  return apiClient.delete<ApiResponse<void>>(`/api/category/${id}`)
  .then(response => response.data)
}

// 根据分类筛选文章
export const getArticlesByCategories = (
  categoryIds: string[]
): Promise<ApiResponse<Article[]>> => {
  const params = categoryIds.length > 0
    ? { categoryIds: categoryIds.join(',') }
    : {};

  return apiClient
  .get<ApiResponse<Article[]>>('/api/article/listByCategories', { params })
  .then(response => response.data);
};