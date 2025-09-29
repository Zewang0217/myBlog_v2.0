<!-- src/views/BlogArticles.vue -->
<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useArticles } from '@/composables/useArticles'
import { ArticleStatus } from '@/types/article'
import { useAuthStore } from '@/stores/auth'
import { useRouter } from 'vue-router'

const { articles, loading, error, fetchPublishedArticles } = useArticles()
const authStore = useAuthStore()
const router = useRouter()

// 组件挂载时获取已发布的文章列表
onMounted(() => {
  fetchArticles()
})

// 获取文章列表
const fetchArticles = async () => {
  await fetchPublishedArticles()
}

// 格式化日期显示
const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('zh-CN')
}
</script>

<template>
  <div class="articles-container">
    <div class="header">
      <h2>文章列表</h2>
      <!-- 只有登录后才显示创建文章按钮 -->
      <div v-if="authStore.isAuthenticated" class="header-actions">
        <router-link to="/article/create" class="create-btn">创建文章</router-link>
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
            v-if="authStore.isAuthenticated && article.status === 0"
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

.create-btn {
  padding: 8px 16px;
  background-color: #42b983;
  color: white;
  text-decoration: none;
  border-radius: 4px;
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
