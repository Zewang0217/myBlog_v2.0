<template>
  <div class="photo-gallery">
    <div class="gallery-header">
      <h1>摄影相册</h1>
      <p class="gallery-description">记录美好瞬间，分享摄影作品</p>
      
      <div class="gallery-actions">
        <button 
          v-if="authStore.isAuthenticated" 
          @click="showUploadModal = true"
          class="btn-upload"
        >
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"/>
            <polyline points="17,8 12,3 7,8"/>
            <line x1="12" y1="3" x2="12" y2="15"/>
          </svg>
          上传图片
        </button>
        <button @click="refreshPhotos" class="btn-refresh">
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <path d="M23 4v6h-6"/>
            <path d="M1 20v-6h6"/>
            <path d="M3.51 9a9 9 0 0 1 14.85-3.36L23 10M1 14l4.64 4.36A9 9 0 0 0 20.49 15"/>
          </svg>
          刷新
        </button>
      </div>
    </div>

    <!-- 筛选和排序 -->
    <div class="filter-section">
      <div class="filter-controls">
        <!-- 搜索框 -->
        <div class="search-box">
          <svg class="search-icon" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor">
            <circle cx="11" cy="11" r="8"/>
            <line x1="21" y1="21" x2="16.65" y2="16.65"/>
          </svg>
          <input 
            v-model="searchQuery" 
            type="text" 
            placeholder="搜索图片名称..." 
            class="search-input"
          />
        </div>
        
        <!-- 分类筛选 -->
        <select v-model="selectedCategory" class="category-select">
          <option value="all">所有分类</option>
          <option value="nature">自然风光</option>
          <option value="portrait">人物肖像</option>
          <option value="city">城市建筑</option>
          <option value="street">街头摄影</option>
          <option value="other">其他</option>
        </select>
        
        <!-- 排序 -->
        <select v-model="sortBy" class="sort-select">
          <option value="newest">最新上传</option>
          <option value="oldest">最早上传</option>
          <option value="name">按名称</option>
          <option value="size">按大小</option>
        </select>
        
        <div class="view-toggle">
          <button 
            :class="['view-btn', { active: viewMode === 'grid' }]"
            @click="viewMode = 'grid'"
          >
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <rect x="3" y="3" width="7" height="7"/>
              <rect x="14" y="3" width="7" height="7"/>
              <rect x="14" y="14" width="7" height="7"/>
              <rect x="3" y="14" width="7" height="7"/>
            </svg>
          </button>
          <button 
            :class="['view-btn', { active: viewMode === 'masonry' }]"
            @click="viewMode = 'masonry'"
          >
            <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor">
              <rect x="3" y="3" width="18" height="8"/>
              <rect x="3" y="13" width="18" height="8"/>
            </svg>
          </button>
        </div>
      </div>
      
      <!-- 标签筛选 -->
      <div class="tag-filter" v-if="availableTags.length > 0">
        <span class="filter-label">标签筛选:</span>
        <div class="tag-list">
          <button 
            v-for="tag in availableTags" 
            :key="tag"
            :class="['tag-item', { active: selectedTags.includes(tag) }]"
            @click="toggleTag(tag)"
          >
            {{ tag }}
          </button>
        </div>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading">
      <div class="loading-spinner"></div>
      <p>正在加载图片...</p>
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="error">
      <div class="error-icon">⚠️</div>
      <p>{{ error }}</p>
      <button @click="loadPhotos" class="btn-retry">重试</button>
    </div>

    <!-- 空状态 -->
    <div v-else-if="sortedPhotos.length === 0" class="empty">
      <div class="empty-icon">📷</div>
      <h3>还没有上传任何图片</h3>
      <p>点击上方"上传图片"按钮开始分享您的摄影作品</p>
    </div>

    <!-- 图片网格 -->
    <div v-else :class="['photo-grid', viewMode]">
      <div 
        v-for="(photo, index) in sortedPhotos" 
        :key="photo.id"
        :class="['photo-item', { large: index % 5 === 0 }]"
        @click="openPhotoModal(photo)"
      >
        <img :src="photo.url" :alt="photo.name" class="photo-image" />
        <div class="photo-overlay">
          <div class="photo-info">
            <h4 class="photo-title">{{ photo.name }}</h4>
            <p class="photo-meta">{{ formatDate(photo.uploadTime) }}</p>
          </div>
          <div class="photo-actions">
            <button 
              v-if="authStore.isAuthenticated"
              @click.stop="deletePhoto(photo.id)"
              class="btn-delete"
              title="删除图片"
            >
              <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor">
                <polyline points="3,6 5,6 21,6"/>
                <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"/>
                <line x1="10" y1="11" x2="10" y2="17"/>
                <line x1="14" y1="11" x2="14" y2="17"/>
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 图片详情模态框 -->
    <div v-if="selectedPhoto" class="photo-modal" @click="closePhotoModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3 class="photo-title">{{ selectedPhoto.name }}</h3>
          <button @click="closePhotoModal" class="close-btn">×</button>
        </div>
        <div class="modal-body">
          <img :src="selectedPhoto.url" :alt="selectedPhoto.name" class="modal-image" />
          <div class="photo-details">
            <div class="detail-item">
              <span class="detail-label">上传时间:</span>
              <span class="detail-value">{{ formatDate(selectedPhoto.uploadTime) }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">文件大小:</span>
              <span class="detail-value">{{ formatFileSize(selectedPhoto.size) }}</span>
            </div>
            <div class="detail-item">
              <span class="detail-label">尺寸:</span>
              <span class="detail-value">{{ selectedPhoto.width }} × {{ selectedPhoto.height }}</span>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="closePhotoModal" class="btn-close">关闭</button>
        </div>
      </div>
    </div>

    <!-- 上传模态框 -->
    <div v-if="showUploadModal" class="upload-modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3>上传摄影图片</h3>
          <button @click="showUploadModal = false" class="close-btn">×</button>
        </div>
        <div class="modal-body">
          <PhotoUpload 
            :max-photos="20"
            :max-size="100"
            @photos-uploaded="handlePhotosUploaded"
            @upload-error="handleUploadError"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useAuthStore } from '@/stores/auth'
