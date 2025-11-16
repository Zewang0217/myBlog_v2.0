<template>
  <div class="simple-markdown-editor">
    <div class="editor-header">
      <div class="toolbar">
        <button
          v-for="tool in toolbarTools"
          :key="tool.name"
          :title="tool.title"
          @click="tool.action"
          class="toolbar-btn"
        >
          <svg 
            :width="tool.icon.width" 
            :height="tool.icon.height" 
            viewBox="0 0 24 24" 
            fill="currentColor"
          >
            <path :d="tool.icon.path" />
          </svg>
        </button>
        <!-- 文件上传按钮 -->
        <div class="file-upload-wrapper">
          <button 
            title="上传文件" 
            @click="triggerFileUpload" 
            class="toolbar-btn"
          >
            <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
              <path d="M19.35 10.04C18.67 6.59 15.64 4 12 4 9.11 4 6.6 5.64 5.35 8.04 2.34 8.36 0 10.91 0 14c0 3.31 2.69 6 6 6h13c2.76 0 5-2.24 5-5 0-2.64-2.05-4.78-4.65-4.96zM14 13v4h-4v-4H7l5-5 5 5h-3z"/>
            </svg>
          </button>
          <input
            ref="fileInputRef"
            type="file"
            @change="handleFileUpload"
            class="file-input"
            accept="image/*,video/*,audio/*"
            multiple
          />
        </div>
      </div>
    </div>

    <textarea
      ref="editorRef"
      v-model="content"
      class="editor-textarea"
      :placeholder="placeholder"
      @input="handleInput"
      @keydown="handleKeydown"
    ></textarea>

    <div class="editor-footer">
      <div class="status-info">
        <span class="word-count">字数: {{ wordCount }}</span>
        <span class="char-count">字符: {{ characterCount }}</span>
      </div>
    </div>
    
    <!-- 上传进度指示器 -->
    <div v-if="uploading" class="upload-overlay">
      <div class="upload-progress">
        <div class="spinner"></div>
        <p>文件上传中...</p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, nextTick, onMounted, onUnmounted } from 'vue'
import apiClient from '@/api/apiClient'

// 定义 props
interface Props {
  modelValue: string
  placeholder?: string
}

const props = withDefaults(defineProps<Props>(), {
  placeholder: '开始输入您的内容...'
})

// 定义 emits
const emit = defineEmits<{
  'update:modelValue': [value: string]
  'auto-save': [value: string]
}>()

// 响应式数据
const content = computed({
  get: () => props.modelValue,
  set: (value) => emit('update:modelValue', value)
})

const editorRef = ref<HTMLTextAreaElement | null>(null)
const fileInputRef = ref<HTMLInputElement | null>(null)
const uploading = ref(false)

// 自动保存相关
const autoSaveTimer = ref<number | null>(null)
const lastSavedContent = ref(props.modelValue)

// 计算字数和字符数
const wordCount = computed(() => {
  return content.value.trim() ? content.value.trim().split(/\s+/).length : 0
})

const characterCount = computed(() => {
  return content.value.length
})

