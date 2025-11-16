<!-- src/views/BlogArticles.vue -->
<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useArticles } from '@/composables/useArticles'
import { useCategories } from '@/composables/useCategories'
import {type Article, ArticleStatus} from '@/types/article'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'
import {type ApiResponse, getArticlesByCategories} from "@/types/api.ts"
import { searchArticles } from "@/api/articleService.ts";
import apiClient from "@/api/apiClient.ts";

const { articles, loading, error, fetchArticles, fetchPublishedArticles } = useArticles()
const { categories, fetchCategories } = useCategories()
const authStore = useAuthStore()
const router = useRouter()

// 分类筛选相关
const showCategoryFilter = ref(false)
const selectedCategoryIds = ref<string[]>([]) // 修复1: 统一使用 string[] 类型

// 搜索相关
const searchKeyword = ref('')

// 组件挂载时获取已发布的文章列表和分类
onMounted(() => {
  fetchPublishedArticles()
  fetchCategories()
})

// 格式化日期显示
const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('zh-CN')
}

// 切换分类选择
const toggleCategory = (categoryId: string) => { // 修复2: 参数类型改为 string
  const index = selectedCategoryIds.value.indexOf(categoryId)
  if (index > -1) {
    selectedCategoryIds.value.splice(index, 1)
  } else {
    selectedCategoryIds.value.push(categoryId)
  }
  // 重新加载文章
  loadArticles()
}

// 加载文章
const loadArticles = async () => {
  try {
    loading.value = true
    error.value = null

    // 如果有搜索关键词，执行搜索
    if (searchKeyword.value.trim()) {
      const response = await searchArticles(searchKeyword.value.trim())
      if (response.code === 200) {
        articles.value = response.data
      } else {
        error.value = response.message
      }
      return
    }

    // 如果有选中的分类，则使用分类筛选api
    if (selectedCategoryIds.value.length > 0) {
      // 直接使用字符串数组，无需转换
      const response = await getArticlesByCategories(selectedCategoryIds.value)
      if (response.code === 200) {
        articles.value = response.data
      } else {
        error.value = response.message
      }
      return
    }

    // 默认加载已发布的文章
    await fetchPublishedArticles()
  } catch (err: any) {
    error.value = err.message || '加载文章失败'
  } finally {
    loading.value = false
  }
}

// 搜索文章，添加分类筛选功能
const searchArticlesWithFilters = async (params: any) => {
  try {
    // 如果同时有搜索关键词和分类，则需要后端API支持
    // 这里假设有一个API可以同时处理搜索和分类筛选
    const response = await apiClient.get<ApiResponse<Article[]>>('/api/article/searchWithFilters', { params })
    return response.data
  } catch (err) {
    console.error('搜索文章失败:', err)
    throw err
  }
}

// 搜索文章 - 修复4: 添加搜索功能
const searchArticlesByKeyword = async () => {
  await loadArticles()
}

// 清除搜索
const clearSearch = async () => {
  searchKeyword.value = ''
  selectedCategoryIds.value = []
  await loadArticles()
}

// 清除分类筛选
const clearCategories = () => {
  selectedCategoryIds.value = []
  // 不再特殊处理搜索关键词
  loadArticles()
}

// 应用分类筛选
const applyCategoryFilter = async () => {
  // 清除搜索关键词
  searchKeyword.value = ''
  // 如果有选中的分类，则根据分类筛选文章
  if (selectedCategoryIds.value.length > 0) {
    try {
      loading.value = true
      error.value = null

      // 直接使用字符串数组，无需转换
      const response = await getArticlesByCategories(selectedCategoryIds.value)
      if (response.code === 200) {
        articles.value = response.data
      } else {
        error.value = response.message
      }
    } catch (err: any) {
      error.value = err.message || '获取文章列表失败'
    } finally {
      loading.value = false
    }
  } else {
    // 如果没有选中分类，获取所有已发布文章
    await fetchPublishedArticles()
  }
  showCategoryFilter.value = false
}

