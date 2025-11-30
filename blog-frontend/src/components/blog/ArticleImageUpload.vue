<template>
  <div class="image-upload-container">
    <div class="upload-section">
      <h3>文章封面图片</h3>
      <div class="upload-area" @drop="handleDrop" @dragover.prevent @dragenter.prevent>
        <div class="upload-content">
          <svg width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
            <polyline points="17,8 12,3 7,8"/>
            <line x1="12" y1="3" x2="12" y2="15"/>
          </svg>
          <p class="upload-text">拖拽图片到此处或点击上传</p>
          <p class="upload-hint">支持 JPG、PNG、GIF 格式，最大 40MB</p>
        </div>
        <input
          ref="fileInput"
          type="file"
          accept="image/*"
          @change="handleFileSelect"
          class="file-input"
        />
      </div>
    </div>

    <!-- 图片预览区域 -->
    <div v-if="previewUrl" class="preview-section">
      <h3>封面预览</h3>
      <div class="preview-container">
        <img :src="previewUrl" alt="封面预览" class="preview-image" />
        <button @click="removeImage" class="remove-btn" title="删除图片">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
            <line x1="18" y1="6" x2="6" y2="18"/>
            <line x1="6" y1="6" x2="18" y2="18"/>
          </svg>
        </button>
      </div>
      <div class="image-info">
        <p class="file-name">{{ fileName }}</p>
        <p class="file-size">{{ formatFileSize(fileSize) }}</p>
      </div>
    </div>

    <!-- 随机图片选项 -->
    <div class="random-image-section">
      <h3>或选择随机封面</h3>
      <div class="random-options">
        <button 
          v-for="(image, index) in randomImages" 
          :key="index"
          @click="selectRandomImage(image)"
          class="random-image-btn"
          :class="{ active: selectedRandomImage === image }"
        >
          <img :src="image" alt="随机封面" />
        </button>
      </div>
      <button @click="refreshRandomImages" class="refresh-btn">
        <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <polyline points="23,4 23,10 17,10"/>
          <path d="M20.49 15a9 9 0 1 1-2.12-9.36L23 10"/>
        </svg>
        换一批
      </button>
    </div>

    <!-- 上传状态 -->
    <div v-if="uploading" class="upload-status">
      <div class="upload-progress">
        <div class="progress-bar" :style="{ width: uploadProgress + '%' }"></div>
      </div>
      <p class="status-text">{{ uploadStatus }}</p>
    </div>

    <!-- 错误提示 -->
    <div v-if="error" class="error-message">
      <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
        <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
      </svg>
      {{ error }}
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import apiClient from '@/api/apiClient'

// Props
interface Props {
  modelValue?: string
}

const props = defineProps<Props>()

// Emits
const emit = defineEmits<{
  'update:modelValue': [value: string]
  'image-uploaded': [url: string, fileName: string]
}>()

// 响应式数据
const fileInput = ref<HTMLInputElement>()
const previewUrl = ref<string>('')
const fileName = ref<string>('')
const fileSize = ref<number>(0)
const uploading = ref<boolean>(false)
const uploadProgress = ref<number>(0)
const uploadStatus = ref<string>('')
const error = ref<string>('')
const selectedRandomImage = ref<string>('')

// 随机图片数组
const randomImages = ref<string[]>([])

// 预定义的随机图片模板
const imageTemplates = [
  'https://picsum.photos/800/400?random=1',
  'https://picsum.photos/800/400?random=2',
  'https://picsum.photos/800/400?random=3',
  'https://picsum.photos/800/400?random=4',
  'https://picsum.photos/800/400?random=5',
  'https://picsum.photos/800/400?random=6',
  'https://picsum.photos/800/400?random=7',
  'https://picsum.photos/800/400?random=8',
  'https://picsum.photos/800/400?random=9',
  'https://picsum.photos/800/400?random=10'
]

// 初始化随机图片
const initRandomImages = () => {
  const shuffled = [...imageTemplates].sort(() => 0.5 - Math.random())
  randomImages.value = shuffled.slice(0, 6)
}

// 刷新随机图片
const refreshRandomImages = () => {
  initRandomImages()
}

// 选择随机图片
const selectRandomImage = (image: string) => {
  selectedRandomImage.value = image
  previewUrl.value = image
  fileName.value = '随机封面'
  fileSize.value = 0
  error.value = ''
  emit('update:modelValue', image)
  emit('image-uploaded', image, '随机封面')
}

// 处理文件选择
const handleFileSelect = (event: Event) => {
  const input = event.target as HTMLInputElement
  if (input.files && input.files[0]) {
    handleFile(input.files[0])
  }
}

// 处理拖拽
const handleDrop = (event: DragEvent) => {
  event.preventDefault()
  const files = event.dataTransfer?.files
  if (files && files[0]) {
    handleFile(files[0])
  }
}

