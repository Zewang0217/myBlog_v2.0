<!-- src/views/BlogArticleCreate.vue -->
<template>
  <div class="article-editor">
    <h2>{{ isEditMode ? '编辑文章' : '创建新文章' }}</h2>

    <form @submit.prevent="handleSubmit" class="article-form">
      <div class="form-group">
        <label for="title">标题</label>
        <input
          id="title"
          v-model="form.title"
          type="text"
          required
          :disabled="loading"
          placeholder="输入文章标题"
          class="form-control"
        />
      </div>

      <div class="form-group">
        <label for="author">作者</label>
        <input
          id="author"
          v-model="form.author"
          type="text"
          required
          :disabled="loading"
          placeholder="输入作者名称"
          class="form-control"
        />
      </div>

      <!-- 分类选择 -->
      <div class="form-group">
        <label>分类</label>
        <div class="category-section">
          <div class="category-checkboxes">
            <label
              v-for="category in categories"
              :key="category.id"
              class="category-checkbox"
            >
              <input
                type="checkbox"
                :value="category.id"
                v-model="selectedCategoryIds"
                :disabled="loading"
              />
              <span>{{ category.name }}</span>
            </label>
          </div>
        </div>
      </div>

      <div class="form-group">
        <label>状态</label>
        <div class="status-options">
          <label class="status-option">
            <input
              type="radio"
              v-model="form.status"
              :value="ArticleStatus.DRAFT"
              :disabled="loading"
            />
            <span>草稿</span>
          </label>
          <label class="status-option">
            <input
              type="radio"
              v-model="form.status"
              :value="ArticleStatus.PUBLISHED"
              :disabled="loading"
            />
            <span>发布</span>
          </label>
        </div>
      </div>

      <div class="form-group">
        <label for="content">内容</label>
        <textarea
          id="content"
          v-model="form.content"
          required
          :disabled="loading"
          rows="20"
          class="form-control editor-textarea"
          placeholder="在此输入文章内容，支持Markdown语法..."
        ></textarea>
      </div>

      <div class="form-actions">
        <router-link to="/articles" class="btn-cancel">取消</router-link>
        <button type="submit" :disabled="loading" class="btn-submit">
          {{ loading ? '处理中...' : isEditMode ? '更新文章' : '保存文章' }}
        </button>
      </div>

      <div v-if="error" class="error-message">
        错误: {{ error }}
      </div>
    </form>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useArticle } from '@/composables/useArticles'
import { useCategories } from '@/composables/useCategories'
import { ArticleStatus } from '@/types/article'

const route = useRoute()
const router = useRouter()

const { article, loading, error, create, update, fetchArticle } = useArticle()
const { categories, fetchCategories } = useCategories()

// 检查是否是编辑模式
const isEditMode = computed(() => !!route.params.id)

// 表单数据
const form = ref({
  title: '',
  content: '',
  author: 'Zewang', // 默认作者为Zewang
  status: ArticleStatus.DRAFT // 默认状态为草稿
})

// 选中的分类ID
const selectedCategoryIds = ref<string[]>([])

// 加载文章数据（编辑模式）
const loadArticle = async () => {
  if (!isEditMode.value) return

  try {
    loading.value = true
    await fetchArticle(route.params.id as string)

    if (article.value) {
      form.value.title = article.value.title
      form.value.content = article.value.content
      form.value.author = article.value.author
      form.value.status = typeof article.value.status === 'number' ? article.value.status : ArticleStatus.DRAFT

      // 设置选中的分类
      if (article.value.categories) {
        selectedCategoryIds.value = article.value.categories.map(c => c.id)
      }
    }
  } catch (err) {
    console.error('加载文章失败:', err)
  } finally {
    loading.value = false
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    let result

    if (isEditMode.value) {
      // 更新文章
      result = await update(route.params.id as string, {
        ...form.value,
        categoryIds: selectedCategoryIds.value
      })

      if (result.success) {
        alert('文章更新成功！')
        router.push(`/article/${route.params.id}`)
      }
    } else {
      // 创建新文章
      result = await create({
        ...form.value,
        categoryIds: selectedCategoryIds.value
      })

      if (result.success) {
        alert('文章创建成功！')
        router.push('/articles')
      }
    }
  } catch (err) {
    console.error('保存文章失败:', err)
  }
}

// 组件挂载时加载数据
onMounted(() => {
  fetchCategories()
  if (isEditMode.value) {
    loadArticle()
  }
})
</script>

<style scoped>
.article-editor {
  max-width: 1200px;
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

.form-control:disabled {
  background-color: #f5f5f5;
  cursor: not-allowed;
}

.editor-textarea {
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 14px;
  line-height: 1.5;
  resize: vertical;
}

.category-section {
  border: 1px solid #ddd;
  border-radius: 4px;
  padding: 15px;
  background-color: #fff;
}

.category-checkboxes {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.category-checkbox {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  user-select: none;
  padding: 5px 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: #fff;
}

.category-checkbox:hover {
  background-color: #f5f5f5;
}

.category-checkbox input[type="checkbox"] {
  margin: 0;
}

.status-options {
  display: flex;
  gap: 20px;
}

.status-option {
  display: flex;
  align-items: center;
  gap: 5px;
  cursor: pointer;
  user-select: none;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 30px;
}

.btn-cancel,
.btn-submit {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  text-decoration: none;
  display: inline-block;
}

.btn-cancel {
  background-color: #6c757d;
  color: white;
}

.btn-cancel:hover {
  background-color: #5a6268;
}

.btn-submit {
  background-color: #42b983;
  color: white;
}

.btn-submit:hover:not(:disabled) {
  background-color: #359c6d;
}

.btn-submit:disabled {
  background-color: #a0a0a0;
  cursor: not-allowed;
}

.error-message {
  margin-top: 15px;
  padding: 10px;
  background-color: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
  border-radius: 4px;
}
</style>