// 计算分类文章数量
const categoryArticleCount = computed(() => {
  const count: Record<string, number> = {}
  articles.value.forEach(article => {
    if (article.categories) {
      article.categories.forEach(category => {
        count[category.id] = (count[category.id] || 0) + 1
      })
    }
  })
  return count
})

// 获取文章预览文本
const getArticlePreview = (content: string, length = 200) => {
  // 移除HTML标签并截取预览文本
  const cleanContent = content.replace(/<[^>]*>/g, '').replace(/\n/g, ' ')
  return cleanContent.length > length ? cleanContent.substring(0, length) + '...' : cleanContent
}

// 为字符串添加哈希方法以生成随机封面
const hashCode = (str: string): number => {
  let hash = 0, i, chr;
  for (i = 0; i < str.length; i++) {
    chr   = str.charCodeAt(i);
    hash  = ((hash << 5) - hash) + chr;
    hash |= 0; // Convert to 32bit integer
  }
  return Math.abs(hash);
}

// 模拟获取文章封面图
const getArticleCover = (articleId: string) => {
  // 在实际项目中，这里应该从文章内容或数据库中获取实际的封面图
  // 现在使用模拟的封面图
  const coverImages = [
    'https://picsum.photos/600/300?random=1',
    'https://picsum.photos/600/300?random=2',
    'https://picsum.photos/600/300?random=3',
    'https://picsum.photos/600/300?random=4',
    'https://picsum.photos/600/300?random=5',
  ]
  return coverImages[hashCode(articleId) % coverImages.length]
}

// 图片加载错误处理
const handleImageError = (event: Event) => {
  const target = event.target as HTMLImageElement;
  target.src = 'https://via.placeholder.com/600x300/42b983/ffffff?text=No+Image';
}

// 选择分类并加载该分类下的文章
const selectCategory = async (categoryId: string) => {
  selectedCategoryIds.value = [categoryId];
  await loadArticles();
}

// 显示所有文章
const showAllArticles = async () => {
  selectedCategoryIds.value = [];
  searchKeyword.value = '';
  await fetchPublishedArticles();
}
</script>