import PhotoUpload from '@/components/blog/PhotoUpload.vue'
import { getPhotos } from '@/api/uploadService'

// 定义图片接口
interface Photo {
  id: string
  name: string
  url: string
  uploadTime: string
  size: number
  width: number
  height: number
  category: string
  tags: string[]
}

// 状态管理
const authStore = useAuthStore()
const photos = ref<Photo[]>([])
const loading = ref(false)
const error = ref('')
const selectedPhoto = ref<Photo | null>(null)
const showUploadModal = ref(false)
const sortBy = ref('newest')
const viewMode = ref<'grid' | 'masonry'>('grid')
const searchQuery = ref('')
const selectedCategory = ref('all')
const selectedTags = ref<string[]>([])

// 计算属性 - 可用标签
const availableTags = computed(() => {
  const tags = new Set<string>()
  photos.value.forEach(photo => {
    photo.tags.forEach(tag => tags.add(tag))
  })
  return Array.from(tags)
})

// 计算属性 - 筛选和排序后的图片
const sortedPhotos = computed(() => {
  let filtered = [...photos.value]
  
  // 搜索筛选
  if (searchQuery.value.trim()) {
    const query = searchQuery.value.toLowerCase().trim()
    filtered = filtered.filter(photo => 
      photo.name.toLowerCase().includes(query)
    )
  }
  
  // 分类筛选
  if (selectedCategory.value !== 'all') {
    filtered = filtered.filter(photo => 
      photo.category === selectedCategory.value
    )
  }
  
  // 标签筛选
  if (selectedTags.value.length > 0) {
    filtered = filtered.filter(photo => 
      selectedTags.value.every(tag => photo.tags.includes(tag))
    )
  }
  
  // 排序
  switch (sortBy.value) {
    case 'newest':
      return filtered.sort((a, b) => new Date(b.uploadTime).getTime() - new Date(a.uploadTime).getTime())
    case 'oldest':
      return filtered.sort((a, b) => new Date(a.uploadTime).getTime() - new Date(b.uploadTime).getTime())
    case 'name':
      return filtered.sort((a, b) => a.name.localeCompare(b.name))
    case 'size':
      return filtered.sort((a, b) => b.size - a.size)
    default:
      return filtered
  }
})

