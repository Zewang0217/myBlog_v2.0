<!-- blog-frontend/src/views/BlogArticleDetail.vue -->
<!-- 博客文章详情页面 -->
<template>
  <div class="blog-article-detail">
    <ArticleDetail />
  </div>
</template>

<script setup lang="ts">
import ArticleDetail from '@/components/blog/ArticleDetail.vue'
</script>

<style scoped>
.blog-article-detail {
  padding: 20px 0;
  max-width: 100%;
  margin: 0 auto;
  background-color: #f8f9fa;
  min-height: 100vh;
}

/* 适配ArticleDetail组件的样式 */
:deep(.article-detail) {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}
</style>
