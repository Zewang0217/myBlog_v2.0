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
          <!-- 分类选择 -->
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

          <!-- 添加新分类 -->
          <div class="add-category-section">
            <div class="add-category-form" v-if="showAddCategoryForm">
              <input
                v-model="newCategory.name"
                type="text"
                placeholder="新分类名称"
                class="form-control"
                :disabled="addingCategory"
              />
              <textarea
                v-model="newCategory.description"
                placeholder="分类描述（可选）"
                class="form-control"
                :disabled="addingCategory"
              ></textarea>
              <div class="add-category-actions">
                <button
                  type="button"
                  @click="showAddCategoryForm = false"
                  class="btn-cancel-small"
                  :disabled="addingCategory"
                >
                  取消
                </button>
                <button
                  type="button"
                  @click="addNewCategory"
                  class="btn-submit-small"
                  :disabled="addingCategory || !newCategory.name.trim()"
                >
                  {{ addingCategory ? '添加中...' : '添加' }}
                </button>
              </div>
            </div>
            <button
              type="button"
              @click="showAddCategoryForm = true"
              v-if="!showAddCategoryForm"
              class="btn-add-category"
            >
              + 添加新分类
            </button>
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

      <div class="form-group editor-section">
        <label for="content">内容</label>
        <div class="editor-container">
          <div class="editor-toolbar">
            <button type="button" @click="insertText('# ', '')" title="标题">H1</button>
            <button type="button" @click="insertText('## ', '')" title="副标题">H2</button>
            <button type="button" @click="insertText('**', '**')" title="加粗"><strong>B</strong></button>
            <button type="button" @click="insertText('*', '*')" title="斜体"><em>I</em></button>
            <button type="button" @click="insertText('`', '`')" title="行内代码"><code>code</code></button>
            <button type="button" @click="insertText('```\n', '\n```')" title="代码块">代码块</button>
            <button type="button" @click="insertText('- ', '')" title="无序列表">•</button>
            <button type="button" @click="insertText('1. ', '')" title="有序列表">1.</button>
            <button type="button" @click="insertText('> ', '')" title="引用">&gt;</button>
            <button type="button" @click="insertText('[链接文字](url)', '')" title="链接">🔗</button>
            <button type="button" @click="insertText('![描述](图片URL)', '')" title="图片">🖼️</button>
          </div>
          <div class="editor-preview-container">
            <div class="editor-wrapper">
              <textarea
                id="content"
                ref="editor"
                v-model="form.content"
                :disabled="loading"
                @input="updatePreview"
                class="editor"
                placeholder="在此输入文章内容，支持Markdown语法..."
              ></textarea>
            </div>
            <div class="preview-wrapper">
              <div class="preview-content" v-html="previewContent"></div>
            </div>
          </div>
        </div>
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
import { ref, computed, onMounted, nextTick } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useArticle } from '@/composables/useArticles'
import { useCategories } from '@/composables/useCategories'
import { ArticleStatus } from '@/types/article'
import { marked } from 'marked'
import DOMPurify from 'dompurify'

const route = useRoute()
const router = useRouter()
const editor = ref<HTMLTextAreaElement | null>(null)

const { article, loading, error, create, update, fetchArticle } = useArticle()
const {
  categories,
  loading: categoriesLoading,
  fetchCategories,
  createCategory
} = useCategories()

// 检查是否是编辑模式
const isEditMode = computed(() => !!route.params.id)

// 表单数据
const form = ref({
  title: '',
  content: '',
  author: '',
  status: ArticleStatus.DRAFT
})

// 选中的分类ID
const selectedCategoryIds = ref<string[]>([])

// 添加分类相关状态
const showAddCategoryForm = ref(false)
const newCategory = ref({ name: '', description: '' })
const addingCategory = ref(false)

// 预览内容
const previewContent = ref('')

// 更新预览
const updatePreview = () => {
  previewContent.value = DOMPurify.sanitize(marked(form.value.content || '*没有内容*'))
}

// 插入文本到光标位置
const insertText = (before: string, after: string) => {
  if (!editor.value) return

  const textarea = editor.value
  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  const text = textarea.value
  const beforeText = text.substring(0, start)
  const afterText = text.substring(end, text.length)

  textarea.value = beforeText + before + text.substring(start, end) + after + afterText
  form.value.content = textarea.value
  textarea.focus()
  textarea.setSelectionRange(start + before.length, end + before.length)
  updatePreview()
}