// 切换标签选择
const toggleTag = (tag: string) => {
  const index = selectedTags.value.indexOf(tag)
  if (index > -1) {
    selectedTags.value.splice(index, 1)
  } else {
    selectedTags.value.push(tag)
  }
}

// 加载图片数据
const loadPhotos = async () => {
  loading.value = true
  error.value = ''
  
  try {
    // 调用真实的API获取图片列表
    const photosList = await getPhotos()
    photos.value = photosList
    loading.value = false
  } catch (err) {
    error.value = '加载图片失败，请稍后重试'
    loading.value = false
  }
}

// 刷新图片
const refreshPhotos = () => {
  loadPhotos()
}

// 删除图片
const deletePhoto = async (photoId: string) => {
  if (!confirm('确定要删除这张图片吗？')) {
    return
  }
  
  try {
    // 模拟API调用 - 实际项目中应该调用真实的API
    // await deletePhoto(photoId)
    photos.value = photos.value.filter(photo => photo.id !== photoId)
  } catch (err) {
    alert('删除图片失败，请稍后重试')
  }
}

// 打开图片详情模态框
const openPhotoModal = (photo: Photo) => {
  selectedPhoto.value = photo
}

// 关闭图片详情模态框
const closePhotoModal = () => {
  selectedPhoto.value = null
}

// 处理图片上传完成
const handlePhotosUploaded = (uploadedPhotos: Array<{url: string, name: string, category: string}>) => {
  showUploadModal.value = false
  
  // 将新上传的图片添加到列表
  const newPhotos: Photo[] = uploadedPhotos.map((photo, index) => {
    // 根据分类自动生成标签
    let tags: string[] = []
    switch (photo.category) {
      case 'nature':
        tags = ['风景', '自然', '山水']
        break
      case 'portrait':
        tags = ['人物', '肖像', '人像']
        break
      case 'city':
        tags = ['城市', '建筑', '现代']
        break
      case 'street':
        tags = ['街头', '人文', '生活']
        break
      default:
        tags = ['其他']
    }
    
    return {
      id: Date.now().toString() + index,
      name: photo.name,
      url: photo.url,
      uploadTime: new Date().toISOString(),
      size: 0, // 实际项目中应该从上传响应中获取
      width: 800, // 实际项目中应该从上传响应中获取
      height: 600, // 实际项目中应该从上传响应中获取
      category: photo.category,
      tags: tags
    }
  })
  
  photos.value.unshift(...newPhotos)
  
  // 显示成功消息
  alert(`成功上传 ${uploadedPhotos.length} 张图片！`)
}

// 处理上传错误
const handleUploadError = (error: string) => {
  alert(`上传失败: ${error}`)
}

// 格式化日期
const formatDate = (dateString: string): string => {
  return new Date(dateString).toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    hour: '2-digit',
    minute: '2-digit'
  })
}

// 格式化文件大小
const formatFileSize = (bytes: number): string => {
  if (bytes === 0) return '0 Bytes'
  const k = 1024
  const sizes = ['Bytes', 'KB', 'MB', 'GB']
  const i = Math.floor(Math.log(bytes) / Math.log(k))
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i]
}

// 组件挂载时加载数据
onMounted(() => {
  loadPhotos()
})
</script>

<style scoped>
.photo-gallery {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  transition: background 0.3s ease;
}

