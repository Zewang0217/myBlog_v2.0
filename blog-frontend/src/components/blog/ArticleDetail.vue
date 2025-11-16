<!-- blog-frontend/src/components/blog/ArticleDetail.vue -->
<!-- 文章详情组件 -->
<template>
  <div class="article-detail">
    <!-- 加载状态 -->
    <div v-if="loading" class="loading">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
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
          <div class="meta-item">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
            </svg>
            <span class="meta-text">{{ article.author }}</span>
          </div>
          <div class="meta-item">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
              <path d="M11.99 2C6.47 2 2 6.48 2 12s4.47 10 9.99 10C17.52 22 22 17.52 22 12S17.52 2 11.99 2zM12 20c-4.42 0-8-3.58-8-8s3.58-8 8-8 8 3.58 8 8-3.58 8-8 8zm.5-13H11v6l5.25 3.15.75-1.23-4.5-2.67z"/>
            </svg>
            <span class="meta-text">{{ formatDate(article.createTime) }}</span>
          </div>
          <div v-if="article.updateTime !== article.createTime" class="meta-item">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
              <path d="M21 11.01L3 11v2h18zM3 16h12v2H3zM21 6H3v2.01L21 8z"/>
            </svg>
            <span class="meta-text">{{ formatDate(article.updateTime) }}</span>
          </div>
          <div v-if="article.categories && article.categories.length > 0" class="meta-item">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/>
            </svg>
            <span class="meta-text">{{ article.categories.map(c => c.name).join(', ') }}</span>
          </div>
        </div>
      </div>

      <div class="article-body">
        <div class="content-wrapper">
          <div class="content" v-html="renderedContent"></div>
        </div>
        
        <!-- 文章状态指示器 -->
        <div v-if="article" class="article-status-badge" :class="getStatusClass(article.status)">
          {{ getStatusText(article.status) }}
        </div>
        
        <div class="article-sidebar">
          <div v-if="toc.length > 0" class="toc-container">
            <h3>文章目录</h3>
            <ul class="toc-list">
              <li 
                v-for="item in toc" 
                :key="item.id" 
                :class="`toc-item toc-level-${item.level}`"
                :style="{
                  'padding-left': (item.level - 2) * 16 + 'px'
                }"
              >
                <a 
                  :href="'#' + item.id"
                  @click.prevent="scrollToElement(item.id)"
                  :class="{
                    'active-toc': activeTocId === item.id
                  }"
                >
                  {{ item.text }}
                </a>
              </li>
            </ul>
          </div>
          
          <div class="social-share">
            <h3>分享文章</h3>
            <div class="share-buttons">
              <button @click="shareToTwitter" class="share-btn twitter" title="分享到Twitter">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M23.953 4.57a10 10 0 01-2.825.775 4.958 4.958 0 002.163-2.723c-.951.555-2.005.959-3.127 1.184a4.92 4.92 0 00-8.384 4.482C7.69 8.095 4.067 6.13 1.64 3.162a4.822 4.822 0 00-.666 2.475c0 1.71.87 3.213 2.188 4.096a4.904 4.904 0 01-2.228-.616v.06a4.923 4.923 0 003.946 4.827 4.996 4.996 0 01-2.212.085 4.936 4.936 0 004.604 3.417 9.867 9.867 0 01-6.102 2.105c-.39 0-.779-.023-1.17-.067a13.995 13.995 0 007.557 2.209c9.053 0 13.998-7.496 13.998-13.985 0-.21 0-.42-.015-.63A9.935 9.935 0 0024 4.59z"/>
                </svg>
              </button>
              <button @click="shareToFacebook" class="share-btn facebook" title="分享到Facebook">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M24 12.073c0-6.627-5.373-12-12-12s-12 5.373-12 12c0 5.99 4.388 10.954 10.125 11.854v-8.385H7.078v-3.47h3.047V9.43c0-3.007 1.792-4.669 4.533-4.669 1.312 0 2.686.235 2.686.235v2.953H15.83c-1.491 0-1.956.925-1.956 1.874v2.25h3.328l-.532 3.47h-2.796v8.385C19.612 23.027 24 18.062 24 12.073z"/>
                </svg>
              </button>
              <button @click="copyLink" class="share-btn link" title="复制链接">
                <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                  <path d="M13.062 8.188a5.207 5.207 0 000 7.364l-1.938 1.938a5.207 5.207 0 00-7.364 0 5.207 5.207 0 000-7.364l1.938-1.938a5.207 5.207 0 007.364 0zM18.188 3.062a5.207 5.207 0 00-7.364 0l-1.938 1.938a5.207 5.207 0 000 7.364l1.938 1.938a5.207 5.207 0 007.364 0 5.207 5.207 0 000-7.364z"/>
                </svg>
              </button>
            </div>
          </div>
        </div>
      </div>

      <div class="article-actions">
        <button class="action-btn like-btn" @click="handleLike" :disabled="likeLoading">
          <svg width="16" height="16" viewBox="0 0 24 24" :fill="article.isLiked ? '#e53935' : 'currentColor'">
            <path d="M12 21.35l-1.45-1.32C5.4 15.36 2 12.28 2 8.5 2 5.42 4.42 3 7.5 3c1.74 0 3.41.81 4.5 2.09C13.09 3.81 14.76 3 16.5 3 19.58 3 22 5.42 22 8.5c0 3.78-3.4 6.86-8.55 11.54L12 21.35z"/>
          </svg>
          {{ article.likeCount || 0 }}
          <span class="like-text">{{ article.isLiked ? '已赞' : '点赞' }}</span>
        </button>
        <button @click="goBack" class="action-btn back-btn">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
            <path d="M20 11H7.83l5.59-5.59L12 4l-8 8 8 8 1.41-1.41L7.83 13H20v-2z"/>
          </svg>
          返回列表
        </button>
        <button @click="editArticle" class="action-btn edit-btn">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
            <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/>
          </svg>
          编辑文章
        </button>
        <button @click="deleteArticle" class="action-btn delete-btn">
          <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
            <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/>
          </svg>
          删除文章
        </button>
      </div>

      <!-- 文章封面图片 -->
      <div v-if="article?.coverImage" class="article-cover">
        <img :src="article.coverImage" :alt="article.title" class="cover-image" />
      </div>

      <!-- 文章评论区 -->
      <div class="article-comments">
        <CommentSection :articleId="article?.id || ''" />
      </div>
    </div>

    <!-- 未找到文章 -->
    <div v-else class="not-found">
      未找到文章
    </div>
  </div>
