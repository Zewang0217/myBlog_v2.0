<!-- blog-frontend/src/components/blog/ArticleList.vue -->
<!-- 文章列表组件 -->
<template>
  <div class="article-list">
    <div class="header">
      <h2>{{ listTitle }}</h2>
      <div class="header-actions">
        <select v-model="currentFilter" @change="handleFilterChange" class="filter-select">
          <option value="all">全部文章</option>
          <option value="published">已发布</option>
          <option value="draft">草稿</option>
        </select>
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
          发布时间: {{ formatDate(article.createTime) }} |
          状态: {{ getStatusText(article.status) }}
        </p>
        <div class="article-actions">
          <button @click="viewArticle(article.id)" class="view-btn">查看</button>
          <button
            v-if="article.status === ArticleStatus.DRAFT"
            @click="publishArticle(article.id)"
            class="publish-btn"
          >
            发布
          </button>
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
import { onMounted, ref, computed } from 'vue'
import { useArticles, useArticle, ArticleStatus } from '@/composables/useArticles'
import { useRouter } from 'vue-router'

// 使用文章列表组合式函数
const { articles, loading, error, fetchArticles, fetchPublishedArticles, fetchDraftArticles } = useArticles()
const { publish } = useArticle()
const router = useRouter()

// 当前筛选条件
const currentFilter = ref('all')

// 列表标题
const listTitle = computed(() => {
  switch (currentFilter.value) {
    case 'published':
      return '已发布文章'
    case 'draft':
      return '草稿箱'
    default:
      return '全部文章'
  }
})

// 获取文章列表（根据筛选条件）
const loadArticles = async () => {
  switch (currentFilter.value) {
    case 'published':
      await fetchPublishedArticles()
      break
    case 'draft':
      await fetchDraftArticles()
      break
    default:
      await fetchArticles()
  }
}

// 处理筛选条件变化
const handleFilterChange = () => {
  loadArticles()
}

// 格式化日期显示
const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('zh-CN')
}

// 获取状态文本
const getStatusText = (status: number) => {
  switch (status) {
    case ArticleStatus.DRAFT:
      return '草稿'
    case ArticleStatus.PUBLISHED:
      return '已发布'
    case ArticleStatus.OFFLINE:
      return '已下架'
    default:
      return '未知'
  }
}

// 查看文章详情
const viewArticle = (id: number) => {
  router.push(`/article/${id}`)
}

// 发布文章
const publishArticle = async (id: number) => {
  if (confirm('确定要发布这篇文章吗？')) {
    // 确保传入的 ID 是数字类型
    const articleId = Number(id);
    if (isNaN(articleId) || articleId <= 0) {
      error.value = '无效的文章ID';
      return;
    }

    const result = await publish(articleId);
    if (result.success) {
      alert('文章发布成功!');
      // 重新加载当前列表
      loadArticles();
    } else {
      error.value = result.error || '发布文章失败';
    }
  }
}


// 跳转到创建文章页面
const goToCreate = () => {
  router.push('/article/create')
}

// 组件挂载时获取文章列表
onMounted(() => {
  loadArticles()
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
  flex-wrap: wrap;
  gap: 10px;
}

.header h2 {
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 10px;
  align-items: center;
}

.filter-select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: white;
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
  gap: 10px;
}

.view-btn, .publish-btn {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.view-btn {
  background-color: #409eff;
  color: white;
}

.view-btn:hover {
  background-color: #337ecc;
}

.publish-btn {
  background-color: #42b983;
  color: white;
}

.publish-btn:hover {
  background-color: #359c6d;
}

.empty-state {
  text-align: center;
  padding: 40px;
  color: #999;
}

@media (max-width: 768px) {
  .header {
    flex-direction: column;
    align-items: stretch;
  }

  .header-actions {
    justify-content: center;
  }
}
</style>