<template>
  <div class="articles-container">
    <div class="header">
      <h2>文章列表</h2>
      <!-- 只有登录后才显示创建文章按钮 -->
      <div v-if="authStore.isAuthenticated" class="header-actions">
        <!-- 搜索框 - 修复5: 添加搜索功能 -->
        <div class="search-container">
          <input
            v-model="searchKeyword"
            type="text"
            placeholder="搜索文章..."
            class="search-input"
            @keyup.enter="searchArticlesByKeyword"
          />
          <button @click="searchArticlesByKeyword" class="search-btn">搜索</button>
          <button v-if="searchKeyword || selectedCategoryIds.length > 0"
                  @click="clearSearch"
                  class="clear-search-btn">
            清除
          </button>
        </div>

        <button
          class="filter-btn"
          @click="showCategoryFilter = !showCategoryFilter"
        >
          分类筛选
        </button>
        <router-link to="/article/create" class="create-btn">创建文章</router-link>
      </div>
    </div>

    <!-- 分类展示栏 -->
    <div class="category-bar">
      <h3>分类</h3>
      <div class="category-list">
        <button 
          @click="showAllArticles" 
          :class="['category-item', { active: selectedCategoryIds.length === 0 }]"
        >
          全部
        </button>
        <button
          v-for="category in categories"
          :key="category.id"
          :class="['category-item', { active: selectedCategoryIds.includes(category.id) }]"
          @click="selectCategory(category.id)"
        >
          {{ category.name }}
          <span class="category-count">({{ categoryArticleCount[category.id] || 0 }})</span>
        </button>
      </div>
    </div>

    <!-- 分类筛选弹窗 -->
    <div v-if="showCategoryFilter" class="category-filter-overlay" @click="showCategoryFilter = false">
      <div class="category-filter-popup" @click.stop>
        <div class="filter-header">
          <h3>按分类筛选</h3>
          <button class="close-btn" @click="showCategoryFilter = false">×</button>
        </div>

        <div class="category-list">
          <div
            v-for="category in categories"
            :key="category.id"
            :class="['category-item', { active: selectedCategoryIds.includes(category.id) }]"
            @click="toggleCategory(category.id)"
          >
            {{ category.name }}
            <span class="category-count">({{ categoryArticleCount[category.id] || 0 }})</span>
          </div>
        </div>

        <div class="filter-actions">
          <button @click="clearCategories" class="clear-btn">清除</button>
          <button @click="applyCategoryFilter" class="apply-btn">应用</button>
        </div>
      </div>
    </div>

    <div v-if="loading" class="loading">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>

    <div v-else-if="error" class="error">
      <div class="error-icon">⚠️</div>
      <p>错误: {{ error }}</p>
    </div>

    <div v-else-if="articles.length === 0" class="no-articles">
      <div class="no-articles-icon">📝</div>
      <p>暂无文章</p>
    </div>

    <div v-else class="articles-grid">
      <div v-for="article in articles" :key="article.id" class="article-card">
        <!-- 文章封面图 -->
        <div class="article-cover">
          <img :src="getArticleCover(article.id)" :alt="article.title" @error="handleImageError" />
          <div class="article-status-badge" v-if="article.status === ArticleStatus.DRAFT">
            草稿
          </div>
        </div>
        
        <!-- 文章内容 -->
        <div class="article-content">
          <div class="article-header">
            <h3 class="article-title">
              <router-link :to="`/article/${article.id}`">{{ article.title }}</router-link>
            </h3>
            <div class="article-categories">
              <span 
                v-for="category in article.categories" 
                :key="category.id" 
                class="category-tag"
              >
                {{ category.name }}
              </span>
            </div>
          </div>
          
          <p class="article-meta">
            <span class="author">作者: {{ article.author }}</span>
            <span class="date">发布时间: {{ formatDate(article.createTime) }}</span>
          </p>
          
          <div class="article-preview">
            {{ getArticlePreview(article.content) }}
          </div>
          
          <div class="article-actions">
            <router-link :to="`/article/${article.id}`" class="btn-read-more">阅读更多</router-link>
            <!-- 只有登录后才显示编辑按钮，且只能编辑自己创建的草稿 -->
            <router-link
              v-if="authStore.isAuthenticated && article.status === ArticleStatus.DRAFT"
              :to="`/article/edit/${article.id}`"
              class="btn-edit">
              编辑
            </router-link>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.articles-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  position: relative;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: 100vh;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  flex-wrap: wrap;
  gap: 15px;
  background: rgba(255, 255, 255, 0.8);
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  backdrop-filter: blur(10px);
}

.header h2 {
  margin: 0;
  font-size: 2.2rem;
  color: #2c3e50;
  font-weight: 600;
}

/* 分类展示栏 */
.category-bar {
  background: rgba(255, 255, 255, 0.8);
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
  backdrop-filter: blur(10px);
  margin-bottom: 30px;
}

.category-bar h3 {
  margin: 0 0 15px 0;
  font-size: 1.4rem;
  color: #2c3e50;
}

.category-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.category-item {
  padding: 10px 18px;
  border: 2px solid #e9ecef;
  border-radius: 24px;
  cursor: pointer;
  background-color: white;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 15px;
  font-weight: 500;
}

.category-item:hover {
  border-color: #42b983;
  background-color: #f8fff7;
  transform: translateY(-2px);
}

.category-item.active {
  background-color: #42b983;
  color: white;
  border-color: #42b983;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(66, 185, 131, 0.3);
}

.category-count {
  font-size: 0.8em;
  opacity: 0.8;
}

/* 搜索框样式 - 修复6: 添加搜索框样式 */
.search-container {
  display: flex;
  gap: 8px;
  align-items: center;
  flex: 1;
  max-width: 400px;
}

