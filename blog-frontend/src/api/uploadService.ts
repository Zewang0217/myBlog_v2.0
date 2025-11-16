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
 * 上传单个图片文件
 * @param file - 图片文件
 * @returns 上传结果
 */
export const uploadImage = async (file: File): Promise<UploadImageResponse> => {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('type', 'image')

  try {
    const response = await apiClient.post('/api/upload/image', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    
    return {
      success: true,
      data: response.data
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
  const formData = new FormData()
  formData.append('file', file)
  formData.append('type', 'article_cover')
  if (articleId) {
    formData.append('articleId', articleId)
  }

  try {
    const response = await apiClient.post('/api/upload/article-cover', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    
    return {
      success: true,
      data: response.data
    }
  } catch (error: any) {
    console.error('上传文章封面失败:', error)
    return {
      success: false,
      message: error.response?.data?.message || '上传文章封面失败'
    }
  }
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
  const formData = new FormData()
  formData.append('file', file)
  formData.append('type', 'photo')
  
  if (metadata) {
    if (metadata.title) {
      formData.append('title', metadata.title)
    }
    if (metadata.description) {
      formData.append('description', metadata.description)
    }
    if (metadata.tags && metadata.tags.length > 0) {
      formData.append('tags', metadata.tags.join(','))
    }
  }

  try {
    const response = await apiClient.post('/api/upload/photo', formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })
    
    return {
      success: true,
      data: response.data
    }
  } catch (error: any) {
    console.error('上传摄影图片失败:', error)
    return {
      success: false,
      message: error.response?.data?.message || '上传摄影图片失败'
    }
  }
}

/**
 * 获取上传配置信息
 * @returns 上传配置
 */
export const getUploadConfig = async () => {
  try {
    const response = await apiClient.get('/api/upload/config')
    return response.data
  } catch (error: any) {
    console.error('获取上传配置失败:', error)
    throw error
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
  try {
    const response = await apiClient.delete('/api/upload/file', {
      params: { fileUrl }
    })
    
    return {
      success: true,
      message: response.data.message
    }
  } catch (error: any) {
    console.error('删除文件失败:', error)
    return {
      success: false,
      message: error.response?.data?.message || '删除文件失败'
    }
  }
}