// src/stores/auth.ts
import { defineStore } from 'pinia'
import apiClient from '@/api/apiClient'
import type { LoginDTO } from '@/types/api'
import type { User } from '@/types/api'


interface AuthState {
  token: string | null
  isAuthenticated: boolean
  user: User | null
}


export const useAuthStore = defineStore('auth', {
  state: (): AuthState => ({
    token: localStorage.getItem('token'),
    isAuthenticated: !!localStorage.getItem('token'),
    user: null
  }),

  actions: {
    async login(credentials: LoginDTO) {
      try {
        const response = await apiClient.post('/api/auth/login', credentials)
        const token = response.data.data.token

        this.token = token
        this.isAuthenticated = true

        // 如果后端在登录响应中返回用户信息，可以这样设置
        // this.user = response.data.data.user

        // 保存到本地存储
        localStorage.setItem('token', token)

        // 设置默认请求头
        apiClient.defaults.headers.common['Authorization'] = `Bearer ${token}`
      } catch (error) {
        throw new Error('登录失败')
      }
    },

    logout() {
      this.token = null
      this.isAuthenticated = false
      this.user = null
      localStorage.removeItem('token')
      delete apiClient.defaults.headers.common['Authorization']
    }
  }
})
