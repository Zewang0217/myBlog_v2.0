<!-- src/components/blog/ArticleSearch.vue -->
<script setup lang="ts">
import { ref, computed } from 'vue'
import { useArticles } from '@/composables/useArticles'
import { useRouter } from 'vue-router'
import { marked } from 'marked'
import DOMPurify from 'dompurify'
import type {Article} from "@/types/article.ts";

const { articles, loading, error, fetchPublishedArticles } = useArticles()
const router = useRouter()

// 搜索相关状态
const searchKeyword = ref('')
const searchResults = ref<Article[]>([])
const isSearching = ref(false)

// 获取所有文章用于搜索
const loadArticlesForSearch = async () => {
  await fetchPublishedArticles()
  searchResults.value = articles.value
}

// 搜索文章函数
const searchArticleList = async () => {
  if (!searchKeyword.value.trim()) {
    searchResults.value = articles.value
    return
  }

  isSearching.value = true
  try {
    // 这里应该调用后端搜索API，目前使用前端过滤作为示例
    const keyword = searchKeyword.value.toLowerCase().trim()
    searchResults.value = articles.value.filter(article =>
      article.title.toLowerCase().includes(keyword) ||
      article.content.toLowerCase().includes(keyword) ||
      article.author.toLowerCase().includes(keyword)
    )
  } catch (err: any) {
    console.error('搜索失败:', err)
    error.value = err.message || '搜索失败'
  } finally {
    isSearching.value = false
  }
}

// 清除搜索
const clearSearch = () => {
  searchKeyword.value = ''
  searchResults.value = articles.value
}

// 格式化日期
const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('zh-CN')
}

// 渲染预览内容
const renderPreview = (content: string, keyword: string) => {
  if (!content) return ''

  // 截取包含关键词的部分内容
  let previewText = content
  if (keyword) {
    const index = content.toLowerCase().indexOf(keyword.toLowerCase())
    if (index > -1) {
      const start = Math.max(0, index - 50)
      const end = Math.min(content.length, index + keyword.length + 150)
      previewText = (start > 0 ? '...' : '') +
        content.substring(start, end) +
        (end < content.length ? '...' : '')
    } else {
      previewText = content.length > 200 ? content.substring(0, 200) + '...' : content
    }
  } else {
    previewText = content.length > 200 ? content.substring(0, 200) + '...' : content
  }

  const html = marked(previewText)
  return DOMPurify.sanitize(html)
}

// 查看文章详情
const viewArticle = (id: string) => {
  router.push(`/article/${id}`)
}

// 初始化加载文章
loadArticlesForSearch()
</script>

<template>
  <div class="article-search">
    <div class="search-header">
      <h2>文章搜索</h2>

      <div class="search-box">
        <input
          v-model="searchKeyword"
          type="text"
          placeholder="输入关键词搜索文章..."
          class="search-input"
          @keyup.enter="searchArticleList"
        />
        <button
          @click="searchArticleList"
          :disabled="isSearching"
          class="search-button"
        >
          {{ isSearching ? '搜索中...' : '搜索' }}
        </button>
        <button
          v-if="searchKeyword"
          @click="clearSearch"
          class="clear-button"
        >
          清除
        </button>
      </div>
    </div>

    <div v-if="loading || isSearching" class="loading">
      加载中...
    </div>

    <div v-else-if="error" class="error">
      错误: {{ error }}
    </div>

    <div v-else class="search-results">
      <div class="results-info">
        找到 {{ searchResults.length }} 篇文章
      </div>

      <div
        v-for="article in searchResults"
        :key="article.id"
        class="article-item"
      >
        <h3 class="article-title">
          <a @click="viewArticle(article.id)" href="javascript:void(0)">
            {{ article.title }}
          </a>
        </h3>

        <p class="article-meta">
          作者: {{ article.author }} |
          发布时间: {{ formatDate(article.createTime) }}
        </p>

        <div
          class="article-preview"
          v-html="renderPreview(article.content, searchKeyword)"
        ></div>

        <div class="article-actions">
          <button @click="viewArticle(article.id)" class="view-btn">
            阅读更多
          </button>
        </div>
      </div>

      <div v-if="searchResults.length === 0" class="no-results">
        未找到相关文章
      </div>
    </div>
  </div>
</template>

<style scoped>
.article-search {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.search-header {
  margin-bottom: 30px;
}

.search-header h2 {
  margin-top: 0;
  margin-bottom: 20px;
}

.search-box {
  display: flex;
  gap: 10px;
  margin-bottom: 20px;
}

.search-input {
  flex: 1;
  padding: 10px 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

.search-button,
.clear-button {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

.search-button {
  background-color: #409eff;
  color: white;
}

.search-button:hover:not(:disabled) {
  background-color: #337ecc;
}

.search-button:disabled {
  background-color: #a0cfff;
  cursor: not-allowed;
}

.clear-button {
  background-color: #f56c6c;
  color: white;
}

.clear-button:hover {
  background-color: #e45656;
}

.loading,
.error {
  text-align: center;
  padding: 40px;
}

.loading {
  color: #999;
}

.error {
  color: #f56c6c;
}

.results-info {
  margin-bottom: 20px;
  color: #666;
  font-size: 14px;
}

.article-item {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 20px;
  margin-bottom: 20px;
  background-color: white;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.article-title {
  margin: 0 0 10px 0;
  font-size: 1.5em;
}

.article-title a {
  color: #333;
  text-decoration: none;
}

.article-title a:hover {
  color: #409eff;
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

.article-preview :deep(.highlight) {
  background-color: yellow;
  font-weight: bold;
}

.article-actions {
  text-align: right;
}

.view-btn {
  padding: 8px 16px;
  background-color: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.view-btn:hover {
  background-color: #337ecc;
}

.no-results {
  text-align: center;
  padding: 40px;
  color: #999;
}
</style>