</template>

<script setup lang="ts">
import { onMounted, computed, ref, nextTick } from 'vue'
import { useArticle } from '@/composables/useArticles'
import { ArticleStatus } from '@/types/article'
import { useRouter, useRoute } from 'vue-router'
import { marked } from 'marked'
import DOMPurify from 'dompurify'
import {useAuthStore} from "@/stores/auth.ts"
import CommentSection from './CommentSection.vue';

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const { article, loading, error, fetchArticle, deleteArticleById: deleteArticleById, toggleArticleLike } = useArticle()

// 目录相关
interface TocItem {
  id: string;
  text: string;
  level: number;
}

const toc = ref<TocItem[]>([])
const activeTocId = ref<string | null>(null)

// 渲染Markdown内容
const renderedContent = computed(() => {
  if (!article.value) return ''
  // 使用marked将Markdown转换为HTML，并使用DOMPurify净化HTML防止XSS攻击
  let content = marked(article.value.content || '')
  // 为标题添加ID以便目录导航
  content = addIdsToHeadings(content)
  return DOMPurify.sanitize(content)
})

// 为Markdown标题添加ID
const addIdsToHeadings = (html: string) => {
  return html.replace(/<h([2-6])>(.*?)<\/h\1>/g, (match, level, content) => {
    // 从标题内容中提取文本并转换为URL友好的ID
    const text = content.replace(/<[^>]*>/g, '') // 移除HTML标签
    const id = text.trim().toLowerCase()
      .replace(/[^\w\u4e00-\u9fff]/g, '-') // 只保留中文、字母、数字和连字符
      .replace(/-+/g, '-') // 多个连字符合并为一个
      .replace(/^-|-$/g, '') // 移除开头和结尾的连字符

    return `<h${level} id="${id}">${content}</h${level}>`
  })
}