// 添加新分类
const addNewCategory = async () => {
  if (!newCategory.value.name.trim()) return

  addingCategory.value = true
  try {
    // 调用API创建新分类
    await createCategory(newCategory.value)

    // 重新获取分类列表
    await fetchCategories()

    // 清空表单并隐藏添加表单
    newCategory.value = { name: '', description: '' }
    showAddCategoryForm.value = false

    alert('分类添加成功！')
  } catch (err: any) {
    console.error('添加分类失败:', err)
    if (err.response && err.response.status === 403) {
      alert('权限不足：只有管理员可以添加分类')
    } else {
      alert('添加分类失败: ' + (err.message || '未知错误'))
    }
  } finally {
    addingCategory.value = false
  }
}

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
      form.value.status = article.value.status

      // 设置选中的分类（需要后端返回文章的分类信息）
      if (article.value.categories) {
        selectedCategoryIds.value = article.value.categories.map(c => c.id)
      }

      await nextTick()
      updatePreview()
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
      // 更新文章（需要包含分类信息）
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
  } else {
    updatePreview()
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
  margin-bottom: 15px;
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

.add-category-section {
  border-top: 1px solid #eee;
  padding-top: 15px;
}

.add-category-form {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.add-category-form textarea {
  min-height: 60px;
  resize: vertical;
}

.add-category-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

.btn-add-category {
  padding: 8px 12px;
  background-color: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn-add-category:hover {
  background-color: #359c6d;
}

.btn-cancel-small,
.btn-submit-small {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
}

.btn-cancel-small {
  background-color: #6c757d;
  color: white;
}

.btn-cancel-small:hover {
  background-color: #5a6268;
}

.btn-submit-small {
  background-color: #42b983;
  color: white;
}

.btn-submit-small:hover:not(:disabled) {
  background-color: #359c6d;
}

.btn-submit-small:disabled {
  background-color: #a0a0a0;
  cursor: not-allowed;
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

.editor-container {
  border: 1px solid #ddd;
  border-radius: 4px;
  overflow: hidden;
}

.editor-toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
  padding: 10px;
  background-color: #f5f5f5;
  border-bottom: 1px solid #ddd;
}

.editor-toolbar button {
  padding: 5px 10px;
  background-color: #fff;
  border: 1px solid #ccc;
  border-radius: 3px;
  cursor: pointer;
  font-size: 14px;
}

.editor-toolbar button:hover {
  background-color: #e9e9e9;
}

.editor-preview-container {
  display: flex;
  height: 500px;
}

.editor-wrapper,
.preview-wrapper {
  flex: 1;
  height: 100%;
  overflow: auto;
}

.editor-wrapper {
  border-right: 1px solid #ddd;
}

.editor {
  width: 100%;
  height: 100%;
  padding: 15px;
  border: none;
  resize: none;
  font-family: 'Monaco', 'Menlo', 'Ubuntu Mono', monospace;
  font-size: 14px;
  line-height: 1.5;
  box-sizing: border-box;
}

.editor:focus {
  outline: none;
}

.preview-content {
  padding: 15px;
  background-color: #fff;
}

.preview-content :deep(h1),
.preview-content :deep(h2),
.preview-content :deep(h3) {
  margin: 1.2em 0 0.8em;
  color: #2c3e50;
}

.preview-content :deep(p) {
  margin: 1em 0;
}

.preview-content :deep(a) {
  color: #42b983;
  text-decoration: none;
}

.preview-content :deep(a:hover) {
  text-decoration: underline;
}

.preview-content :deep(code) {
  padding: 0.2em 0.4em;
  margin: 0;
  font-size: 0.9em;
  background-color: #f6f8fa;
  border-radius: 3px;
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
}

.preview-content :deep(pre) {
  background-color: #f6f8fa;
  border-radius: 3px;
  padding: 16px;
  overflow: auto;
  margin: 1em 0;
}

.preview-content :deep(blockquote) {
  margin: 1em 0;
  padding: 0.5em 1em;
  border-left: 4px solid #42b983;
  background-color: #f9f9f9;
  color: #666;
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