// 工具栏工具定义
const toolbarTools = [
  {
    name: 'bold',
    title: '粗体 (Ctrl+B)',
    action: () => formatText('bold'),
    icon: {
      width: 20,
      height: 20,
      path: 'M15.6 10.79c.97-.67 1.65-1.77 1.65-2.79 0-2.26-1.75-4-4-4H7v14h7.04c2.09 0 3.71-1.7 3.71-3.79 0-1.52-.86-2.82-2.15-3.42zM10 6.5h3c.83 0 1.5.67 1.5 1.5s-.67 1.5-1.5 1.5h-3v-3zm3.5 9H10v-3h3.5c.83 0 1.5.67 1.5 1.5s-.67 1.5-1.5 1.5z'
    }
  },
  {
    name: 'italic',
    title: '斜体 (Ctrl+I)',
    action: () => formatText('italic'),
    icon: {
      width: 20,
      height: 20,
      path: 'M10 4v3h2.21l-3.42 8H6v3h8v-3h-2.21l3.42-8H18V4h-8z'
    }
  },
  {
    name: 'heading',
    title: '标题 (Ctrl+H)',
    action: () => formatText('heading'),
    icon: {
      width: 20,
      height: 20,
      path: 'M12 3v10.55c-.59-.34-1.27-.55-2-.55-2.21 0-4 1.79-4 4s1.79 4 4 4 4-1.79 4-4V7h4V3h-6z'
    }
  },
  {
    name: 'link',
    title: '链接 (Ctrl+K)',
    action: () => formatText('link'),
    icon: {
      width: 20,
      height: 20,
      path: 'M3.9 12c0-1.71 1.39-3.1 3.1-3.1h4V7H7c-2.76 0-5 2.24-5 5s2.24 5 5 5h4v-1.9H7c-1.71 0-3.1-1.39-3.1-3.1zM8 13h8v-2H8v2zm9-6h-4v1.9h4c1.71 0 3.1 1.39 3.1 3.1s-1.39 3.1-3.1 3.1h-4V17h4c2.76 0 5-2.24 5-5s-2.24-5-5-5z'
    }
  },
  {
    name: 'code',
    title: '代码 (Ctrl+Shift+K)',
    action: () => formatText('code'),
    icon: {
      width: 20,
      height: 20,
      path: 'M8 3v2l4.5 4.5L8 14v2l6.5-6.5L8 3zm8 0v2l4.5 4.5L16 14v2l6.5-6.5L16 3z'
    }
  },
  {
    name: 'quote',
    title: '引用 (Ctrl+Q)',
    action: () => formatText('quote'),
    icon: {
      width: 20,
      height: 20,
      path: 'M6 10.5c-.83 0-1.5.67-1.5 1.5s.67 1.5 1.5 1.5 1.5-.67 1.5-1.5-.67-1.5-1.5-1.5zm0-6c-.83 0-1.5.67-1.5 1.5S5.17 7.5 6 7.5 7.5 6.83 7.5 6 6.83 4.5 6 4.5zm0 12c-.83 0-1.5.68-1.5 1.5s.68 1.5 1.5 1.5 1.5-.68 1.5-1.5-.67-1.5-1.5-1.5zM10 19h10v-2H10v2zm0-6h10v-2H10v2zm0-8v2h10V5h-10z'
    }
  },
  {
    name: 'list',
    title: '列表 (Ctrl+U)',
    action: () => formatText('list'),
    icon: {
      width: 20,
      height: 20,
      path: 'M4 10.5c-.83 0-1.5.67-1.5 1.5s.67 1.5 1.5 1.5 1.5-.67 1.5-1.5-.67-1.5-1.5-1.5zm0-6c-.83 0-1.5.67-1.5 1.5S3.17 7.5 4 7.5 5.5 6.83 5.5 6 4.83 4.5 4 4.5zm0 12c-.83 0-1.5.68-1.5 1.5s.68 1.5 1.5 1.5 1.5-.68 1.5-1.5-.67-1.5-1.5-1.5zM7 19h14v-2H7v2zm0-6h14v-2H7v2zm0-8v2h14V5H7z'
    }
  },
  {
    name: 'image',
    title: '图片 (Ctrl+Shift+I)',
    action: () => formatText('image'),
    icon: {
      width: 20,
      height: 20,
      path: 'M21 19V5c0-1.1-.9-2-2-2H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2zM8.5 13.5l2.5 3.01L14.5 12l4.5 6H5l3.5-4.5z'
    }
  }
]

