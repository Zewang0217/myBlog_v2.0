// src/api/apiClient.ts
import axios from 'axios'

// 创建 axios 实例
const apiClient = axios.create({
  baseURL: '', // 使用相对路径以避免跨域问题
  timeout: 10000, // 请求超时时间
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
apiClient.interceptors.request.use(
  (config) => {
    // 添加认证 token
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

// 响应拦截器
apiClient.interceptors.response.use(
  (response) => {
    return response
  },
  (error) => {
    console.error('API Error:', error.response || error.message)

    // 如果是认证错误，清除认证信息
    if (error.response?.status === 401) {
      localStorage.removeItem('token')
      // 只在当前不是登录页面时重定向
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    }

    return Promise.reject(error)
  }
)

export default apiClient
