<!-- blog-frontend/src/views/BlogArticles.vue -->
<!-- 博客文章列表页面 -->
<template>
  <div class="articles-container">
    <div class="header">
      <h2>文章列表</h2>
      <div class="header-actions">
        <router-link to="/article/create" class="create-btn">
          创建新文章
        </router-link>
      </div>
    </div>

    <div v-if="loading" class="loading">加载中...</div>

    <div v-else-if="error" class="error">
      加载文章失败: {{ error }}
      <button @click="fetchArticles" class="retry-btn">重试</button>
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
          <router-link v-if="article.status === 0" :to="`/article/edit/${article.id}`" class="btn-edit">编辑</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useArticles } from '@/composables/useArticles'
import { ArticleStatus } from '@/types/article'

const { articles, loading, error, fetchPublishedArticles } = useArticles()

// 组件挂载时获取已发布的文章列表
onMounted(() => {
  fetchArticles()
})

// 获取文章列表
const fetchArticles = async () => {
  await fetchPublishedArticles()
}

// 格式化日期
const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString('zh-CN')
}
</script>

<style scoped>
.articles-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.header h2 {
  margin: 0;
  color: #333;
}

.create-btn {
  padding: 8px 16px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  text-decoration: none;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.create-btn:hover {
  background-color: #359c6d;
}

.loading,
.no-articles {
  text-align: center;
  padding: 40px;
  color: #666;
}

.error {
  text-align: center;
  padding: 20px;
  color: #f44336;
  background-color: #ffebee;
  border-radius: 4px;
  margin: 20px 0;
}

.retry-btn {
  margin-left: 10px;
  padding: 5px 10px;
  background-color: #f0f0f0;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
}

.articles-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.article-item {
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  transition: transform 0.2s, box-shadow 0.2s;
}

.article-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
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
  background-color: #f0f0f0;
  color: #333;
}

.btn-edit:hover {
  background-color: #e0e0e0;
}

@media (max-width: 768px) {
  .header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }

  .header-actions {
    width: 100%;
  }

  .create-btn {
    width: 100%;
    text-align: center;
  }
}
</style>
