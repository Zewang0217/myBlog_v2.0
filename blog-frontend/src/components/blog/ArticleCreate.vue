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
        <SimpleMarkdownEditor
          v-model="form.content"
          @auto-save="handleAutoSave"
          placeholder="在此输入文章内容，支持Markdown语法..."
          class="markdown-editor-container"
        />
        <div v-if="autoSaveStatus" class="auto-save-status">
          {{ autoSaveStatus }}
        </div>
      </div>

      <div class="form-group">
        <label>文章状态:</label>
        <div class="radio-group">
          <label class="radio-label">
            <input
              type="radio"
              v-model="form.status"
              :value="ArticleStatus.DRAFT"
              :disabled="loading"
              required
            /> 保存为草稿
          </label>
          <label class="radio-label">
            <input
              type="radio"
              v-model="form.status"
              :value="ArticleStatus.PUBLISHED"
              :disabled="loading"
              required
            /> 直接发布
          </label>
        </div>
      </div>

      <div class="form-actions">
        <button type="button" @click="goBack" :disabled="loading" class="btn-secondary">
          取消
        </button>
        <button type="submit" :disabled="loading" class="btn-primary">
          {{ loading ? '处理中...' : '保存文章' }}
        </button>
      </div>

      <div v-if="error" class="error-message">
        错误: {{ error }}
      </div>

      <div v-if="error" class="error-message">
        错误: {{ error }}
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { useArticle } from '@/composables/useArticles'
import { ArticleStatus } from '@/types/article'
import { useRouter } from 'vue-router'
import SimpleMarkdownEditor from '@/components/blog/SimpleMarkdownEditor.vue'

const router = useRouter()
const { loading, error, create } = useArticle()

// 表单数据
const form = reactive({
  title: '',
  content: '',
  author: '',
  status: ArticleStatus.DRAFT
})

// 自动保存状态
const autoSaveStatus = ref('')

// 处理自动保存
const handleAutoSave = async (content: string) => {
  // 只有当标题不为空时才自动保存
  if (form.title.trim() !== '') {
    autoSaveStatus.value = '自动保存中...'
    
    try {
      // 创建一个临时的草稿对象
      const draftForm = {
        ...form,
        content: content,
        status: ArticleStatus.DRAFT
      }
      
      // 调用创建或更新方法
      const result = await create(draftForm)
      
      if (result.success && result.data) {
        autoSaveStatus.value = `已自动保存: ${new Date().toLocaleTimeString()}`
      } else {
        autoSaveStatus.value = '自动保存失败'
      }
    } catch (err) {
      console.error('自动保存失败:', err)
      autoSaveStatus.value = '自动保存失败'
    }
  }
}

// 提交表单
const handleSubmit = async () => {
  const result = await create(form)
  if (result.success) {
    if (form.status === ArticleStatus.PUBLISHED) {
      alert('文章已成功发布!')
    } else {
      alert('文章已保存为草稿!')
    }
    router.push('/articles')
  }
}

// 返回文章列表
const goBack = () => {
  if (confirm('确定要离开页面吗？未保存的内容将会丢失。')) {
    router.push('/articles')
  }
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

.radio-group {
  display: flex;
  gap: 20px;
  margin-top: 5px;
}

.radio-label {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  font-weight: normal;
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

.markdown-editor-container {
  height: 500px;
  border-radius: 4px;
  overflow: hidden;
  border: 1px solid #ddd;
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