// 格式化文本
const formatText = (type: 'bold' | 'italic' | 'heading' | 'link' | 'code' | 'quote' | 'list' | 'image') => {
  if (!editorRef.value) return
  
  const textarea = editorRef.value
  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  const selectedText = content.value.substring(start, end)
  
  let newText = ''
  let newCursorPos = start
  
  switch (type) {
    case 'bold':
      newText = `**${selectedText || '粗体文字'}**`
      newCursorPos = start + 2
      break
    case 'italic':
      newText = `*${selectedText || '斜体文字'}*`
      newCursorPos = start + 1
      break
    case 'heading':
      const currentLineStart = content.value.lastIndexOf('\n', start - 1) + 1
      const currentLineEnd = content.value.indexOf('\n', start)
      const lineEnd = currentLineEnd === -1 ? content.value.length : currentLineEnd
      const currentLine = content.value.substring(currentLineStart, lineEnd)
      
      // 检查当前行是否已经是标题
      if (currentLine.startsWith('#')) {
        // 如果已经是标题，移除标题标记
        const cleanedLine = currentLine.replace(/^#+\s*/, '')
        newText = cleanedLine
        newCursorPos = currentLineStart
        content.value = content.value.substring(0, currentLineStart) + newText + content.value.substring(lineEnd)
        return
      } else {
        // 添加标题标记
        newText = `# ${currentLine || '标题'}`
        newCursorPos = currentLineStart + 2
        content.value = content.value.substring(0, currentLineStart) + newText + content.value.substring(lineEnd)
        return
      }
    case 'link':
      newText = `[${selectedText || '链接文字'}](http://example.com)`
      newCursorPos = start + 1
      break
    case 'code':
      if (selectedText.includes('\n')) {
        // 代码块
        newText = `\`\`\`\n${selectedText || '代码内容'}\n\`\`\``
        newCursorPos = start + 4
      } else {
        // 行内代码
        newText = '`' + (selectedText || '代码') + '`'
        newCursorPos = start + 1
      }
      break
    case 'quote':
      newText = `> ${selectedText || '引用文字'}`
      newCursorPos = start + 2
      break
    case 'list':
      newText = `- ${selectedText || '列表项'}`
      newCursorPos = start + 2
      break
    case 'image':
      newText = `![${selectedText || '图片描述'}](http://example.com/image.jpg)`
      newCursorPos = start + 2
      break
    default:
      return
  }
  
  content.value = content.value.substring(0, start) + newText + content.value.substring(end)
  newCursorPos = start + newText.length
  
  // 在下一次 DOM 更新后设置光标位置
  nextTick(() => {
    if (editorRef.value) {
      editorRef.value.focus()
      editorRef.value.setSelectionRange(newCursorPos, newCursorPos)
    }
  })
}

// 处理键盘快捷键
const handleKeydown = (e: KeyboardEvent) => {
  // Ctrl+B - 粗体
  if (e.ctrlKey && e.key === 'b') {
    e.preventDefault()
    formatText('bold')
  }
  // Ctrl+I - 斜体
  if (e.ctrlKey && e.key === 'i') {
    e.preventDefault()
    formatText('italic')
  }
  // Ctrl+H - 标题
  if (e.ctrlKey && e.key === 'h') {
    e.preventDefault()
    formatText('heading')
  }
  // Ctrl+K - 链接
  if (e.ctrlKey && e.key === 'k') {
    e.preventDefault()
    formatText('link')
  }
  // Ctrl+Q - 引用
  if (e.ctrlKey && e.key === 'q') {
    e.preventDefault()
    formatText('quote')
  }
  // Ctrl+U - 列表
  if (e.ctrlKey && e.key === 'u') {
    e.preventDefault()
    formatText('list')
  }
  // Ctrl+Shift+K - 代码
  if (e.ctrlKey && e.shiftKey && e.key === 'K') {
    e.preventDefault()
    formatText('code')
  }
  // Ctrl+Shift+I - 图片
  if (e.ctrlKey && e.shiftKey && e.key === 'I') {
    e.preventDefault()
    formatText('image')
  }
}

// 处理输入事件
const handleInput = () => {
  // 更新内容
  content.value = content.value
}

// 组件挂载时启动自动保存
onMounted(() => {
  startAutoSave()
})

// 组件卸载时清理定时器
onUnmounted(() => {
  if (autoSaveTimer.value) {
    clearInterval(autoSaveTimer.value)
  }
})

// 自动保存功能
const startAutoSave = () => {
  // 每30秒自动保存一次
  autoSaveTimer.value = window.setInterval(() => {
    if (content.value !== lastSavedContent.value) {
      lastSavedContent.value = content.value
      // 触发自动保存事件
      emit('auto-save', content.value)
    }
  }, 30000)
}

// 触发文件上传
const triggerFileUpload = () => {
  if (fileInputRef.value) {
    fileInputRef.value.click()
  }
}

// 处理文件上传
const handleFileUpload = async (event: Event) => {
  const input = event.target as HTMLInputElement
  if (!input.files || input.files.length === 0) return

  uploading.value = true
  
  try {
    const files = Array.from(input.files)
    
    // 依次上传文件
    for (const file of files) {
      const formData = new FormData()
      formData.append('file', file)
      
      try {
        const response = await apiClient.post('/api/file/upload', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          }
        })
        
        if (response.data.code === 200) {
          // 插入图片到编辑器
          const fileUrl = response.data.data.url
          const fileName = response.data.data.originalName
          
          // 根据文件类型生成Markdown语法
          let markdownText = ''
          if (file.type.startsWith('image/')) {
            markdownText = `![${fileName}](${fileUrl})\n`
          } else {
            markdownText = `[${fileName}](${fileUrl})\n`
          }
          
          insertTextAtCursor(markdownText)
        } else {
          console.error('文件上传失败:', response.data.message)
          alert(`文件上传失败: ${response.data.message}`)
        }
      } catch (error) {
        console.error('文件上传错误:', error)
        alert('文件上传过程中发生错误')
      }
    }
  } finally {
    uploading.value = false
    // 清空文件输入框
    if (input) {
      input.value = ''
    }
  }
}

