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
          </div>
        </div>

        <div class="filter-actions">
          <button @click="clearCategories" class="clear-btn">清除</button>
          <button @click="applyCategoryFilter" class="apply-btn">应用</button>
        </div>
      </div>
    </div>

    <div v-if="loading" class="loading">
      加载中...
    </div>

    <div v-else-if="error" class="error">
      错误: {{ error }}
    </div>

    <div v-else-if="articles.length === 0" class="no-articles">
      暂无文章
    </div>

    <div v-else class="articles-list">
      <div v-for="article in articles" :key="article.id" class="article-item">
        <h3 class="article-title">
          <router-link :to="`/article/${article.id}`">{{ article.title }}</router-link>
        </h3>
        <p class="article-meta">
          作者: {{ article.author }} | 发布时间: {{ formatDate(article.createTime) }}
        </p>
        <div class="article-preview">
          {{ article.content.substring(0, 150) }}{{ article.content.length > 150 ? '...' : '' }}
        </div>
        <div class="article-actions">
          <router-link :to="`/article/${article.id}`" class="btn-view">阅读更多</router-link>
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
</template>

<style scoped>
.articles-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
  position: relative;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  flex-wrap: wrap;
  gap: 10px;
}

.header h2 {
  margin: 0;
}

/* 搜索框样式 - 修复6: 添加搜索框样式 */
.search-container {
  display: flex;
  gap: 5px;
  align-items: center;
}

.search-input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 14px;
}

.search-btn, .clear-search-btn {
  padding: 8px 12px;
  border-radius: 4px;
  cursor: pointer;
  border: none;
  font-size: 14px;
}

.search-btn {
  background-color: #42b983;
  color: white;
}

.clear-search-btn {
  background-color: #6c757d;
  color: white;
}

.filter-btn, .create-btn {
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  text-decoration: none;
  display: inline-block;
  margin-left: 10px;
}

.filter-btn {
  background-color: #6c757d;
  color: white;
  border: none;
}

.filter-btn:hover {
  background-color: #5a6268;
}

.create-btn {
  background-color: #42b983;
  color: white;
}

.create-btn:hover {
  background-color: #359c6d;
}

.loading, .error, .no-articles {
  text-align: center;
  padding: 40px;
}

.error {
  color: #e53935;
}

.articles-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.article-item {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 20px;
  background-color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.article-title {
  margin: 0 0 10px 0;
  font-size: 1.5em;
  color: #333;
}

.article-title a {
  color: inherit;
  text-decoration: none;
}

.article-title a:hover {
  color: #42b983;
  text-decoration: underline;
}

.article-meta {
  margin: 0 0 15px 0;
  color: #666;
  font-size: 0.9em;
}

.article-preview {
  margin-bottom: 15px;
  color: #444;
  line-height: 1.6;
}

.article-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding-top: 15px;
  border-top: 1px solid #f0f0f0;
}

.btn-view,
.btn-edit {
  padding: 6px 12px;
  border-radius: 4px;
  text-decoration: none;
  font-size: 14px;
  transition: background-color 0.2s;
}

.btn-view {
  background-color: #409eff;
  color: white;
}

.btn-view:hover {
  background-color: #3082e0;
}

.btn-edit {
  background-color: #ffc107;
  color: #212529;
}

.btn-edit:hover {
  background-color: #e0a800;
}

/* 分类筛选弹窗样式 */
.category-filter-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.category-filter-popup {
  background: white;
  border-radius: 8px;
  padding: 20px;
  width: 90%;
  max-width: 500px;
  max-height: 80vh;
  overflow-y: auto;
}

.filter-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.filter-header h3 {
  margin: 0;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #999;
}

.category-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-bottom: 20px;
}

.category-item {
  padding: 8px 16px;
  border: 1px solid #ddd;
  border-radius: 20px;
  cursor: pointer;
  background-color: #f8f9fa;
  transition: all 0.2s;
}

.category-item:hover {
  background-color: #e9ecef;
}

.category-item.active {
  background-color: #42b983;
  color: white;
  border-color: #42b983;
}

.filter-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.clear-btn, .apply-btn {
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
  border: none;
}

.clear-btn {
  background-color: #f8f9fa;
  color: #212529;
  border: 1px solid #ddd;
}

.clear-btn:hover {
  background-color: #e9ecef;
}

.apply-btn {
  background-color: #42b983;
  color: white;
}

.apply-btn:hover {
  background-color: #359c6d;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .header {
    flex-direction: column;
    align-items: stretch;
  }

  .header-actions {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;
  }

  .search-container {
    flex: 1;
    min-width: 100%;
  }
}
</style>
