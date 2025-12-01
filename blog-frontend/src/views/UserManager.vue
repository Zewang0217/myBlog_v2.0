<template>
  <div class="user-manager">
    <h1 class="page-title">用户管理</h1>
    
    <!-- 添加用户按钮 -->
    <div class="action-bar">
      <button class="add-user-btn" @click="showAddModal = true">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <line x1="12" y1="5" x2="12" y2="19"></line>
          <line x1="5" y1="12" x2="19" y2="12"></line>
        </svg>
        添加用户
      </button>
    </div>
    
    <!-- 用户列表 -->
    <div class="user-list">
      <table class="user-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>用户名</th>
            <th>角色</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="user in users" :key="user.id">
            <td>{{ user.id }}</td>
            <td>{{ user.username }}</td>
            <td>{{ user.role }}</td>
            <td class="action-buttons">
              <button class="edit-btn" @click="editUser(user)">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <path d="M11 4H4a2 2 0 0 0-2 2v14a2 2 0 0 0 2 2h14a2 2 0 0 0 2-2v-7"></path>
                  <path d="M18.5 2.5a2.121 2.121 0 0 1 3 3L12 15l-4 1 1-4 9.5-9.5z"></path>
                </svg>
                编辑
              </button>
              <button class="delete-btn" @click="deleteUser(user.id)" :disabled="user.role === 'ROLE_ADMIN'">
                <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <polyline points="3 6 5 6 21 6"></polyline>
                  <path d="M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6m3 0V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2"></path>
                  <line x1="10" y1="11" x2="10" y2="17"></line>
                  <line x1="14" y1="11" x2="14" y2="17"></line>
                </svg>
                删除
              </button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>
    
    <!-- 添加/编辑用户模态框 -->
    <div v-if="showAddModal || showEditModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h2>{{ showEditModal ? '编辑用户' : '添加用户' }}</h2>
          <button class="close-btn" @click="closeModal">
            <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <line x1="18" y1="6" x2="6" y2="18"></line>
              <line x1="6" y1="6" x2="18" y2="18"></line>
            </svg>
          </button>
        </div>
        <div class="modal-body">
          <form @submit.prevent="saveUser">
            <div class="form-group">
              <label for="username">用户名</label>
              <input 
                type="text" 
                id="username" 
                v-model="formData.username" 
                required 
                placeholder="请输入用户名"
              >
            </div>
            <div class="form-group">
              <label for="password">密码 {{ showEditModal ? '(留空则不修改)' : '' }}</label>
              <input 
                type="password" 
                id="password" 
                v-model="formData.password" 
                :required="!showEditModal"
                placeholder="请输入密码"
              >
            </div>
            <div class="form-group">
              <label for="role">角色</label>
              <select id="role" v-model="formData.role" required>
                <option value="ROLE_USER">普通用户</option>
                <option value="ROLE_ADMIN">管理员</option>
              </select>
            </div>
            <div class="form-actions">
              <button type="button" class="cancel-btn" @click="closeModal">取消</button>
              <button type="submit" class="save-btn">{{ showEditModal ? '保存修改' : '添加用户' }}</button>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import apiClient from '@/api/apiClient'

// 状态管理
const users = ref<any[]>([])
const loading = ref(true)
const showAddModal = ref(false)
const showEditModal = ref(false)
const editingUserId = ref<string | null>(null)

// 表单数据
const formData = ref({
  username: '',
  password: '',
  role: 'ROLE_USER'
})

// 获取用户列表
const fetchUsers = async () => {
  try {
    loading.value = true
    const response = await apiClient.get('/api/admin/users')
    users.value = response.data.data
  } catch (error) {
    console.error('获取用户列表失败:', error)
  } finally {
    loading.value = false
  }
}

// 编辑用户
const editUser = (user: any) => {
  editingUserId.value = user.id
  formData.value = {
    username: user.username,
    password: '', // 密码留空，修改时需要重新输入
    role: user.role
  }
  showEditModal.value = true
}

// 删除用户
const deleteUser = async (id: string) => {
  if (confirm('确定要删除这个用户吗？')) {
    try {
      await apiClient.delete(`/api/admin/users/${id}`)
      await fetchUsers() // 重新获取用户列表
    } catch (error) {
      console.error('删除用户失败:', error)
    }
  }
}