.search-input {
  padding: 12px 16px;
  border: 2px solid #e0e0e0;
  border-radius: 25px;
  font-size: 15px;
  width: 100%;
  transition: all 0.3s ease;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.search-input:focus {
  outline: none;
  border-color: #42b983;
  box-shadow: 0 2px 12px rgba(66, 185, 131, 0.2);
  transform: translateY(-2px);
}

.search-btn, .clear-search-btn {
  padding: 10px 16px;
  border-radius: 20px;
  cursor: pointer;
  border: none;
  font-size: 14px;
  transition: all 0.3s ease;
  font-weight: 500;
}

.search-btn {
  background: linear-gradient(135deg, #42b983 0%, #359c6d 100%);
  color: white;
  box-shadow: 0 4px 12px rgba(66, 185, 131, 0.3);
}

.search-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(66, 185, 131, 0.4);
}

.clear-search-btn {
  background: linear-gradient(135deg, #6c757d 0%, #5a6268 100%);
  color: white;
}

.clear-search-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(108, 117, 125, 0.3);
}

.filter-btn, .create-btn {
  padding: 10px 20px;
  border-radius: 25px;
  cursor: pointer;
  text-decoration: none;
  display: inline-block;
  font-weight: 500;
  transition: all 0.3s ease;
  border: none;
  font-size: 15px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.filter-btn {
  background: linear-gradient(135deg, #6c757d 0%, #5a6268 100%);
  color: white;
}

.filter-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(108, 117, 125, 0.3);
}

.create-btn {
  background: linear-gradient(135deg, #42b983 0%, #359c6d 100%);
  color: white;
}

.create-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(66, 185, 131, 0.3);
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 5px solid #e0e0e0;
  border-top: 5px solid #42b983;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 15px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  text-align: center;
  background: rgba(229, 57, 53, 0.1);
  border-radius: 12px;
  color: #e53935;
}

.error-icon {
  font-size: 3rem;
  margin-bottom: 15px;
}

.no-articles {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
  background: rgba(108, 117, 125, 0.05);
  border-radius: 12px;
  color: #6c757d;
}

.no-articles-icon {
  font-size: 4rem;
  margin-bottom: 15px;
}

.articles-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 25px;
  padding: 10px 0;
}

.article-card {
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.12);
  transition: all 0.3s ease;
  display: flex;
  flex-direction: column;
  height: 100%;
  border: 1px solid rgba(0, 0, 0, 0.05);
}

.article-card:hover {
  transform: translateY(-10px);
  box-shadow: 0 16px 40px rgba(0, 0, 0, 0.18);
}

.article-cover {
  position: relative;
  height: 180px;
  overflow: hidden;
}

.article-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.article-card:hover .article-cover img {
  transform: scale(1.05);
}

.article-status-badge {
  position: absolute;
  top: 15px;
  right: 15px;
  background: rgba(255, 193, 7, 0.9);
  color: #212529;
  padding: 5px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
  z-index: 2;
}

.article-content {
  padding: 20px;
  flex: 1;
  display: flex;
  flex-direction: column;
}

.article-header {
  margin-bottom: 15px;
}

.article-title {
  margin: 0 0 12px 0;
  font-size: 1.4rem;
  color: #2c3e50;
  line-height: 1.4;
}

.article-title a {
  color: inherit;
  text-decoration: none;
  transition: color 0.3s ease;
}

.article-title a:hover {
  color: #42b983;
  text-decoration: none;
}

.article-categories {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 10px;
}

.category-tag {
  background: #e8f4f1;
  color: #42b983;
  padding: 4px 10px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 500;
}

.article-meta {
  margin: 0 0 15px 0;
  color: #6c757d;
  font-size: 0.9rem;
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap;
  gap: 8px;
}

.article-meta .author,
.article-meta .date {
  display: flex;
  align-items: center;
}

.article-meta .author::before,
.article-meta .date::before {
  content: "•";
  margin: 0 6px;
  color: #42b983;
}

.article-meta .author:first-child::before,
.article-meta .date:first-child::before {
  display: none;
}

.article-preview {
  margin-bottom: 20px;
  color: #495057;
  line-height: 1.6;
  flex: 1;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 4;
  -webkit-box-orient: vertical;
}

