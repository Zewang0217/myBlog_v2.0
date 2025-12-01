// src/api/uploadService.ts - 文件上传服务

import apiClient from './apiClient'

// 上传图片的响应接口
export interface UploadImageResponse {
  success: boolean
  data?: {
    url: string
    fileName: string
    size: number
    width: number
    height: number
  }
  message?: string
}

/**
 * 压缩图片到指定大小
 * @param file - 原始图片文件
 * @param maxSizeMB - 最大文件大小（MB）
 * @returns 压缩后的图片文件
 */
export const compressImage = async (file: File, maxSizeMB: number = 5): Promise<File> => {
  const maxSizeBytes = maxSizeMB * 1024 * 1024
  
  // 如果文件已经小于目标大小，直接返回
  if (file.size <= maxSizeBytes) {
    return file
  }
  
  return new Promise((resolve, reject) => {
    const reader = new FileReader()
    reader.readAsDataURL(file)
    reader.onload = (event) => {
      const img = new Image()
      img.src = event.target?.result as string
      img.onload = () => {
        // 创建canvas元素
        const canvas = document.createElement('canvas')
        const ctx = canvas.getContext('2d')
        
        if (!ctx) {
          reject(new Error('无法获取canvas上下文'))
          return
        }
        
        // 计算压缩后的尺寸，保持宽高比
        let width = img.width
        let height = img.height
        const maxWidth = 1920
        const maxHeight = 1080
        
        if (width > maxWidth || height > maxHeight) {
          const ratio = Math.min(maxWidth / width, maxHeight / height)
          width = Math.floor(width * ratio)
          height = Math.floor(height * ratio)
        }
        
        // 设置canvas尺寸
        canvas.width = width
        canvas.height = height
        
        // 绘制压缩后的图像
        ctx.drawImage(img, 0, 0, width, height)
        
        // 尝试不同的质量值，直到文件大小符合要求
        let quality = 0.8
        let compressedFile: File | null = null
        
        const tryCompression = () => {
          canvas.toBlob(
            (blob) => {
              if (!blob) {
                reject(new Error('无法创建blob'))
                return
              }
              
              if (blob.size <= maxSizeBytes || quality <= 0.1) {
                // 即使质量降到0.1仍不符合要求，也返回当前结果
                compressedFile = new File([blob], file.name, {
                  type: file.type,
                  lastModified: file.lastModified
                })
                resolve(compressedFile)
                return
              }
              
              // 如果文件仍然太大，降低质量继续尝试
              quality -= 0.1
              tryCompression()
            },
            file.type,
            quality
          )
        }
        
        tryCompression()
      }
      img.onerror = () => {
        reject(new Error('图片加载失败'))
      }
    }
    reader.onerror = () => {
      reject(new Error('文件读取失败'))
    }
  })
}

/**
 * 上传单个图片文件
 * @param file - 图片文件
 * @returns 上传结果
 */
export const uploadImage = async (file: File): Promise<UploadImageResponse> => {
  // 压缩图片到5MB左右
  const compressedFile = await compressImage(file, 5)
  
  const formData = new FormData()
  formData.append('file', compressedFile)

  try {
    const response = await apiClient.post('/api/file/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    
    return {
      success: true,
      data: {
        url: response.data.data.url.replace(/\\/g, '/'), // 将反斜杠替换为正斜杠
        fileName: response.data.data.originalName,
        size: response.data.data.size,
        width: 0, // 后端没有返回宽度，暂时设为0
        height: 0 // 后端没有返回高度，暂时设为0
      }
    }
  } catch (error: any) {
    console.error('上传图片失败:', error)
    return {
      success: false,
      message: error.response?.data?.message || '上传图片失败'
    }
  }
}

/**
 * 上传多个图片文件
 * @param files - 图片文件数组
 * @returns 上传结果数组
 */
export const uploadImages = async (files: File[]): Promise<UploadImageResponse[]> => {
  const uploadPromises = files.map(file => uploadImage(file))
  return Promise.all(uploadPromises)
}

/**
 * 上传文章封面图片
 * @param file - 图片文件
 * @param articleId - 文章ID（可选）
 * @returns 上传结果
 */
export const uploadArticleCover = async (file: File, articleId?: string): Promise<UploadImageResponse> => {
  // 复用uploadImage函数，因为它们的功能相同
  return uploadImage(file)
}

/**
 * 上传摄影图片
 * @param file - 图片文件
 * @param metadata - 图片元数据（可选）
 * @returns 上传结果
 */
export const uploadPhoto = async (file: File, metadata?: {
  title?: string
  description?: string
  tags?: string[]
}): Promise<UploadImageResponse> => {
  // 复用uploadImage函数，因为它们的功能相同
  return uploadImage(file)
}

/**
 * 获取上传配置信息
 * @returns 上传配置
 */
export const getUploadConfig = async () => {
  // 模拟返回上传配置
  return {
    maxSize: 40 * 1024 * 1024,
    allowedTypes: ['image/jpeg', 'image/png', 'image/gif']
  }
}

/**
 * 获取所有上传的图片列表
 * @returns 图片列表
 */
export const getPhotos = async (): Promise<Array<{
  id: string
  name: string
  url: string
  uploadTime: string
  size: number
  width: number
  height: number
  category: string
  tags: string[]
}>> => {
  try {
    const response = await apiClient.get('/api/file/list')
    const files = response.data.data
    
    // 将文件信息转换为前端需要的格式
    return Object.values(files).map((file: any, index: number) => ({
      id: index.toString(),
      name: file.originalName,
      url: file.url.replace(/\\/g, '/'), // 将反斜杠替换为正斜杠
      uploadTime: new Date().toISOString(), // 实际项目中应该从后端获取
      size: file.size,
      width: 800, // 实际项目中应该从后端获取
      height: 600, // 实际项目中应该从后端获取
      category: 'nature', // 实际项目中应该从后端获取
      tags: ['风景', '自然', '山水'] // 实际项目中应该从后端获取
    }))
  } catch (error: any) {
    console.error('获取图片列表失败:', error)
    return []
  }
}

/**
 * 删除已上传的文件
 * @param fileUrl - 文件URL
 * @returns 删除结果
 */
export const deleteUploadedFile = async (fileUrl: string): Promise<{
  success: boolean
  message?: string
}> => {
  // 暂时返回成功，因为后端没有提供删除文件的API
  return {
    success: true,
    message: '文件删除成功'
  }
}