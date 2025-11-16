<template>
  <div class="photo-upload-container">
    <div class="upload-header">
      <h3>摄影图片上传</h3>
      <p class="upload-description">分享您的摄影作品，支持批量上传</p>
    </div>

    <!-- 上传区域 -->
    <div class="upload-area" @drop="handleDrop" @dragover.prevent @dragenter.prevent>
      <div class="upload-content">
        <svg width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
          <polyline points="17,8 12,3 7,8"/>
          <line x1="12" y1="3" x2="12" y2="15"/>
        </svg>
        <p class="upload-text">拖拽图片到此处或点击选择</p>
        <p class="upload-hint">支持 JPG、PNG、GIF、WebP 格式，单张最大 10MB</p>
        <p class="upload-hint">最多可上传 20 张图片</p>
      </div>
      <input
        ref="fileInput"
        type="file"
        accept="image/*"
        multiple
        @change="handleFileSelect"
        class="file-input"
      />
    </div>

    <!-- 上传进度 -->
    <div v-if="uploading" class="upload-progress">
      <div class="progress-bar">
        <div class="progress-fill" :style="{ width: totalProgress + '%' }"></div>
      </div>
      <p class="progress-text">{{ uploadStatus }} ({{ uploadedCount }}/{{ totalCount }})</p>
    </div>

    <!-- 图片预览网格 -->
    <div v-if="photoPreviews.length > 0" class="photo-grid">
      <div v-for="(photo, index) in photoPreviews" :key="index" class="photo-item">
        <div class="photo-container">
          <img :src="photo.previewUrl" :alt="photo.name" class="photo-image" />
          <div class="photo-overlay">
            <button @click="removePhoto(index)" class="remove-btn" title="删除图片">
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
                <line x1="18" y1="6" x2="6" y2="18"/>
                <line x1="6" y1="6" x2="18" y2="18"/>
              </svg>
            </button>
          </div>
        </div>
        <div class="photo-info">
          <p class="photo-name">{{ photo.name }}</p>
          <p class="photo-size">{{ formatFileSize(photo.size) }}</p>
        </div>
      </div>
    </div>

    <!-- 操作按钮 -->
    <div v-if="photoPreviews.length > 0" class="action-buttons">
      <button @click="clearAll" class="btn-clear">清空所有</button>
      <button @click="uploadPhotos" :disabled="uploading" class="btn-upload">
        {{ uploading ? '上传中...' : '开始上传' }}
      </button>
    </div>

    <!-- 错误信息 -->
    <div v-if="error" class="error-message">
      <div class="error-icon">⚠️</div>
      <p>{{ error }}</p>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { uploadImage } from '@/api/uploadService'

// 定义props和emits
const props = defineProps<{
  maxPhotos?: number
  maxSize?: number // MB
}>()

const emit = defineEmits<{
  'photos-uploaded': [photos: Array<{url: string, name: string}>]
  'upload-error': [error: string]
}>()

// 响应式数据
const fileInput = ref<HTMLInputElement>()
const photoPreviews = ref<Array<{
  file: File
  previewUrl: string
  name: string
  size: number
}>>([])
const uploading = ref(false)
const uploadProgress = ref<number[]>([])
const uploadStatus = ref('')
const error = ref('')

// 配置
const maxPhotos = props.maxPhotos || 20
const maxSize = (props.maxSize || 10) * 1024 * 1024 // 转换为字节

// 计算属性
const totalCount = computed(() => photoPreviews.value.length)
const uploadedCount = computed(() => uploadProgress.value.filter(p => p === 100).length)
const totalProgress = computed(() => {
  if (photoPreviews.value.length === 0) return 0
  const total = uploadProgress.value.reduce((sum, progress) => sum + progress, 0)
  return Math.round(total / photoPreviews.value.length)
})

// 文件选择处理
const handleFileSelect = (event: Event) => {
  const target = event.target as HTMLInputElement
  if (target.files) {
    handleFiles(Array.from(target.files))
  }
}