.article-actions {
  display: flex;
  justify-content: space-between;
  gap: 10px;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

.btn-read-more,
.btn-edit {
  padding: 10px 18px;
  border-radius: 22px;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s ease;
  font-weight: 500;
  border: none;
  cursor: pointer;
  flex: 1;
  text-align: center;
}

.btn-read-more {
  background: transparent;
  color: #42b983;
  border: 2px solid #42b983;
}

.btn-read-more:hover {
  background: #42b983;
  color: white;
  transform: translateY(-2px);
}

.btn-edit {
  background: #ffc107;
  color: #212529;
  border: 2px solid #ffc107;
}

.btn-edit:hover {
  background: #e0a800;
  color: white;
  transform: translateY(-2px);
}

/* 分类筛选弹窗样式 */
.category-filter-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.6);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 10000;
  backdrop-filter: blur(5px);
}

.category-filter-popup {
  background: white;
  border-radius: 16px;
  padding: 25px;
  width: 90%;
  max-width: 500px;
  max-height: 80vh;
  overflow-y: auto;
  box-shadow: 0 25px 50px rgba(0, 0, 0, 0.25);
  animation: popupSlideIn 0.3s ease-out;
}

@keyframes popupSlideIn {
  from {
    opacity: 0;
    transform: translateY(-20px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.filter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}

.filter-header h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 1.4rem;
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

.category-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  margin-bottom: 25px;
}

.category-item {
  padding: 10px 18px;
  border: 2px solid #e9ecef;
  border-radius: 24px;
  cursor: pointer;
  background-color: white;
  transition: all 0.2s ease;
  display: flex;
  align-items: center;
  gap: 5px;
}

.category-item:hover {
  border-color: #42b983;
  background-color: #f8fff7;
  transform: translateY(-2px);
}

.category-item.active {
  background-color: #42b983;
  color: white;
  border-color: #42b983;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(66, 185, 131, 0.3);
}

.category-count {
  font-size: 0.8em;
  opacity: 0.8;
}

.filter-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 15px;
  border-top: 1px solid #eee;
}

.clear-btn, .apply-btn {
  padding: 10px 24px;
  border-radius: 25px;
  cursor: pointer;
  border: 2px solid;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.clear-btn {
  background-color: white;
  color: #212529;
  border-color: #ddd;
}

.clear-btn:hover {
  background-color: #f8f9fa;
  border-color: #adb5bd;
  transform: translateY(-2px);
}

.apply-btn {
  background-color: #42b983;
  color: white;
  border-color: #42b983;
}

.apply-btn:hover {
  background-color: #359c6d;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(66, 185, 131, 0.3);
}

/* 响应式设计 */
@media (max-width: 1024px) {
  .articles-grid {
    grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
    gap: 20px;
  }
}

@media (max-width: 768px) {
  .articles-container {
    padding: 15px;
  }
  
  .header {
    flex-direction: column;
    align-items: stretch;
    gap: 15px;
  }

  .header-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
  }

  .search-container {
    flex: 1;
    min-width: 100%;
    max-width: 100%;
  }
  
  .header h2 {
    font-size: 1.8rem;
  }
  
  .category-bar {
    padding: 15px;
  }
  
  .category-list {
    gap: 8px;
  }
  
  .category-item {
    padding: 8px 14px;
    font-size: 14px;
  }
  
  .articles-grid {
    grid-template-columns: 1fr;
    gap: 20px;
  }
  
  .article-card {
    height: auto;
  }
  
  .article-meta {
    flex-direction: column;
    gap: 5px;
  }
  
  .article-actions {
    flex-direction: column;
  }
  
  .btn-read-more,
  .btn-edit {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .articles-container {
    padding: 10px;
  }
  
  .header {
    padding: 15px;
  }
  
  .category-bar {
    padding: 15px;
  }
  
  .article-title {
    font-size: 1.2rem;
  }
  
  .category-list {
    flex-direction: column;
  }
  
  .category-item {
    width: 100%;
    justify-content: space-between;
  }
}
</style>