[data-theme="dark"] .photo-gallery {
  background: linear-gradient(135deg, #1a1a1a 0%, #2d2d2d 100%);
}

.gallery-header {
  text-align: center;
  margin-bottom: 40px;
  padding: 30px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

[data-theme="dark"] .gallery-header {
  background: var(--background-glass);
  border: 1px solid var(--border-color-base);
}

.gallery-header h1 {
  margin: 0 0 10px 0;
  font-size: 2.5rem;
  color: #2c3e50;
  font-weight: 700;
  transition: color 0.3s ease;
}

[data-theme="dark"] .gallery-header h1 {
  color: var(--text-color-primary);
}

.gallery-description {
  margin: 0 0 30px 0;
  color: #6c757d;
  font-size: 1.1rem;
  transition: color 0.3s ease;
}

[data-theme="dark"] .gallery-description {
  color: var(--text-color-secondary);
}

.gallery-actions {
  display: flex;
  justify-content: center;
  gap: 15px;
  flex-wrap: wrap;
}

.btn-upload,
.btn-refresh {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border: none;
  border-radius: 25px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 600;
  transition: all 0.3s ease;
}

.btn-upload {
  background: linear-gradient(135deg, #42b983, #359c6d);
  color: white;
  box-shadow: 0 4px 12px rgba(66, 185, 131, 0.3);
}

.btn-upload:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(66, 185, 131, 0.4);
}

.btn-refresh {
  background: white;
  color: #6c757d;
  border: 2px solid #e9ecef;
}

.btn-refresh:hover {
  background: #f8f9fa;
  border-color: #42b983;
  color: #42b983;
}

[data-theme="dark"] .btn-refresh {
  background: var(--background-primary);
  color: var(--text-color-secondary);
  border-color: var(--border-color-base);
}

[data-theme="dark"] .btn-refresh:hover {
  background: var(--background-secondary);
  border-color: var(--primary-color-light);
  color: var(--primary-color-light);
}

.filter-section {
  margin-bottom: 30px;
  padding: 20px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  transition: all 0.3s ease;
}

[data-theme="dark"] .filter-section {
  background: var(--background-glass);
  border: 1px solid var(--border-color-base);
}

.filter-controls {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 20px;
}

/* 搜索框样式 */
.search-box {
  position: relative;
  flex: 1;
  min-width: 250px;
}

.search-input {
  width: 100%;
  padding: 12px 40px 12px 18px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  font-size: 1rem;
  background: white;
  color: #495057;
  transition: all 0.3s ease;
}

.search-input:focus {
  outline: none;
  border-color: #42b983;
  box-shadow: 0 0 0 3px rgba(66, 185, 131, 0.1);
}

.search-icon {
  position: absolute;
  right: 15px;
  top: 50%;
  transform: translateY(-50%);
  color: #999;
  pointer-events: none;
}

/* 分类筛选样式 */
.category-select {
  padding: 12px 18px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  background: white;
  color: #495057;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 500;
  min-width: 150px;
}

.category-select:focus {
  outline: none;
  border-color: #42b983;
  box-shadow: 0 0 0 3px rgba(66, 185, 131, 0.1);
}

.category-select option {
  background: white;
  color: #495057;
  padding: 10px;
  font-weight: 500;
}

/* 标签筛选样式 */
.tag-filter {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.filter-label {
  display: block;
  margin-bottom: 10px;
  font-weight: 600;
  color: #495057;
  font-size: 0.95rem;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.tag-item {
  padding: 8px 16px;
  border: 2px solid #e9ecef;
  background: white;
  color: #495057;
  border-radius: 20px;
  cursor: pointer;
  font-size: 0.9rem;
  font-weight: 500;
  transition: all 0.3s ease;
}

.tag-item:hover {
  border-color: #42b983;
  color: #42b983;
}

.tag-item.active {
  background: #42b983;
  color: white;
  border-color: #42b983;
}

/* 深色主题样式 */
[data-theme="dark"] .search-input {
  background: var(--background-primary);
  border-color: var(--border-color-base);
  color: var(--text-color-primary);
}

[data-theme="dark"] .search-input:focus {
  border-color: var(--primary-color-light);
  box-shadow: 0 0 0 3px rgba(129, 140, 248, 0.3);
}

[data-theme="dark"] .search-icon {
  color: var(--text-color-secondary);
}

/* 深色主题下的下拉列表样式 */
[data-theme="dark"] .category-select,
[data-theme="dark"] .sort-select {
  background: var(--background-primary);
  border-color: var(--border-color-base);
  color: var(--text-color-primary);
}

[data-theme="dark"] .category-select:focus,
[data-theme="dark"] .sort-select:focus {
  border-color: var(--primary-color-light);
  box-shadow: 0 0 0 3px rgba(129, 140, 248, 0.3);
}

[data-theme="dark"] .category-select option,
[data-theme="dark"] .sort-select option {
  background: var(--background-primary);
  color: var(--text-color-primary);
  border: none;
  padding: 10px;
}

[data-theme="dark"] .category-select option:hover,
[data-theme="dark"] .sort-select option:hover {
  background: var(--background-secondary);
}

[data-theme="dark"] .category-select option:checked,
[data-theme="dark"] .sort-select option:checked {
  background: var(--primary-color);
  color: white;
}

[data-theme="dark"] .category-select {
  background: var(--background-primary);
  border-color: var(--border-color-base);
  color: var(--text-color-primary);
}

[data-theme="dark"] .category-select:focus {
  border-color: var(--primary-color-light);
  box-shadow: 0 0 0 3px rgba(129, 140, 248, 0.3);
}

[data-theme="dark"] .category-select option {
  background: var(--background-primary);
  color: var(--text-color-primary);
}

[data-theme="dark"] .tag-filter {
  border-top-color: var(--border-color-base);
}

[data-theme="dark"] .filter-label {
  color: var(--text-color-primary);
}

[data-theme="dark"] .tag-item {
  background: var(--background-primary);
  border-color: var(--border-color-base);
  color: var(--text-color-primary);
}

[data-theme="dark"] .tag-item:hover {
  border-color: var(--primary-color-light);
  color: var(--primary-color-light);
}

[data-theme="dark"] .tag-item.active {
  background: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}

.sort-select {
  padding: 12px 18px;
  border: 2px solid #e9ecef;
  border-radius: 8px;
  background: white;
  color: #495057;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 500;
  min-width: 150px;
}

.sort-select:focus {
  outline: none;
  border-color: #42b983;
  box-shadow: 0 0 0 3px rgba(66, 185, 131, 0.1);
}

.sort-select option {
  background: white;
  color: #495057;
  padding: 10px;
  font-weight: 500;
}

[data-theme="dark"] .sort-select {
  background: var(--background-primary);
  border-color: var(--border-color-base);
  color: var(--text-color-primary);
}

[data-theme="dark"] .sort-select:focus {
  box-shadow: 0 0 0 3px rgba(129, 140, 248, 0.3);
}

[data-theme="dark"] .sort-select option {
  background: var(--background-primary);
  color: var(--text-color-primary);
  border: none;
}

[data-theme="dark"] .sort-select option:hover {
  background: var(--background-secondary);
}

[data-theme="dark"] .sort-select option:checked {
  background: var(--primary-color);
  color: white;
}

.view-toggle {
  display: flex;
  gap: 5px;
}

.view-btn {
  padding: 10px;
  border: 2px solid #e9ecef;
  background: white;
  color: #6c757d;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.view-btn.active {
  background: #42b983;
  color: white;
  border-color: #42b983;
}

.view-btn:hover:not(.active) {
  border-color: #42b983;
  color: #42b983;
}

[data-theme="dark"] .view-btn {
  background: var(--background-primary);
  border-color: var(--border-color-base);
  color: var(--text-color-secondary);
}

[data-theme="dark"] .view-btn.active {
  background: var(--primary-color);
  border-color: var(--primary-color);
  color: white;
}

.loading,
.error,
.empty {
  text-align: center;
  padding: 60px 20px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  backdrop-filter: blur(10px);
  margin: 40px 0;
  transition: all 0.3s ease;
}

[data-theme="dark"] .loading,
[data-theme="dark"] .error,
[data-theme="dark"] .empty {
  background: var(--background-glass);
  border: 1px solid var(--border-color-base);
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 5px solid #e0e0e0;
  border-top: 5px solid #42b983;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-icon,
.empty-icon {
  font-size: 4rem;
  margin-bottom: 20px;
}

.error {
  color: #e53935;
}

.empty {
  color: #6c757d;
}

.empty h3 {
  margin: 0 0 10px 0;
  color: #2c3e50;
  transition: color 0.3s ease;
}

[data-theme="dark"] .empty h3 {
  color: var(--text-color-primary);
}

.btn-retry {
  padding: 10px 20px;
  background: #42b983;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1rem;
  margin-top: 15px;
  transition: all 0.3s ease;
}

.btn-retry:hover {
  background: #359c6d;
}

.photo-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
  margin-top: 30px;
}

.photo-grid.masonry {
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  grid-auto-rows: 200px;
}

.photo-item {
  position: relative;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
  background: white;
}

.photo-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
}

[data-theme="dark"] .photo-item {
  background: var(--background-primary);
  border: 1px solid var(--border-color-base);
}

.photo-item.large {
  grid-column: span 2;
  grid-row: span 2;
}

.masonry .photo-item:nth-child(3n+1) {
  grid-row: span 2;
}

.masonry .photo-item:nth-child(4n+2) {
  grid-column: span 2;
}

.photo-image {
  width: 100%;
  height: 250px;
  object-fit: cover;
  transition: transform 0.3s ease;
}

.photo-item.large .photo-image {
  height: 520px;
}

.masonry .photo-image {
  height: 100%;
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
  background: linear-gradient(to bottom, transparent 60%, rgba(0, 0, 0, 0.8));
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
  padding: 20px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.photo-item:hover .photo-overlay {
  opacity: 1;
}

.photo-info {
  color: white;
}

.photo-title {
  margin: 0 0 5px 0;
  font-size: 1.1rem;
  font-weight: 600;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.photo-meta {
  margin: 0;
  font-size: 0.9rem;
  opacity: 0.9;
}

.photo-actions {
  position: absolute;
  top: 10px;
  right: 10px;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.photo-item:hover .photo-actions {
  opacity: 1;
}

.btn-delete {
  background: rgba(220, 53, 69, 0.9);
  color: white;
  border: none;
  border-radius: 50%;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-delete:hover {
  background: #c82333;
  transform: scale(1.1);
}

/* 模态框样式 */
.photo-modal,
.upload-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(5px);
}

.modal-content {
  background: white;
  border-radius: 16px;
  max-width: 90vw;
  max-height: 90vh;
  overflow: hidden;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.3);
  animation: modalSlideIn 0.3s ease-out;
}

[data-theme="dark"] .modal-content {
  background: var(--background-primary);
  border: 1px solid var(--border-color-base);
}

@keyframes modalSlideIn {
  from {
    opacity: 0;
    transform: translateY(-20px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #eee;
}

[data-theme="dark"] .modal-header {
  border-bottom-color: var(--border-color-base);
}

.modal-header h3 {
  margin: 0;
  color: #2c3e50;
  transition: color 0.3s ease;
}

[data-theme="dark"] .modal-header h3 {
  color: var(--text-color-primary);
}

.close-btn {
  background: none;
  border: none;
  font-size: 28px;
  cursor: pointer;
  color: #999;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: all 0.3s ease;
}

.close-btn:hover {
  background: #f8f9fa;
  color: #495057;
}

[data-theme="dark"] .close-btn:hover {
  background: var(--background-secondary);
  color: var(--text-color-primary);
}

.modal-body {
  padding: 24px;
  max-height: 60vh;
  overflow-y: auto;
}

.modal-image {
  width: 100%;
  max-width: 800px;
  height: auto;
  border-radius: 8px;
  margin-bottom: 20px;
}

.photo-details {
  background: #f8f9fa;
  padding: 20px;
  border-radius: 8px;
}

[data-theme="dark"] .photo-details {
  background: var(--background-secondary);
}

.detail-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.detail-item:last-child {
  margin-bottom: 0;
}

.detail-label {
  font-weight: 600;
  color: #495057;
  transition: color 0.3s ease;
}

[data-theme="dark"] .detail-label {
  color: var(--text-color-primary);
}

.detail-value {
  color: #6c757d;
  transition: color 0.3s ease;
}

[data-theme="dark"] .detail-value {
  color: var(--text-color-secondary);
}

.modal-footer {
  padding: 20px 24px;
  border-top: 1px solid #eee;
  display: flex;
  justify-content: flex-end;
}

[data-theme="dark"] .modal-footer {
  border-top-color: var(--border-color-base);
}

.btn-close {
  padding: 10px 20px;
  background: #6c757d;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.btn-close:hover {
  background: #5a6268;
}

@media (max-width: 768px) {
  .photo-gallery {
    padding: 15px;
  }
  
  .gallery-header {
    padding: 20px;
  }
  
  .gallery-header h1 {
    font-size: 2rem;
  }
  
  .filter-controls {
    flex-direction: column;
    align-items: stretch;
  }
  
  .photo-grid {
    grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
    gap: 15px;
  }
  
  .photo-item.large {
    grid-column: span 1;
    grid-row: span 1;
  }
  
  .modal-content {
    margin: 20px;
    max-width: calc(100vw - 40px);
    max-height: calc(100vh - 40px);
  }
}
</style>