// 生成目录
const generateToc = () => {
  if (!article.value) return

  const content = article.value.content || ''
  const headings = content.match(/^#{2,6}\s+.*/gm) || []
  toc.value = []

  headings.forEach(heading => {
    const level = heading.match(/^#{2,6}/)![0].length
    const text = heading.substring(level + 1).trim()
    const id = text.toLowerCase()
      .replace(/[^\w\u4e00-\u9fff]/g, '-') // 只保留中文、字母、数字和连字符
      .replace(/-+/g, '-') // 多个连字符合并为一个
      .replace(/^-|-$/g, '') // 移除开头和结尾的连字符

    toc.value.push({ id, text, level })
  })
}

// 滚动到指定元素
const scrollToElement = (id: string) => {
  const element = document.getElementById(id)
  if (element) {
    element.scrollIntoView({ behavior: 'smooth' })
    activeTocId.value = id
  }
}

// 监听滚动事件以高亮目录
const handleScroll = () => {
  const headings = document.querySelectorAll('.content h2, .content h3, .content h4, .content h5, .content h6')
  let current = ''
  
  headings.forEach(heading => {
    const rect = heading.getBoundingClientRect()
    if (rect.top <= 100) {
      current = heading.id
    }
  })
  
  if (current && activeTocId.value !== current) {
    activeTocId.value = current
  }
}

// 格式化日期显示
const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleDateString('zh-CN')
}

// 获取状态文本
const getStatusText = (status: number | string) => {
  // 处理字符串类型的状态值
  if (typeof status === 'string') {
    switch (status.toUpperCase()) {
      case 'DRAFT':
        return '草稿'
      case 'PUBLISHED':
        return '已发布'
      case 'OFFLINE':
        return '已下架'
      default:
        return '未知'
    }
  }
  
  // 处理数字类型的状态值
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

// 获取状态样式类
const getStatusClass = (status: number | string) => {
  const statusValue = typeof status === 'string' ? status.toUpperCase() : status
  
  if (statusValue === 'DRAFT' || statusValue === 0) {
    return 'status-draft'
  } else if (statusValue === 'PUBLISHED' || statusValue === 1) {
    return 'status-published'
  } else if (statusValue === 'OFFLINE' || statusValue === 2) {
    return 'status-offline'
  }
  return 'status-unknown'
}

// 返回文章列表
const goBack = () => {
  router.push('/articles')
}

// 处理点赞
const likeLoading = ref(false)
const handleLike = async () => {
  if (!authStore.isAuthenticated) {
    alert('请先登录后再点赞')
    router.push('/login')
    return
  }
  
  likeLoading.value = true
  try {
    await toggleArticleLike()
  } catch (error) {
    alert('点赞操作失败，请重试')
  } finally {
    likeLoading.value = false
  }
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

  // 改进权限检查方式
  const userRole = authStore.user?.role
  const hasAdminRole = userRole === 'ADMIN' || userRole === 'ROLE_ADMIN'

  if (!hasAdminRole) {
    alert('权限不足，只有管理员可以删除文章')
    return
  }

  if (article.value && confirm('确定要删除这篇文章吗？此操作不可恢复！')) {
    try {
      const result = await deleteArticleById(article.value.id)
      if (result.success) {
        alert('文章删除成功')
        router.push('/articles')
      } else {
        alert(`删除失败: ${result.error}`)
      }
    } catch (error: any) {
      // 检查是否是权限错误
      if (error.response?.status === 403) {
        alert('权限不足，无法删除文章')
      } else if (error.response?.status === 401) {
        // Token过期或无效
        alert('登录已过期，请重新登录')
        authStore.logout()
        router.push('/login')
      } else {
        alert('删除失败: ' + (error.message || '未知错误'))
      }
    }
  }
}

// 社交分享功能
const shareToTwitter = () => {
  if (!article.value) return
  const url = encodeURIComponent(window.location.href)
  const text = encodeURIComponent(article.value.title)
  window.open(`https://twitter.com/intent/tweet?text=${text}&url=${url}`, '_blank')
}

const shareToFacebook = () => {
  if (!article.value) return
  const url = encodeURIComponent(window.location.href)
  window.open(`https://www.facebook.com/sharer/sharer.php?u=${url}`, '_blank')
}

const copyLink = async () => {
  try {
    await navigator.clipboard.writeText(window.location.href)
    alert('链接已复制到剪贴板！')
  } catch (err) {
    console.error('复制链接失败:', err)
    alert('复制链接失败，请手动复制')
  }
}

// 组件挂载时获取文章详情
onMounted(async () => {
  const id = route.params.id as string
  if (id && typeof id === 'string' && id.trim() !== '') {
    console.log('调用 fetchArticle 函数');
    const result = await fetchArticle(id)
    if (result.success) {
      await nextTick() // 等待DOM更新完成
      generateToc()
      window.addEventListener('scroll', handleScroll)
    }
  } else {
    console.error('Invalid article ID:', id)
    error.value = '文章不存在'
  }
})

// 组件卸载时移除事件监听
import { onUnmounted } from 'vue'

onUnmounted(() => {
  window.removeEventListener('scroll', handleScroll)
})
</script>

<style scoped>
/* 深色主题支持 */
[data-theme="dark"] .article-detail {
  background: var(--background-secondary);
}

[data-theme="dark"] .loading {
  color: var(--text-color-light);
}

[data-theme="dark"] .error {
  color: #f87171;
  background: linear-gradient(135deg, rgba(239, 68, 68, 0.1), rgba(220, 38, 38, 0.1));
  border: 2px solid rgba(239, 68, 68, 0.3);
}

[data-theme="dark"] .not-found {
  color: var(--text-color-light);
}

[data-theme="dark"] .article-header {
  border-bottom: 1px solid var(--border-color-base);
}

[data-theme="dark"] .article-title {
  background: linear-gradient(135deg, var(--text-color-primary), var(--text-color-secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

[data-theme="dark"] .article-meta {
  color: var(--text-color-light);
}

[data-theme="dark"] .meta-text {
  color: var(--text-color-secondary);
}

[data-theme="dark"] .content {
  background: var(--background-glass);
  color: var(--text-color-primary);
  border: 1px solid var(--border-color-base);
}

[data-theme="dark"] .content :deep(h1),
[data-theme="dark"] .content :deep(h2),
[data-theme="dark"] .content :deep(h3),
[data-theme="dark"] .content :deep(h4),
[data-theme="dark"] .content :deep(h5),
[data-theme="dark"] .content :deep(h6) {
  color: var(--text-color-primary);
}

[data-theme="dark"] .content :deep(p) {
  color: var(--text-color-secondary);
}

[data-theme="dark"] .content :deep(a) {
  color: var(--primary-color);
  border-bottom: 1px solid transparent;
}

[data-theme="dark"] .content :deep(a:hover) {
  border-bottom: 1px solid var(--primary-color);
  color: var(--primary-color-dark);
}

[data-theme="dark"] .content :deep(code) {
  background-color: var(--background-secondary);
  color: #fbbf24;
  border: 1px solid var(--border-color-base);
}

[data-theme="dark"] .content :deep(pre) {
  background-color: var(--background-secondary);
  color: var(--text-color-primary);
}

[data-theme="dark"] .content :deep(pre code) {
  color: var(--text-color-secondary);
}

[data-theme="dark"] .content :deep(blockquote) {
  border-left: 4px solid var(--primary-color);
  background-color: var(--background-secondary);
  color: var(--text-color-secondary);
}

[data-theme="dark"] .content :deep(img) {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
}

[data-theme="dark"] .toc-container {
  background: var(--background-glass);
  border: 1px solid var(--border-color-base);
}

[data-theme="dark"] .toc-container h3 {
  color: var(--text-color-primary);
}

[data-theme="dark"] .toc-item a {
  color: var(--text-color-secondary);
}

[data-theme="dark"] .toc-item a:hover,
[data-theme="dark"] .toc-item a.active-toc {
  background-color: rgba(129, 140, 248, 0.2);
  color: var(--primary-color);
}

[data-theme="dark"] .social-share {
  background: var(--background-glass);
  border: 1px solid var(--border-color-base);
}

[data-theme="dark"] .social-share h3 {
  color: var(--text-color-primary);
}

[data-theme="dark"] .share-btn.twitter {
  background-color: #1da1f2;
}

[data-theme="dark"] .share-btn.facebook {
  background-color: #1877f2;
}

[data-theme="dark"] .share-btn.link {
  background-color: var(--border-color-base);
}

[data-theme="dark"] .article-actions {
  border-top: 1px solid var(--border-color-base);
}

[data-theme="dark"] .action-btn {
  background-color: var(--background-glass);
  color: var(--text-color-primary);
  border: 2px solid var(--border-color-base);
}

[data-theme="dark"] .action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.3);
}

[data-theme="dark"] .back-btn {
  background-color: var(--border-color-base);
}

[data-theme="dark"] .edit-btn {
  background-color: #10b981;
}

[data-theme="dark"] .delete-btn {
  background-color: #ef4444;
}

[data-theme="dark"] .like-btn {
  background-color: #1da1f2;
}

[data-theme="dark"] .article-comments {
  background: var(--background-glass);
  border-top: 1px solid var(--border-color-base);
}

/* 文章封面图片样式 */
.article-cover {
  margin-bottom: 30px;
  text-align: center;
}

.cover-image {
  max-width: 100%;
  max-height: 400px;
  object-fit: cover;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  border: 1px solid rgba(0, 0, 0, 0.1);
}

[data-theme="dark"] .article-cover {
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
  border: 1px solid var(--border-color-base);
}

[data-theme="dark"] .cover-image {
  filter: brightness(0.9);
}

.article-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  display: flex;
  flex-direction: column;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #666;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #42b983;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 10px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error, .not-found {
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
  margin: 0 0 20px 0;
  color: #2c3e50;
  font-size: 2.5em;
  font-weight: 700;
  line-height: 1.2;
  text-align: center;
  letter-spacing: -0.5px;
}

.article-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 25px;
  justify-content: center;
  color: #666;
  font-size: 16px;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
}

.meta-text {
  font-weight: 500;
}

.article-body {
  display: flex;
  gap: 30px;
  margin-bottom: 30px;
  position: relative;
}

@media (max-width: 992px) {
  .article-body {
    flex-direction: column-reverse;
  }
  
  .article-sidebar {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .article-title {
    font-size: 2em;
  }
  
  .article-meta {
    gap: 15px;
    font-size: 14px;
  }
  
  .meta-item svg {
    width: 14px;
    height: 14px;
  }
}

.content-wrapper {
  flex: 1;
  min-width: 0;
}

.content {
  line-height: 1.8;
  color: #444;
  font-size: 1.1em;
  background: white;
  padding: 40px;
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}

@media (max-width: 768px) {
  .content {
    padding: 20px;
  }
}

.content :deep(h1),
.content :deep(h2),
.content :deep(h3),
.content :deep(h4),
.content :deep(h5),
.content :deep(h6) {
  margin: 1.5em 0 1em;
  color: #2c3e50;
  font-weight: 700;
  line-height: 1.3;
}

.content :deep(h1) {
  font-size: 2em;
  border-bottom: 2px solid #f0f0f0;
  padding-bottom: 0.3em;
}

.content :deep(h2) {
  font-size: 1.7em;
  border-bottom: 1px solid #f0f0f0;
  padding-bottom: 0.3em;
}

.content :deep(h3) {
  font-size: 1.4em;
}

.content :deep(h4) {
  font-size: 1.2em;
}

.content :deep(h5) {
  font-size: 1.1em;
}

.content :deep(h6) {
  font-size: 1em;
}

.content :deep(p) {
  margin: 1.2em 0;
  text-align: justify;
}

.content :deep(a) {
  color: #42b983;
  text-decoration: none;
  border-bottom: 1px solid transparent;
  transition: all 0.2s ease;
  font-weight: 500;
}

.content :deep(a:hover) {
  border-bottom: 1px solid #42b983;
  color: #359c6d;
}

.content :deep(code) {
  padding: 0.25em 0.5em;
  margin: 0;
  font-size: 0.9em;
  background-color: #f6f8fa;
  border: 1px solid #e1e4e8;
  border-radius: 6px;
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
  color: #e96900;
}

.content :deep(pre) {
  background-color: #2d3748;
  border-radius: 8px;
  padding: 16px;
  overflow: auto;
  margin: 1.5em 0;
  line-height: 1.5;
  box-shadow: inset 0 1px 2px rgba(0,0,0,0.1);
}

.content :deep(pre code) {
  color: #e2e8f0;
  background: none;
  padding: 0;
  font-size: 0.9em;
  border: none;
}

.content :deep(blockquote) {
  margin: 1.5em 0;
  padding: 1em 1.5em;
  border-left: 4px solid #42b983;
  background-color: #f0f9f7;
  color: #555;
  border-radius: 0 8px 8px 0;
  font-style: italic;
}

.content :deep(blockquote p) {
  margin: 0;
}

.content :deep(ul),
.content :deep(ol) {
  padding-left: 1.5em;
  margin: 1em 0;
}

.content :deep(li) {
  margin: 0.5em 0;
}

.content :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: 8px;
  margin: 1.5em auto;
  display: block;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.article-sidebar {
  flex: 0 0 280px;
  position: sticky;
  top: 20px;
  align-self: flex-start;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.toc-container {
  background: #f8f9fa;
  border-radius: 10px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.toc-container h3 {
  margin-top: 0;
  margin-bottom: 15px;
  color: #2c3e50;
  font-size: 1.2em;
  font-weight: 600;
}

.toc-list {
  list-style: none;
  padding: 0;
  margin: 0;
  max-height: 400px;
  overflow-y: auto;
}

.toc-item {
  margin: 6px 0;
  font-size: 0.95em;
}

.toc-level-3 { padding-left: 16px; }
.toc-level-4 { padding-left: 32px; }
.toc-level-5 { padding-left: 48px; }
.toc-level-6 { padding-left: 64px; }

.toc-item a {
  display: block;
  padding: 8px 12px;
  color: #495057;
  text-decoration: none;
  border-radius: 6px;
  transition: all 0.2s ease;
  font-weight: 500;
}

.toc-item a:hover,
.toc-item a.active-toc {
  background-color: #42b983;
  color: white;
}

.social-share {
  background: #f8f9fa;
  border-radius: 10px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.social-share h3 {
  margin-top: 0;
  margin-bottom: 15px;
  color: #2c3e50;
  font-size: 1.2em;
  font-weight: 600;
}

.share-buttons {
  display: flex;
  gap: 12px;
}

.share-btn {
  width: 44px;
  height: 44px;
  border: none;
  border-radius: 50%;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  box-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.share-btn.twitter {
  background-color: #1da1f2;
  color: white;
}

.share-btn.facebook {
  background-color: #1877f2;
  color: white;
}

.share-btn.link {
  background-color: #6c757d;
  color: white;
}

.share-btn:hover {
  transform: translateY(-3px) scale(1.1);
  box-shadow: 0 4px 8px rgba(0,0,0,0.15);
}

.article-status-badge {
  position: absolute;
  top: 20px;
  right: 20px;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 0.9em;
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.3);
  z-index: 10;
}

.status-draft {
  background: linear-gradient(135deg, #fbbf24, #f59e0b);
  color: white;
}

.status-published {
  background: linear-gradient(135deg, #10b981, #059669);
  color: white;
}

.status-offline {
  background: linear-gradient(135deg, #6b7280, #4b5563);
  color: white;
}

.status-unknown {
  background: linear-gradient(135deg, #ef4444, #dc2626);
  color: white;
}

.article-actions {
    display: flex;
    justify-content: center;
    gap: 20px;
    flex-wrap: wrap;
    padding-top: 30px;
    margin-top: 20px;
    border-top: 1px solid #eee;
  }

  .action-btn {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 12px 24px;
    color: white;
    border: none;
    border-radius: 8px;
    cursor: pointer;
    font-size: 16px;
    font-weight: 500;
    transition: all 0.3s ease;
    box-shadow: 0 2px 4px rgba(0,0,0,0.1);
  }

  .back-btn {
    background-color: #6c757d;
  }

  .edit-btn {
    background-color: #42b983;
  }

  .delete-btn {
    background-color: #e53935;
  }

  .like-btn {
    background-color: #1da1f2;
  }

  .like-btn:hover {
    background-color: #0d95e8;
  }

  .action-btn:hover {
    transform: translateY(-3px);
    box-shadow: 0 4px 8px rgba(0,0,0,0.15);
  }

  .action-btn:disabled {
    opacity: 0.6;
    cursor: not-allowed;
    transform: none;
  }

  .back-btn:hover {
    background-color: #5a6268;
  }

  .edit-btn:hover {
    background-color: #359c6d;
  }

  .delete-btn:hover {
    background-color: #c0392b;
  }

  .like-text {
    margin-left: 4px;
  }

/* 滚动条样式 */
.toc-list::-webkit-scrollbar {
  width: 6px;
}

.toc-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.toc-list::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.toc-list::-webkit-scrollbar-thumb:hover {
  background: #a1a1a1;
}
</style>
