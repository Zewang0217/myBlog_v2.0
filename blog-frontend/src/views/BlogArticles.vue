<!-- blog-frontend/src/views/BlogArticles.vue -->
<!-- 博客文章列表页面 -->
<template>
  <div class="blog-articles">
    <ArticleList />
  </div>
</template>

<script setup lang="ts">
import ArticleList from '@/components/blog/ArticleList.vue'
</script>

<style scoped>
.blog-articles {
  padding: 20px 0;
}
</style>
