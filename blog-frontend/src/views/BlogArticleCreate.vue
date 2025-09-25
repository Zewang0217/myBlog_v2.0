<!-- blog-frontend/src/views/BlogArticleCreate.vue -->
<!-- 博客文章创建页面 -->
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
            <button type="button" @click="insertText('```\n', '\n```')" title="代码块">```</button>
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
                required
                :disabled="loading"
                placeholder="在这里输入文章内容，支持Markdown格式..."
                class="editor"
                @input="updatePreview"
              ></textarea>
            </div>
            <div class="preview-wrapper">
              <div class="preview" v-html="previewContent"></div>
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
import { ArticleStatus } from '@/types/article'
import { marked } from 'marked'
import DOMPurify from 'dompurify'

const route = useRoute()
const router = useRouter()
const editor = ref<HTMLTextAreaElement | null>(null)

const { article, loading, error, create, update, fetchArticle } = useArticle()

// 检查是否是编辑模式
const isEditMode = computed(() => !!route.params.id)

// 表单数据
const form = ref({
  title: '',
  content: '',
  author: '',
  status: ArticleStatus.DRAFT
})

// 预览内容
const previewContent = ref('')

// 更新预览
const updatePreview = () => {
  previewContent.value = DOMPurify.sanitize(marked(form.value.content || '*没有内容*'))
}

// 插入文本到光标位置
const insertText = (before: string, after: string) => {
  if (!editor.value) return

  const start = editor.value.selectionStart
  const end = editor.value.selectionEnd
  const selectedText = form.value.content.substring(start, end)

  // 插入文本
  const newText = form.value.content.substring(0, start) + before + selectedText + after + form.value.content.substring(end)
  form.value.content = newText

  // 设置光标位置
  const newCursorPos = start + before.length
  nextTick(() => {
    if (editor.value) {
      editor.value.selectionStart = newCursorPos
      editor.value.selectionEnd = newCursorPos + selectedText.length
      editor.value.focus()
    }
  })

  updatePreview()
}

// 加载文章数据（编辑模式）
const loadArticle = async () => {
  if (!isEditMode.value) return

  const articleId = Number(route.params.id)
  if (!articleId) return

  const result = await fetchArticle(articleId)
  if (result.success && result.data) {
    const articleData = result.data
    form.value = {
      title: articleData.title,
      content: articleData.content,
      author: articleData.author,
      status: articleData.status
    }
    updatePreview()
  }
}

// 提交表单
const handleSubmit = async () => {
  try {
    let result

    if (isEditMode.value) {
      // 更新文章
      result = await update(Number(route.params.id), form.value)
      if (result.success) {
        alert('文章更新成功！')
        router.push(`/article/${route.params.id}`)
      }
    } else {
      // 创建新文章
      result = await create(form.value)
      if (result.success) {
        alert('文章创建成功！')
        router.push('/articles')
      }
    }
  } catch (err) {
    console.error('保存文章失败:', err)
  }
}

// 组件挂载时加载文章数据（如果是编辑模式）
onMounted(() => {
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
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #2c3e50;
}

.form-control {
  width: 100%;
  padding: 10px 12px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  font-size: 14px;
  transition: border-color 0.3s;
}

.form-control:focus {
  outline: none;
  border-color: #42b983;
  box-shadow: 0 0 0 2px rgba(66, 185, 131, 0.2);
}

.status-options {
  display: flex;
  gap: 20px;
  margin-top: 5px;
}

.status-option {
  display: flex;
  align-items: center;
  gap: 6px;
  cursor: pointer;
  user-select: none;
}

.status-option input[type="radio"] {
  margin: 0;
}

.editor-section {
  display: flex;
  flex-direction: column;
  height: 600px;
}

.editor-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  overflow: hidden;
}

.editor-toolbar {
  display: flex;
  flex-wrap: wrap;
  gap: 5px;
  padding: 8px;
  background-color: #f5f7fa;
  border-bottom: 1px solid #e4e7ed;
}

.editor-toolbar button {
  background: white;
  border: 1px solid #dcdfe6;
  border-radius: 3px;
  padding: 4px 8px;
  cursor: pointer;
  font-size: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  min-width: 32px;
  height: 28px;
  transition: all 0.2s;
}

.editor-toolbar button:hover {
  background-color: #f0f2f5;
  border-color: #c0c4cc;
}

.editor-preview-container {
  display: flex;
  flex: 1;
  overflow: hidden;
}

.editor-wrapper,
.preview-wrapper {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.editor-wrapper {
  border-right: 1px solid #e4e7ed;
}

.editor {
  flex: 1;
  padding: 12px;
  border: none;
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
  font-size: 14px;
  line-height: 1.6;
  resize: none;
  background-color: #fafafa;
}

.editor:focus {
  outline: none;
  background-color: white;
}

.preview-wrapper {
  overflow-y: auto;
}

.preview {
  flex: 1;
  padding: 12px;
  background-color: white;
  line-height: 1.6;
  overflow-y: auto;
}

.preview :deep(h1),
.preview :deep(h2),
.preview :deep(h3) {
  margin: 1.2em 0 0.8em;
  color: #2c3e50;
}

.preview :deep(p) {
  margin: 1em 0;
}

.preview :deep(a) {
  color: #42b983;
  text-decoration: none;
}

.preview :deep(a:hover) {
  text-decoration: underline;
}

.preview :deep(code) {
  padding: 0.2em 0.4em;
  margin: 0;
  font-size: 0.9em;
  background-color: #f6f8fa;
  border-radius: 3px;
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
}

.preview :deep(pre) {
  background-color: #f6f8fa;
  border-radius: 3px;
  padding: 16px;
  overflow: auto;
  margin: 1em 0;
}

.preview :deep(blockquote) {
  margin: 1em 0;
  padding: 0.5em 1em;
  border-left: 4px solid #42b983;
  background-color: #f9f9f9;
  color: #666;
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #f0f0f0;
}

.btn-cancel,
.btn-submit {
  padding: 10px 20px;
  border: none;
  border-radius: 4px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.2s;
}

.btn-cancel {
  background-color: #f0f0f0;
  color: #666;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.btn-cancel:hover {
  background-color: #e0e0e0;
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
  background-color: #ffebee;
  color: #e53935;
  border-radius: 4px;
  font-size: 14px;
}

@media (max-width: 768px) {
  .editor-preview-container {
    flex-direction: column;
  }
  
  .editor-wrapper {
    border-right: none;
    border-bottom: 1px solid #e4e7ed;
  }
  
  .form-actions {
    flex-direction: column;
  }

  .btn-cancel,
  .btn-submit {
    width: 100%;
  }
}
</style>