// 保存用户（添加或编辑）
const saveUser = async () => {
  try {
    if (showEditModal.value && editingUserId.value) {
      // 编辑用户
      await apiClient.put(`/api/admin/users/${editingUserId.value}`, formData.value)
    } else {
      // 添加用户
      await apiClient.post('/api/admin/users', formData.value)
    }
    
    // 关闭模态框并刷新列表
    closeModal()
    await fetchUsers()
  } catch (error) {
    console.error('保存用户失败:', error)
  }
}

// 关闭模态框
const closeModal = () => {
  showAddModal.value = false
  showEditModal.value = false
  editingUserId.value = null
  // 重置表单
  formData.value = {
    username: '',
    password: '',
    role: 'ROLE_USER'
  }
}

// 初始化
onMounted(() => {
  fetchUsers()
})
</script>

<style scoped>
.user-manager {
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

.action-bar {
  margin-bottom: 20px;
  display: flex;
  justify-content: flex-end;
}

.add-user-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  background-color: var(--primary-color);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  transition: background-color 0.3s ease;
}

.add-user-btn:hover {
  background-color: var(--primary-color-dark);
}

.user-list {
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.user-table {
  width: 100%;
  border-collapse: collapse;
}

.user-table th,
.user-table td {
  padding: 16px;
  text-align: left;
  border-bottom: 1px solid var(--border-color);
}

.user-table th {
  background-color: var(--bg-color-light);
  font-weight: bold;
  color: var(--text-color-primary);
}

.user-table tr:last-child td {
  border-bottom: none;
}

.action-buttons {
  display: flex;
  gap: 10px;
}

.edit-btn,
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
}

.edit-btn {
  background-color: var(--warning-color);
  color: white;
}

.edit-btn:hover {
  background-color: var(--warning-color-dark);
}

.delete-btn {
  background-color: var(--danger-color);
  color: white;
}

.delete-btn:hover:not(:disabled) {
  background-color: var(--danger-color-dark);
}

.delete-btn:disabled {
  background-color: var(--disabled-color);
  cursor: not-allowed;
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
  background-color: white;
  border-radius: 12px;
  box-shadow: 0 4px 24px rgba(0, 0, 0, 0.15);
  width: 90%;
  max-width: 500px;
  overflow: hidden;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid var(--border-color);
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

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: var(--text-color-primary);
  font-weight: 500;
}

.form-group input,
.form-group select {
  width: 100%;
  padding: 12px;
  border: 1px solid var(--border-color);
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.3s ease;
}

.form-group input:focus,
.form-group select:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.1);
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 30px;
}

.cancel-btn,
.save-btn {
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  transition: all 0.3s ease;
}

.cancel-btn {
  background-color: var(--bg-color-light);
  color: var(--text-color-primary);
}

.cancel-btn:hover {
  background-color: var(--border-color);
}

.save-btn {
  background-color: var(--success-color);
  color: white;
}

.save-btn:hover {
  background-color: var(--success-color-dark);
}
/* 深色主题样式 */
@media (prefers-color-scheme: dark) {
  .user-manager {
    background-color: var(--bg-color);
  }
  
  .page-title {
    color: var(--text-color-primary);
  }
  
  .user-list {
    background-color: var(--bg-color-light);
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.3);
  }
  
  .user-table th,
  .user-table td {
    color: var(--text-color-primary);
    border-bottom-color: var(--border-color);
  }
  
  .user-table th {
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
  
  .form-group label {
    color: var(--text-color-primary);
  }
  
  .form-group input,
  .form-group select {
    background-color: var(--bg-color);
    color: var(--text-color-primary);
    border-color: var(--border-color);
  }
  
  .form-group input:focus,
  .form-group select:focus {
    border-color: var(--primary-color);
    box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
  }
  
  .cancel-btn {
    background-color: var(--bg-color);
    color: var(--text-color-primary);
    border: 1px solid var(--border-color);
  }
  
  .cancel-btn:hover {
    background-color: var(--border-color);
  }
  
  .no-activities {
    color: var(--text-color-regular);
  }
}
</style>