// 处理文件上传
const handleFile = (file: File) => {
  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    error.value = '请选择图片文件'
    return
  }

  // 验证文件大小 (40MB)
  if (file.size > 40 * 1024 * 1024) {
    error.value = '图片大小不能超过40MB'
    return
  }

  // 清除错误
  error.value = ''
  
  // 显示预览
  const reader = new FileReader()
  reader.onload = (e) => {
    previewUrl.value = e.target?.result as string
    fileName.value = file.name
    fileSize.value = file.size
    selectedRandomImage.value = ''
    
    // 上传文件前进行压缩
    compressImage(file)
      .then(compressedFile => {
        uploadFile(compressedFile)
      })
      .catch(err => {
        console.error('压缩失败，使用原图上传:', err)
        uploadFile(file)
      })
  }
  reader.readAsDataURL(file)
}

// 上传文件到服务器
  const uploadFile = async (file: File) => {
    uploading.value = true
    uploadProgress.value = 0
    uploadStatus.value = '正在上传...'

    try {
      const formData = new FormData()
      formData.append('file', file)

      // 模拟上传进度
      const progressInterval = setInterval(() => {
        if (uploadProgress.value < 90) {
          uploadProgress.value += 10
        }
      }, 200)

      const response = await apiClient.post('/api/file/upload', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      })

      clearInterval(progressInterval)
      uploadProgress.value = 100

      // 直接使用response.data，因为后端返回的是ApiResponse格式
      const fileUrl = response.data.data.url
      uploadStatus.value = '上传成功！'
      emit('update:modelValue', fileUrl)
      emit('image-uploaded', fileUrl, file.name)
      
      setTimeout(() => {
        uploading.value = false
        uploadProgress.value = 0
      }, 1000)
    } catch (err: any) {
      console.error('上传失败:', err)
      error.value = err.response?.data?.message || err.message || '上传失败，请重试'
      uploading.value = false
      uploadProgress.value = 0
    }
  }

// 删除图片
const removeImage = () => {
  previewUrl.value = ''
  fileName.value = ''
  fileSize.value = 0
  selectedRandomImage.value = ''
  emit('update:modelValue', '')
}

