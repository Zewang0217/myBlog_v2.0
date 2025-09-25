<!-- blog-frontend/src/components/blog/ArticleDetail.vue -->
<!-- 文章详情组件 -->
<template>
  <div class="article-detail">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading">
      加载中...
    </div>

    <!-- 错误状态 -->
    <div v-else-if="error" class="error">
      错误: {{ error }}
    </div>

    <!-- 文章内容 -->
    <div v-else-if="article" class="article-content">
      <div class="article-header">
        <h1 class="article-title">{{ article.title }}</h1>
        <div class="article-meta">
          <span class="author">作者: {{ article.author }}</span>
          <span class="date">发布时间: {{ formatDate(article.createTime) }}</span>
          <span v-if="article.updateTime !== article.createTime" class="updated">
            更新时间: {{ formatDate(article.updateTime) }}
          </span>
        </div>
      </div>

      <div class="article-body">
        <p class="content">{{ article.content }}</p>
      </div>

      <div class="article-actions">
        <button @click="goBack" class="back-btn">返回列表</button>
        <button @click="editArticle" class="edit-btn">编辑文章</button>
        <button @click="deleteArticle" class="delete-btn">删除文章</button>
      </div>
    </div>

    <!-- 未找到文章 -->
    <div v-else class="not-found">
      未找到文章
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted } from 'vue'
import { useArticle } from '@/composables/useArticles'
import { useRouter, useRoute } from 'vue-router'

const router = useRouter()
const route = useRoute()
const { article, loading, error, fetchArticle } = useArticle()

// 格式化日期显示
const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('zh-CN')
}

// 返回文章列表
const goBack = () => {
  router.push('/articles')
}

// 编辑文章
const editArticle = () => {
  if (article.value) {
    router.push(`/article/edit/${article.value.id}`)
  }
}

// 删除文章
const deleteArticle = async () => {
  if (article.value && confirm('确定要删除这篇文章吗？此操作不可恢复！')) {
    const result = await deleteArticleById(article.value.id)
    if (result.success) {
      alert('文章删除成功')
      router.push('/articles')
    } else {
      alert(`删除失败: ${result.error}`)
    }
  }
}

// 组件挂载时获取文章详情
onMounted(() => {
  const id = parseInt(route.params.id as string)
  if (id) {
    fetchArticle(id)
  }
})
</script>

<style scoped>
.article-detail {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.loading, .error, .not-found {
  text-align: center;
  padding: 40px;
}

.error {
  color: #e53935;
}

.not-found {
  color: #999;
}

.article-header {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.article-title {
  margin: 0 0 15px 0;
  color: #333;
  font-size: 28px;
  line-height: 1.3;
}

.article-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  color: #666;
  font-size: 14px;
}

.article-body {
  margin-bottom: 30px;
}

.content {
  white-space: pre-wrap;
  line-height: 1.6;
  color: #444;
}

.article-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.back-btn, .edit-btn {
  padding: 8px 16px;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.back-btn {
  background-color: #666;
}

.edit-btn {
  background-color: #42b983;
}
</style>
