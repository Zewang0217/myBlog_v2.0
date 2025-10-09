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

  getters: {
    userRole: (state) => state.user?.role || null,
    isAdmin: (state) => state.user?.role === 'ADMIN' || state.user?.role === 'ROLE_ADMIN'
  },

  actions: {
    checkAuthStatus() {
      // 检查 token 是否存在且未过期
      if (!this.token) {
        this.isAuthenticated = false
        return false
      }

      // 检查 token 是否过期
      return this.checkTokenExpiration()
    },
    async login(credentials: { username: string; password: string }) {
      try {
        const response = await apiClient.post('/api/auth/login', credentials)

        // 确保正确提取 token
        const token = response.data.data?.token
        if (!token) {
          throw new Error('登录响应中未包含token')
        }

        this.token = token
        this.isAuthenticated = true
        localStorage.setItem('token', token)
        apiClient.defaults.headers.common['Authorization'] = `Bearer ${token}`

        // 获取用户信息
        await this.fetchUserInfo()

        return response
      } catch (error) {
        this.logout() // 出错时清理可能的认证状态
        throw new Error('登录失败')
      }
    },

    // 获取用户信息的实际实现
    async fetchUserInfo() {
      try {
        // 调用后端用户信息接口获取当前用户信息
        const response = await apiClient.get('/api/user/info')
        this.user = response.data.data
      } catch (error) {
        console.error('获取用户信息失败:', error)
        // 如果获取用户信息失败，可能需要重新登录
        this.logout()
      }
    },

    logout() {
      this.token = null
      this.isAuthenticated = false
      this.user = null
      localStorage.removeItem('token')
      delete apiClient.defaults.headers.common['Authorization']
    },

    // 检查token是否过期
    checkTokenExpiration() {
      if (!this.token) {
        this.logout()
        return false
      }

      try {
        // 解析JWT token
        const payload = JSON.parse(atob(this.token.split('.')[1]))
        // 获取过期时间（以秒为单位）
        const exp = payload.exp
        // 获取当前时间（以秒为单位）
        const currentTime = Math.floor(Date.now() / 1000)

        // 检查是否过期
        if (exp < currentTime) {
          this.logout()
          return false
        }

        return true
      } catch (error) {
        // 如果解析失败，认为token已过期
        this.logout()
        return false
      }
    }
  }
})