// 格式化文件大小
const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 图片压缩函数
const compressImage = (file: File): Promise<File> => {
  return new Promise((resolve, reject) => {
    // 如果文件小于1MB，不压缩
    if (file.size < 1 * 1024 * 1024) {
      resolve(file)
      return
    }
    
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
        
        // 将canvas转换为blob
        canvas.toBlob(
          (blob) => {
            if (!blob) {
              reject(new Error('无法创建blob'))
              return
            }
            
            // 创建压缩后的文件
            const compressedFile = new File([blob], file.name, {
              type: file.type,
              lastModified: file.lastModified
            })
            
            resolve(compressedFile)
          },
          file.type,
          0.8 // 压缩质量，0-1之间
        )
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

// 监听modelValue变化
watch(() => props.modelValue, (newValue) => {
  if (newValue && !newValue.startsWith('https://picsum.photos/')) {
    previewUrl.value = newValue
    fileName.value = '已上传图片'
    fileSize.value = 0
  } else if (newValue && newValue.startsWith('https://picsum.photos/')) {
    selectRandomImage(newValue)
  }
})

// 初始化
onMounted(() => {
  initRandomImages()
  if (props.modelValue) {
    if (props.modelValue.startsWith('https://picsum.photos/')) {
      selectRandomImage(props.modelValue)
    } else {
      previewUrl.value = props.modelValue
      fileName.value = '已上传图片'
    }
  }
})
</script>

<style scoped>
/* 深色主题支持 */
[data-theme="dark"] .image-upload-container {
  background: var(--background-secondary);
}

[data-theme="dark"] .upload-section h3,
[data-theme="dark"] .preview-section h3,
[data-theme="dark"] .random-image-section h3 {
  color: var(--text-color-primary);
}

[data-theme="dark"] .upload-area {
  background: var(--background-glass);
  border: 2px dashed var(--border-color-base);
}

[data-theme="dark"] .upload-content svg {
  color: var(--primary-color);
}

[data-theme="dark"] .upload-text {
  color: var(--text-color-primary);
}

[data-theme="dark"] .upload-hint {
  color: var(--text-color-light);
}

[data-theme="dark"] .preview-container {
  background: var(--background-secondary);
  border: 2px solid var(--border-color-base);
}

[data-theme="dark"] .remove-btn {
  background: rgba(239, 68, 68, 0.9);
  color: white;
}

[data-theme="dark"] .remove-btn:hover {
  background: #dc2626;
}

[data-theme="dark"] .image-info {
  background: var(--background-glass);
  border: 1px solid var(--border-color-base);
}

[data-theme="dark"] .file-name {
  color: var(--text-color-primary);
}

[data-theme="dark"] .file-size {
  color: var(--text-color-light);
}

[data-theme="dark"] .random-image-btn {
  background: var(--background-glass);
  border: 2px solid var(--border-color-base);
}

[data-theme="dark"] .random-image-btn:hover {
  border-color: var(--primary-color);
}

[data-theme="dark"] .random-image-btn.active {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(129, 140, 248, 0.3);
}

[data-theme="dark"] .refresh-btn {
  background: var(--primary-color);
  color: white;
}

[data-theme="dark"] .refresh-btn:hover {
  background: var(--primary-color-dark);
}

[data-theme="dark"] .upload-status {
  background: var(--background-glass);
  border: 1px solid var(--border-color-base);
}

[data-theme="dark"] .progress-bar {
  background: linear-gradient(135deg, var(--primary-color), var(--primary-color-dark));
}

[data-theme="dark"] .status-text {
  color: var(--text-color-secondary);
}

[data-theme="dark"] .error-message {
  background: linear-gradient(135deg, rgba(239, 68, 68, 0.1), rgba(220, 38, 38, 0.1));
  color: #f87171;
  border: 2px solid rgba(239, 68, 68, 0.3);
}

/* 基础样式 */
.image-upload-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 24px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(0, 0, 0, 0.1);
}

.upload-section h3,
.preview-section h3,
.random-image-section h3 {
  margin: 0 0 16px 0;
  font-size: 1.2em;
  font-weight: 600;
  color: var(--text-color-primary);
}

.upload-area {
  position: relative;
  padding: 40px;
  border: 2px dashed var(--border-color-base);
  border-radius: 12px;
  text-align: center;
  transition: all 0.3s ease;
  cursor: pointer;
  background: rgba(255, 255, 255, 0.5);
}

.upload-area:hover {
  border-color: var(--primary-color);
  background: rgba(102, 126, 234, 0.05);
}

.upload-content svg {
  color: var(--primary-color);
  margin-bottom: 12px;
}

.upload-text {
  font-size: 1.1em;
  font-weight: 600;
  color: var(--text-color-primary);
  margin-bottom: 8px;
}

.upload-hint {
  font-size: 0.9em;
  color: var(--text-color-light);
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

.preview-section {
  margin-top: 24px;
}

.preview-container {
  position: relative;
  display: inline-block;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.preview-image {
  max-width: 300px;
  max-height: 200px;
  object-fit: cover;
  border-radius: 12px;
}

.remove-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  background: rgba(239, 68, 68, 0.9);
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
  backdrop-filter: blur(4px);
}

.remove-btn:hover {
  background: #dc2626;
  transform: scale(1.1);
}

.image-info {
  margin-top: 12px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 8px;
  border: 1px solid var(--border-color-light);
}

.file-name {
  font-weight: 600;
  color: var(--text-color-primary);
  margin-bottom: 4px;
}

.file-size {
  font-size: 0.9em;
  color: var(--text-color-light);
}

.random-image-section {
  margin-top: 24px;
}

.random-options {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(120px, 1fr));
  gap: 12px;
  margin-bottom: 16px;
}

.random-image-btn {
  position: relative;
  padding: 0;
  border: 2px solid var(--border-color-base);
  border-radius: 8px;
  overflow: hidden;
  background: none;
  cursor: pointer;
  transition: all 0.3s ease;
  aspect-ratio: 4/3;
}

.random-image-btn img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.random-image-btn:hover {
  border-color: var(--primary-color);
  transform: translateY(-2px);
}

.random-image-btn.active {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.3);
}

.refresh-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background: var(--primary-color);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.9em;
  font-weight: 600;
  transition: all 0.3s ease;
}

.refresh-btn:hover {
  background: var(--primary-color-dark);
  transform: translateY(-1px);
}

.upload-status {
  margin-top: 16px;
  padding: 16px;
  background: rgba(255, 255, 255, 0.7);
  border-radius: 8px;
  border: 1px solid var(--border-color-light);
}

.upload-progress {
  width: 100%;
  height: 4px;
  background: var(--border-color-light);
  border-radius: 2px;
  overflow: hidden;
  margin-bottom: 8px;
}

.progress-bar {
  height: 100%;
  background: linear-gradient(135deg, var(--primary-color), var(--primary-color-dark));
  border-radius: 2px;
  transition: width 0.3s ease;
}

.status-text {
  font-size: 0.9em;
  color: var(--text-color-secondary);
  text-align: center;
}

.error-message {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-top: 16px;
  padding: 12px 16px;
  background: linear-gradient(135deg, #fff5f5, #fed7d7);
  color: #e53e3e;
  border-radius: 8px;
  border: 1px solid #feb2b2;
  font-size: 0.9em;
}
</style>