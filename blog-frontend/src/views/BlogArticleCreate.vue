<!-- blog-frontend/src/views/BlogArticleCreate.vue -->
<!-- 博客文章创建页面 -->
<template>
  <div class="blog-article-create">
    <ArticleCreate />
  </div>
</template>

<script setup lang="ts">
import ArticleCreate from '@/components/blog/ArticleCreate.vue'
</script>

<style scoped>
.blog-article-create {
  padding: 20px 0;
}
</style>
