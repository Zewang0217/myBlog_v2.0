<template>
  <div class="comment-manager">
    <h1 class="page-title">评论管理</h1>
    
    <!-- 评论列表 -->
    <div class="comment-list">
      <table class="comment-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>评论内容</th>
            <th>文章ID</th>
            <th>状态</th>
            <th>创建时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="comment in comments" :key="comment.id">
            <td>{{ comment.id }}</td>
            <td>{{ comment.username }}</td>
            <td class="comment-content">{{ comment.content }}</td>
            <td>{{ comment.articleId }}</td>
            <td>
              <span class="status-badge" :class="comment.status === 0 ? 'active' : 'deleted'">
                {{ comment.status === 0 ? '正常' : '已删除' }}
              </span>
            </td>
            <td>{{ formatTime(comment.createTime) }}</td>
            <td class="action-buttons">
              <button class="view-btn" @click="viewComment(comment)">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"></path>
                  <circle cx="12" cy="12" r="3"></circle>
                </svg>
                查看
              </button>
              <button class="status-btn" @click="toggleCommentStatus(comment)">
                <svg v-if="comment.status === 0" xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <line x1="18" y1="6" x2="6" y2="18"></line>
                  <line x1="6" y1="6" x2="18" y2="18"></line>
                </svg>
                <svg v-else xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M22 11.08V12a10 10 0 1 1-5.93-9.14"></path>
                  <polyline points="22 4 12 14.01 9 11.01"></polyline>
                </svg>
                {{ comment.status === 0 ? '删除' : '恢复' }}
              </button>
              <button class="delete-btn" @click="deleteComment(comment.id)">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <polyline points="3 6 5 6 21 6"></polyline>
                  <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                  <line x1="10" y1="11" x2="10" y2="17"></line>
                  <line x1="14" y1="11" x2="14" y2="17"></line>
                </svg>
                彻底删除
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <!-- 评论详情模态框 -->
    <div v-if="showCommentModal" class="modal-overlay" @click="closeCommentModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>评论详情</h2>
          <button class="close-btn" @click="closeCommentModal">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <line x1="18" y1="6" x2="6" y2="18"></line>
              <line x1="6" y1="6" x2="18" y2="18"></line>
            </svg>
          </button>
        </div>
        <div class="modal-body">
          <div v-if="selectedComment" class="comment-detail">
            <div class="detail-item">
              <label>ID:</label>
              <span>{{ selectedComment.id }}</span>
            </div>
            <div class="detail-item">
              <label>用户名:</label>
              <span>{{ selectedComment.username }}</span>
            </div>
            <div class="detail-item">
              <label>文章ID:</label>
              <span>{{ selectedComment.articleId }}</span>
            </div>
            <div class="detail-item">
              <label>评论内容:</label>
              <div class="content-box">{{ selectedComment.content }}</div>
            </div>
            <div class="detail-item">
              <label>父评论ID:</label>
              <span>{{ selectedComment.parentId || '无' }}</span>
            </div>
            <div class="detail-item">
              <label>状态:</label>
              <span class="status-badge" :class="selectedComment.status === 0 ? 'active' : 'deleted'">
                {{ selectedComment.status === 0 ? '正常' : '已删除' }}
              </span>
            </div>
            <div class="detail-item">
              <label>点赞数:</label>
              <span>{{ selectedComment.likes }}</span>
            </div>
            <div class="detail-item">
              <label>创建时间:</label>
              <span>{{ formatTime(selectedComment.createTime) }}</span>
            </div>
            <div class="detail-item">
              <label>更新时间:</label>
              <span>{{ formatTime(selectedComment.updateTime) }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import apiClient from '@/api/apiClient'

// 状态管理
const comments = ref<any[]>([])
const loading = ref(true)
const showCommentModal = ref(false)
const selectedComment = ref<any>(null)

// 获取评论列表
const fetchComments = async () => {
  try {
    loading.value = true
    const response = await apiClient.get('/api/admin/comments')
    comments.value = response.data.data
  } catch (error) {
    console.error('获取评论列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 查看评论详情
const viewComment = (comment: any) => {
  selectedComment.value = comment
  showCommentModal.value = true
}

// 关闭评论详情模态框
const closeCommentModal = () => {
  showCommentModal.value = false
  selectedComment.value = null
}

// 切换评论状态
const toggleCommentStatus = async (comment: any) => {
  try {
    const newStatus = comment.status === 0 ? 1 : 0
    const response = await apiClient.put(`/api/admin/comments/${comment.id}/status`, { status: newStatus })
    comment.status = newStatus
    comment.updateTime = response.data.data.updateTime
  } catch (error) {
    console.error('更新评论状态失败:', error)
  }
}

// 删除评论
const deleteComment = async (id: string) => {
  if (confirm('确定要彻底删除这条评论吗？')) {
    try {
      await apiClient.delete(`/api/admin/comments/${id}`)
      // 从列表中移除删除的评论
      comments.value = comments.value.filter(comment => comment.id !== id)
    } catch (error) {
      console.error('删除评论失败:', error)
    }
  }
}

// 格式化时间为相对时间
const formatTime = (timeString: string) => {
  const date = new Date(timeString)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  const minutes = Math.floor(diff / (1000 * 60))
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  if (minutes < 60) {
    return `${minutes}分钟前`
  } else if (hours < 24) {
    return `${hours}小时前`
  } else if (days < 30) {
    return `${days}天前`
  } else {
    return date.toLocaleDateString()
  }
}

// 初始化
onMounted(() => {
  fetchComments()
})
</script>

<style scoped>
.comment-manager {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-title {
  font-size: 2rem;
  margin-bottom: 2rem;
  color: var(--text-color-primary);
  text-align: center;
}

.comment-list {
  background-color: var(--bg-color-light);
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.comment-table {
  width: 100%;
  border-collapse: collapse;
}

.comment-table th,
.comment-table td {
  padding: 16px;
  text-align: left;
  border-bottom: 1px solid var(--border-color);
  color: var(--text-color-primary);
}

.comment-table th {
  background-color: var(--bg-color);
  font-weight: bold;
}

.comment-table tr:last-child td {
  border-bottom: none;
}

.comment-content {
  max-width: 300px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.status-badge {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 0.8rem;
  font-weight: 500;
}

.status-badge.active {
  background-color: var(--success-color-light);
  color: var(--success-color);
}

.status-badge.deleted {
  background-color: var(--danger-color-light);
  color: var(--danger-color);
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.view-btn,
.status-btn,
.delete-btn {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 6px 12px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 0.9rem;
  transition: all 0.3s ease;
  color: white;
}

.view-btn {
  background-color: var(--primary-color);
}

.view-btn:hover {
  background-color: var(--primary-color-dark);
}

.status-btn {
  background-color: var(--warning-color);
}

.status-btn:hover {
  background-color: var(--warning-color-dark);
}

.delete-btn {
  background-color: var(--danger-color);
}

.delete-btn:hover {
  background-color: var(--danger-color-dark);
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background-color: var(--bg-color-light);
  border-radius: 12px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.15);
  width: 90%;
  max-width: 600px;
  overflow: hidden;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid var(--border-color);
  background-color: var(--bg-color);
}

.modal-header h2 {
  margin: 0;
  color: var(--text-color-primary);
  font-size: 1.5rem;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: var(--text-color-regular);
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background-color 0.3s ease;
}

.close-btn:hover {
  background-color: var(--bg-color-light);
}

.modal-body {
  padding: 20px;
}

.comment-detail {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.detail-item {
  display: flex;
  gap: 12px;
  align-items: flex-start;
}

.detail-item label {
  width: 100px;
  font-weight: 500;
  color: var(--text-color-regular);
  flex-shrink: 0;
}

.detail-item span {
  color: var(--text-color-primary);
  flex: 1;
}

.content-box {
  flex: 1;
  padding: 12px;
  background-color: var(--bg-color);
  border-radius: 8px;
  border: 1px solid var(--border-color);
  color: var(--text-color-primary);
  white-space: pre-wrap;
  word-break: break-word;
}

/* 深色主题样式 */
@media (prefers-color-scheme: dark) {
  .comment-manager {
    background-color: var(--bg-color);
  }
  
  .page-title {
    color: var(--text-color-primary);
  }
  
  .comment-list {
    background-color: var(--bg-color-light);
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3);
  }
  
  .comment-table th,
  .comment-table td {
    color: var(--text-color-primary);
    border-bottom-color: var(--border-color);
  }
  
  .comment-table th {
    background-color: var(--bg-color);
  }
  
  .modal-content {
    background-color: var(--bg-color-light);
    box-shadow: 0 4px 24px rgba(0, 0, 0, 0.3);
  }
  
  .modal-header {
    background-color: var(--bg-color);
    border-bottom-color: var(--border-color);
  }
  
  .modal-header h2 {
    color: var(--text-color-primary);
  }
  
  .detail-item label {
    color: var(--text-color-regular);
  }
  
  .detail-item span {
    color: var(--text-color-primary);
  }
  
  .content-box {
    background-color: var(--bg-color);
    border-color: var(--border-color);
    color: var(--text-color-primary);
  }
}
</style>
