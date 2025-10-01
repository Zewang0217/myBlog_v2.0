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
      <div v-if="error.includes('不存在') || error.includes('NOT_FOUND')">
        <h3>文章未找到</h3>
        <p>您访问的文章不存在或已被删除。</p>
        <button @click="goBack" class="back-btn">返回文章列表</button>
      </div>
      <div v-else-if="error.includes('无效的文章ID')">
        <h3>请求参数错误</h3>
        <p>文章ID参数无效，请检查URL是否正确。</p>
        <button @click="goBack" class="back-btn">返回文章列表</button>
      </div>
      <div v-else>
        错误: {{ error }}
        <button @click="goBack" class="back-btn">返回文章列表</button>
      </div>
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
        <div class="content" v-html="renderedContent"></div>
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
import { onMounted, computed } from 'vue'
import { useArticle } from '@/composables/useArticles'
import { useRouter, useRoute } from 'vue-router'
import { marked } from 'marked'
import DOMPurify from 'dompurify'
import {useAuthStore} from "@/stores/auth.ts";

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const { article, loading, error, fetchArticle, deleteArticleById: deleteArticleById } = useArticle()

// 渲染Markdown内容
const renderedContent = computed(() => {
  if (!article.value) return ''
  // 使用marked将Markdown转换为HTML，并使用DOMPurify净化HTML防止XSS攻击
  return DOMPurify.sanitize(marked(article.value.content || ''))
})

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
  if (!authStore.isAuthenticated) {
    // 未登录则跳转到登录页
    router.push('/login')
    return
  }

  // 检查是否有管理员权限（这里假设管理员角色为'ADMIN'）
  const hasAdminRole = authStore.user?.role === 'ADMIN'

  if (!hasAdminRole) {
    // 权限不足提示
    alert('权限不足，只有管理员可以删除文章')
    return
  }

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
  const id = route.params.id as string
  if (id && typeof id === 'string' && id.trim() !== '') {
    console.log('调用 fetchArticle 函数');
    fetchArticle(id)
  } else {
    console.error('Invalid article ID:', id)
    error.value = '文章不存在'
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
  line-height: 1.6;
  color: #444;
}

.content :deep(h1),
.content :deep(h2),
.content :deep(h3) {
  margin: 1.2em 0 0.8em;
  color: #2c3e50;
}

.content :deep(p) {
  margin: 1em 0;
}

.content :deep(a) {
  color: #42b983;
  text-decoration: none;
}

.content :deep(a:hover) {
  text-decoration: underline;
}

.content :deep(code) {
  padding: 0.2em 0.4em;
  margin: 0;
  font-size: 0.9em;
  background-color: #f6f8fa;
  border-radius: 3px;
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
}

.content :deep(pre) {
  background-color: #f6f8fa;
  border-radius: 3px;
  padding: 16px;
  overflow: auto;
  margin: 1em 0;
}

.content :deep(blockquote) {
  margin: 1em 0;
  padding: 0.5em 1em;
  border-left: 4px solid #42b983;
  background-color: #f9f9f9;
  color: #666;
}

.article-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.back-btn, .edit-btn, .delete-btn {
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

.delete-btn {
  background-color: #e53935;
}

.back-btn:hover {
  background-color: #555;
}

.edit-btn:hover {
  background-color: #359c6d;
}

.delete-btn:hover {
  background-color: #c0392b;
}
</style>
