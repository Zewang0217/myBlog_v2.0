// blog-frontend/src/types/api.ts
// 项目中使用的全局类型定义

// API 响应的基本结构
export interface ApiResponse<T> {
  code: number
  message: string
  data: T
}

// 分页响应结构
export interface PageResponse<T> {
  content: T[]
  totalElements: number
  totalPages: number
  currentPage: number
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
