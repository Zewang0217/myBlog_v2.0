<template>
  <div class="drafts-container">
    <h2>草稿箱</h2>

    <div v-if="loading" class="loading">加载中...</div>

    <div v-else-if="error" class="error">
      加载草稿失败: {{ error }}
      <button @click="fetchDrafts" class="retry-btn">重试</button>
    </div>

    <div v-else-if="drafts.length === 0" class="no-drafts">
      暂无草稿
    </div>

    <div v-else class="drafts-list">
      <div v-for="draft in drafts" :key="draft.id" class="draft-item">
        <h3>{{ draft.title || '无标题' }}</h3>
        <div class="draft-meta">
          <span>最后更新: {{ formatDate(draft.updateTime) }}</span>
          <div class="draft-actions">
            <router-link
              :to="{ name: 'ArticleEdit', params: { id: draft.id } }"
              class="btn-edit"
            >
              编辑
            </router-link>
            <button v-if="draft.status === 0"
                    @click="publishDraft(draft.id)"
                    class="btn-publish">
              发布
            </button>
            <button @click="confirmDelete(draft.id)" class="btn-delete">
              删除
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 确认删除对话框 -->
    <div v-if="showDeleteConfirm" class="modal">
      <div class="modal-content">
        <p>确定要删除这篇草稿吗？此操作不可撤销。</p>
        <div class="modal-actions">
          <button @click="showDeleteConfirm = false" class="btn-secondary">取消</button>
          <button @click="deleteDraft" class="btn-delete">确认删除</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useArticles, useArticle } from '@/composables/useArticles'
import type { Article } from '@/types/article'

const router = useRouter()
const { articles, loading, error, fetchDraftArticles } = useArticles()
const drafts = articles
const { deleteArticleById, publish } = useArticle()

// 状态管理
const showDeleteConfirm = ref(false)
const selectedDraftId = ref<number | null>(null)

// 获取草稿列表
const fetchDrafts = async () => {
  // console.log('开始获取草稿列表')
  await fetchDraftArticles()
  // console.log('草稿列表获取成功', articles.value)
}

// 格式化日期
const formatDate = (dateString: string) => {
  return new Date(dateString).toLocaleString()
}

// 发布草稿
const publishDraft = async (id: number) => {
  try {
    loading.value = true
    error.value = null
    const result = await publish(id)
    if (result.success) {
      await fetchDrafts() // 刷新列表
      alert('文章已成功发布')
    }
  } catch (err) {
    console.error('发布文章失败:', err)
    error.value = err instanceof Error ? err.message : '发布文章失败'
  } finally {
    loading.value = false
  }
}

// 确认删除
const confirmDelete = (id: number) => {
  selectedDraftId.value = id
  showDeleteConfirm.value = true
}

// 删除草稿
const deleteDraft = async () => {
  if (selectedDraftId.value === null) return

  try {
    loading.value = true
    error.value = null
    const result = await deleteArticleById(selectedDraftId.value)
    if (result.success) {
      await fetchDrafts() // 刷新列表
      showDeleteConfirm.value = false
    } else {
      error.value = result.error || '删除草稿失败'
    }
  } catch (err) {
    console.error('删除草稿失败:', err)
    error.value = err instanceof Error ? err.message : '删除草稿失败'
  } finally {
    loading.value = false
  }
}

// 组件挂载时获取草稿列表
onMounted(() => {
  fetchDrafts()
})
</script>

<style scoped>
.drafts-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.drafts-list {
  margin-top: 20px;
}

.draft-item {
  background: #fff;
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  padding: 15px;
  margin-bottom: 15px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
}

.draft-item h3 {
  margin: 0 0 10px 0;
  color: #333;
}

.draft-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #666;
  font-size: 0.9em;
}

.draft-actions {
  display: flex;
  gap: 10px;
}

.btn-edit,
.btn-publish,
.btn-delete {
  padding: 5px 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 0.9em;
  transition: background-color 0.2s;
}

.btn-edit {
  background-color: #f0f0f0;
  color: #333;
  text-decoration: none;
}

.btn-edit:hover {
  background-color: #e0e0e0;
}

.btn-publish {
  background-color: #4caf50;
  color: white;
}

.btn-publish:hover {
  background-color: #45a049;
}

.btn-delete {
  background-color: #f44336;
  color: white;
}

.btn-delete:hover {
  background-color: #d32f2f;
}

.loading,
.no-drafts {
  text-align: center;
  padding: 40px;
  color: #666;
}

.error {
  text-align: center;
  padding: 20px;
  color: #f44336;
  background-color: #ffebee;
  border-radius: 4px;
  margin: 20px 0;
}

.retry-btn {
  margin-left: 10px;
  padding: 5px 10px;
  background-color: #f0f0f0;
  border: 1px solid #ddd;
  border-radius: 4px;
  cursor: pointer;
}

/* 模态框样式 */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  padding: 20px;
  border-radius: 8px;
  max-width: 500px;
  width: 90%;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}

.btn-secondary {
  background-color: #f0f0f0;
  color: #333;
  padding: 8px 16px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.btn-secondary:hover {
  background-color: #e0e0e0;
}
</style>
