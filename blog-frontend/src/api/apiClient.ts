// src/api/apiClient.ts
import axios from 'axios'
import { useAuthStore } from '@/stores/auth'
import router from '@/router'

const apiClient = axios.create({
  baseURL: 'http://localhost:8080',
  timeout: 10000,
})

// 请求拦截器 - 添加认证信息
apiClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// 响应拦截器
apiClient.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    const authStore = useAuthStore()

    if (error.response?.status === 401) {
      // Token过期或无效
      authStore.logout()
      alert('登录已过期，请重新登录')
      router.push('/login')
    } else if (error.response?.status === 403) {
      console.warn('权限不足:', error.response.data?.message || '访问被拒绝')
    }

    return Promise.reject(error)
  }
)

export default apiClient