// 在光标位置插入文本
const insertTextAtCursor = (text: string) => {
  if (!editorRef.value) return
  
  const textarea = editorRef.value
  const start = textarea.selectionStart
  const end = textarea.selectionEnd
  
  const newValue = content.value.substring(0, start) + text + content.value.substring(end)
  content.value = newValue
  
  // 设置光标位置
  nextTick(() => {
    if (editorRef.value) {
      const newCursorPos = start + text.length
      editorRef.value.focus()
      editorRef.value.setSelectionRange(newCursorPos, newCursorPos)
    }
  })
}
</script>

<style scoped>
.simple-markdown-editor {
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
  background: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  height: 100%;
}

.editor-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background-color: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
}

.toolbar {
  display: flex;
  gap: 4px;
}

.toolbar-btn {
  width: 36px;
  height: 36px;
  border: 1px solid #dee2e6;
  background: white;
  border-radius: 6px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s ease;
  color: #495057;
}

.toolbar-btn:hover {
  background-color: #e9ecef;
  border-color: #adb5bd;
}

.editor-textarea {
  flex: 1;
  width: 100%;
  padding: 16px;
  border: none;
  resize: none;
  font-family: 'SFMono-Regular', Consolas, 'Liberation Mono', Menlo, monospace;
  font-size: 14px;
  line-height: 1.6;
  outline: none;
  background-color: #fafafa;
}

.editor-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  background-color: #f8f9fa;
  border-top: 1px solid #e9ecef;
  font-size: 14px;
  color: #6c757d;
}

.status-info {
  display: flex;
  gap: 16px;
}

/* 文件上传相关样式 */
.file-upload-wrapper {
  position: relative;
  display: inline-block;
}

.file-input {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  opacity: 0;
  cursor: pointer;
}

/* 上传进度指示器 */
.upload-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(255, 255, 255, 0.8);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.upload-progress {
  text-align: center;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.spinner {
  border: 4px solid #f3f3f3;
  border-top: 4px solid #42b983;
  border-radius: 50%;
  width: 30px;
  height: 30px;
  animation: spin 1s linear infinite;
  margin: 0 auto 10px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .editor-header {
    flex-direction: column;
    gap: 12px;
    align-items: stretch;
  }
}
</style>