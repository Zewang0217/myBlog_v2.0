<!-- blog-frontend/src/components/blog/ArticleList.vue -->
<!-- 文章列表组件 -->
<template>
  <div class="article-list">
    <div class="header">
      <h2>文章列表</h2>
      <div class="header-actions">
        <button @click="goToCreate" class="create-btn">
          创建文章
        </button>
        <button @click="fetchArticles" :disabled="loading" class="refresh-btn">
          {{ loading ? '刷新中...' : '刷新' }}
        </button>
      </div>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading">
      加载中...
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="error">
      错误: {{ error }}
      <button @click="fetchArticles">重试</button>
    </div>

    <!-- 文章列表 -->
    <div v-else class="articles">
      <div
        v-for="article in articles"
        :key="article.id"
        class="article-item"
      >
        <h3 class="article-title">{{ article.title }}</h3>
        <p class="article-meta">
          作者: {{ article.author }} |
          发布时间: {{ formatDate(article.createTime) }}
        </p>
        <div class="article-actions">
          <button @click="viewArticle(article.id)" class="view-btn">查看</button>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="articles.length === 0" class="empty-state">
        暂无文章
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useArticles } from '@/composables/useArticles'
import { useRouter } from 'vue-router'

// 使用文章列表组合式函数
const { articles, loading, error, fetchArticles } = useArticles()
const router = useRouter()

// 格式化日期显示
const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('zh-CN')
}

// 查看文章详情
const viewArticle = (id: number) => {
  router.push(`/article/${id}`)
}

// 跳转到创建文章页面
const goToCreate = () => {
  router.push('/article/create')
}

// 组件挂载时获取文章列表
onMounted(() => {
  fetchArticles()
})
</script>

<style scoped>
.article-list {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.header h2 {
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 10px;
}

.create-btn {
  padding: 8px 16px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.create-btn:hover {
  background-color: #359c6d;
}

.refresh-btn {
  padding: 8px 16px;
  background-color: #666;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.refresh-btn:hover:not(:disabled) {
  background-color: #555;
}

.refresh-btn:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.loading, .error {
  text-align: center;
  padding: 20px;
}

.error {
  color: #e53935;
}

.articles {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.article-item {
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 16px;
  background-color: #fff;
}

.article-title {
  margin: 0 0 8px 0;
  color: #333;
}

.article-meta {
  margin: 0 0 16px 0;
  color: #666;
  font-size: 14px;
}

.article-actions {
  display: flex;
  justify-content: flex-end;
}

.view-btn {
  padding: 6px 12px;
  background-color: #409eff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.view-btn:hover {
  background-color: #337ecc;
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: #999;
}
</style>