// 拖拽处理
const handleDrop = (event: DragEvent) => {
  event.preventDefault()
  const files = event.dataTransfer?.files
  if (files) {
    handleFiles(Array.from(files))
  }
}

// 处理文件
const handleFiles = (files: File[]) => {
  error.value = ''
  
  // 验证数量
  if (photoPreviews.value.length + files.length > maxPhotos) {
    error.value = `最多只能上传 ${maxPhotos} 张图片`
    return
  }

  // 过滤和验证文件
  const validFiles = files.filter(file => {
    // 检查文件类型
    if (!file.type.startsWith('image/')) {
      error.value = `${file.name} 不是图片文件`
      return false
    }
    
    // 检查文件大小
    if (file.size > maxSize) {
      error.value = `${file.name} 文件过大，最大支持 ${maxSize / (1024 * 1024)}MB`
      return false
    }
    
    return true
  })

  // 添加到预览列表
  validFiles.forEach(file => {
    const reader = new FileReader()
    reader.onload = (e) => {
      photoPreviews.value.push({
        file,
        previewUrl: e.target?.result as string,
        name: file.name,
        size: file.size
      })
      uploadProgress.value.push(0)
    }
    reader.readAsDataURL(file)
  })
}

// 删除图片
const removePhoto = (index: number) => {
  photoPreviews.value.splice(index, 1)
  uploadProgress.value.splice(index, 1)
}

// 清空所有
const clearAll = () => {
  photoPreviews.value = []
  uploadProgress.value = []
  error.value = ''
}

// 格式化文件大小
const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 上传图片
const uploadPhotos = async () => {
  if (photoPreviews.value.length === 0) {
    error.value = '请先选择要上传的图片'
    return
  }

  uploading.value = true
  error.value = ''
  uploadStatus.value = '准备上传...'

  try {
    const uploadedPhotos: Array<{url: string, name: string}> = []
    
    for (let i = 0; i < photoPreviews.value.length; i++) {
      const photo = photoPreviews.value[i]
      uploadStatus.value = `正在上传: ${photo.name}`
      
      try {
        // 模拟上传进度
        uploadProgress.value[i] = 0
        const progressInterval = setInterval(() => {
          if (uploadProgress.value[i] < 90) {
            uploadProgress.value[i] += 10
          }
        }, 100)

        // 实际上传
        const response = await uploadImage(photo.file)
        
        clearInterval(progressInterval)
        uploadProgress.value[i] = 100
        
        if (response.success && response.data) {
          uploadedPhotos.push({
            url: response.data.url,
            name: photo.name
          })
        } else {
          throw new Error(response.message || '上传失败')
        }
      } catch (err) {
        uploadProgress.value[i] = 0
        throw new Error(`上传 ${photo.name} 失败: ${err}`)
      }
    }

    uploadStatus.value = '上传完成！'
    emit('photos-uploaded', uploadedPhotos)
    
    // 清空已上传的图片
    setTimeout(() => {
      clearAll()
    }, 2000)
    
  } catch (err: any) {
    error.value = err.message || '上传失败'
    emit('upload-error', error.value)
  } finally {
    uploading.value = false
    uploadStatus.value = ''
  }
}
</script>

<style scoped>
.photo-upload-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 16px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

[data-theme="dark"] .photo-upload-container {
  background: var(--background-glass);
  border: 1px solid var(--border-color-base);
}

.upload-header {
  text-align: center;
  margin-bottom: 30px;
}

.upload-header h3 {
  margin: 0 0 10px 0;
  font-size: 1.8rem;
  color: #2c3e50;
  transition: color 0.3s ease;
}

[data-theme="dark"] .upload-header h3 {
  color: var(--text-color-primary);
}

.upload-description {
  margin: 0;
  color: #6c757d;
  font-size: 1rem;
  transition: color 0.3s ease;
}

[data-theme="dark"] .upload-description {
  color: var(--text-color-secondary);
}

.upload-area {
  position: relative;
  border: 3px dashed #42b983;
  border-radius: 16px;
  padding: 40px 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s ease;
  background: rgba(66, 185, 131, 0.05);
}

