// blog-frontend/src/types/api.ts
// 项目中使用的全局类型定义

// API 响应的基本结构
import type {Article, Category} from "@/types/article.ts";
import apiClient from "@/api/apiClient.ts";

export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

export interface Page<T> {
  content: T[]
  totalElements: number
  totalPages: number
  number: number
  size: number
}

// 登录参数
export interface LoginDTO {
  username: string
  password: string
}

// 用户信息
export interface User {
  id: number
  username: string
  password?: string // 登录后通常不会返回密码
  role?: string
}

// 获取所有分类
export const getCategories = (): Promise<ApiResponse<Category[]>> => {
  return apiClient.get<ApiResponse<Category[]>>('/api/category')
  .then(response => response.data);
};

// 根据分类筛选文章
export const getArticlesByCategories = (categoryIds: string[]): Promise<ApiResponse<Article[]>> => {
  const params = categoryIds.length > 0 ? { categoryIds: categoryIds.join(',') } : {};
  return apiClient.get<ApiResponse<Article[]>>('/api/article/listByCategories', { params })
  .then(response => response.data);
};
