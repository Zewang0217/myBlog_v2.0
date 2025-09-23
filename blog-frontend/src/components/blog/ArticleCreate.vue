<!-- blog-frontend/src/components/blog/ArticleCreate.vue -->
<!-- 创建文章组件 -->
<template>
  <div class="article-create">
    <h2>创建新文章</h2>
    <form @submit.prevent="handleSubmit" class="article-form">
      <div class="form-group">
        <label for="title">标题:</label>
        <input
          id="title"
          v-model="form.title"
          type="text"
          required
          :disabled="loading"
          class="form-control"
        />
      </div>

      <div class="form-group">
        <label for="author">作者:</label>
        <input
          id="author"
          v-model="form.author"
          type="text"
          required
          :disabled="loading"
          class="form-control"
        />
      </div>

      <div class="form-group">
        <label for="content">内容:</label>
        <textarea
          id="content"
          v-model="form.content"
          required
          :disabled="loading"
          rows="10"
          class="form-control"
        ></textarea>
      </div>

      <div class="form-actions">
        <button type="button" @click="goBack" :disabled="loading" class="btn-secondary">
          取消
        </button>
        <button type="submit" :disabled="loading" class="btn-primary">
          {{ loading ? '创建中...' : '创建文章' }}
        </button>
      </div>

      <div v-if="error" class="error-message">
        错误: {{ error }}
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { reactive } from 'vue'
import { useArticle } from '@/composables/useArticles'
import { useRouter } from 'vue-router'

const router = useRouter()
const { loading, error, create } = useArticle()

// 表单数据
const form = reactive({
  title: '',
  content: '',
  author: ''
})

// 提交表单
const handleSubmit = async () => {
  const result = await create(form)
  if (result.success) {
    alert('文章创建成功!')
    router.push('/articles')
  }
}

// 返回文章列表
const goBack = () => {
  router.push('/articles')
}
</script>

<style scoped>
.article-create {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.article-form {
  background: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
  color: #333;
}

.form-control {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
  box-sizing: border-box;
}

.form-control:focus {
  outline: none;
  border-color: #42b983;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 30px;
}

.btn-primary, .btn-secondary {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

.btn-primary {
  background-color: #42b983;
  color: white;
}

.btn-primary:hover:not(:disabled) {
  background-color: #359c6d;
}

.btn-primary:disabled {
  background-color: #a0a0a0;
  cursor: not-allowed;
}

.btn-secondary {
  background-color: #666;
  color: white;
}

.btn-secondary:hover:not(:disabled) {
  background-color: #555;
}

.btn-secondary:disabled {
  background-color: #a0a0a0;
  cursor: not-allowed;
}

.error-message {
  margin-top: 15px;
  padding: 10px;
  background-color: #ffebee;
  color: #e53935;
  border-radius: 4px;
}
</style>