.upload-area:hover {
  border-color: #359c6d;
  background: rgba(66, 185, 131, 0.1);
}

[data-theme="dark"] .upload-area {
  border-color: var(--primary-color-light);
  background: rgba(66, 185, 131, 0.1);
}

.upload-content svg {
  margin-bottom: 15px;
  color: #42b983;
}

.upload-text {
  font-size: 1.2rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 10px 0;
  transition: color 0.3s ease;
}

[data-theme="dark"] .upload-text {
  color: var(--text-color-primary);
}

.upload-hint {
  font-size: 0.9rem;
  color: #6c757d;
  margin: 5px 0;
  transition: color 0.3s ease;
}

[data-theme="dark"] .upload-hint {
  color: var(--text-color-secondary);
}

.file-input {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  cursor: pointer;
}

.upload-progress {
  margin: 20px 0;
  text-align: center;
}

.progress-bar {
  width: 100%;
  height: 8px;
  background: #e9ecef;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 10px;
}

.progress-fill {
  height: 100%;
  background: linear-gradient(135deg, #42b983, #359c6d);
  border-radius: 4px;
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 0.9rem;
  color: #6c757d;
  margin: 0;
  transition: color 0.3s ease;
}

[data-theme="dark"] .progress-text {
  color: var(--text-color-secondary);
}

.photo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 20px;
  margin: 30px 0;
}

.photo-item {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.photo-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

[data-theme="dark"] .photo-item {
  background: var(--background-primary);
  border: 1px solid var(--border-color-base);
}

.photo-container {
  position: relative;
  aspect-ratio: 1;
  overflow: hidden;
}

.photo-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.photo-item:hover .photo-image {
  transform: scale(1.05);
}

.photo-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: flex-start;
  justify-content: flex-end;
  padding: 10px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.photo-container:hover .photo-overlay {
  opacity: 1;
}

.remove-btn {
  background: rgba(220, 53, 69, 0.9);
  color: white;
  border: none;
  border-radius: 50%;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.remove-btn:hover {
  background: #c82333;
  transform: scale(1.1);
}

.photo-info {
  padding: 15px;
}

.photo-name {
  font-size: 0.9rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 5px 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  transition: color 0.3s ease;
}

[data-theme="dark"] .photo-name {
  color: var(--text-color-primary);
}

.photo-size {
  font-size: 0.8rem;
  color: #6c757d;
  margin: 0;
  transition: color 0.3s ease;
}

[data-theme="dark"] .photo-size {
  color: var(--text-color-secondary);
}

.action-buttons {
  display: flex;
  justify-content: space-between;
  gap: 15px;
  margin-top: 20px;
}

.btn-clear,
.btn-upload {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 600;
  transition: all 0.3s ease;
  flex: 1;
}

.btn-clear {
  background: #6c757d;
  color: white;
}

.btn-clear:hover {
  background: #5a6268;
  transform: translateY(-2px);
}

.btn-upload {
  background: linear-gradient(135deg, #42b983, #359c6d);
  color: white;
  box-shadow: 0 4px 12px rgba(66, 185, 131, 0.3);
}

.btn-upload:hover:not(:disabled) {
  background: #359c6d;
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(66, 185, 131, 0.4);
}

.btn-upload:disabled {
  background: #a0a0a0;
  cursor: not-allowed;
  box-shadow: none;
}

.error-message {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 15px;
  background: rgba(220, 53, 69, 0.1);
  border: 1px solid rgba(220, 53, 69, 0.3);
  border-radius: 8px;
  color: #dc3545;
  margin-top: 20px;
  transition: all 0.3s ease;
}

[data-theme="dark"] .error-message {
  background: rgba(220, 53, 69, 0.2);
  color: #ff6b6b;
}

.error-icon {
  font-size: 1.2rem;
}

@media (max-width: 768px) {
  .photo-upload-container {
    padding: 15px;
  }
  
  .photo-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 15px;
  }
  
  .action-buttons {
    flex-direction: column;
  }
}
</style>