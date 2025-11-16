<!-- src/views/CategoryManager.vue -->
<template>
  <div class="category-manager">
    <div class="header">
      <h2>分类管理</h2>
      <p class="subtitle">管理您的博客文章分类</p>
    </div>

    <!-- 添加分类表单 -->
    <div class="add-category-form card">
      <h3>添加新分类</h3>
      <form @submit.prevent="addCategory">
        <div class="form-group">
          <label for="categoryName">分类名称</label>
          <input
            id="categoryName"
            v-model="newCategory.name"
            type="text"
            required
            class="form-control"
            placeholder="输入分类名称"
          />
        </div>
        <div class="form-group">
          <label for="categoryDescription">分类描述</label>
          <textarea
            id="categoryDescription"
            v-model="newCategory.description"
            class="form-control"
            placeholder="输入分类描述（可选）"
            rows="3"
          ></textarea>
        </div>
        <button type="submit" :disabled="addingCategory" class="btn-submit">
          {{ addingCategory ? '添加中...' : '添加分类' }}
        </button>
      </form>
    </div>

    <!-- 分类列表 -->
    <div class="category-list card">
      <div class="list-header">
        <h3>现有分类</h3>
        <div class="list-actions">
          <button @click="fetchCategories" class="btn-refresh">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
              <path d="M17.65 6.35C16.2 4.9 14.21 4 12 4c-4.42 0-7.99 3.58-7.99 8s3.57 8 7.99 8c3.73 0 6.84-2.55 7.73-6h-2.08c-.82 2.33-3.04 4-5.65 4-3.31 0-6-2.69-6-6s2.69-6 6-6c1.66 0 3.14.69 4.22 1.78L13 11h7V4l-2.35 2.35z"/>
            </svg>
            刷新
          </button>
        </div>
      </div>
      
      <div v-if="loadingCategories" class="loading">
        <div class="spinner"></div>
        <span>加载中...</span>
      </div>
      <div v-else-if="categories.length === 0" class="empty">
        <svg width="48" height="48" viewBox="0 0 24 24" fill="currentColor">
          <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8zm-1-13h2v6h-2V7zm0 8h2v2h-2v-2z"/>
        </svg>
        <p>暂无分类</p>
      </div>
      <div v-else class="categories-grid">
        <div
          v-for="category in categories"
          :key="category.id"
          class="category-card"
        >
          <div class="category-content">
            <div class="category-header">
              <h4 class="category-name">{{ category.name }}</h4>
              <span class="category-count">{{ category.articleCount || 0 }} 篇文章</span>
            </div>
            <p v-if="category.description" class="category-description">{{ category.description }}</p>
          </div>
          <div class="category-actions">
            <button
              @click="editCategory(category)"
              class="btn-edit"
            >
              <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                <path d="M3 17.25V21h3.75L17.81 9.94l-3.75-3.75L3 17.25zM20.71 7.04c.39-.39.39-1.02 0-1.41l-2.34-2.34c-.39-.39-1.02-.39-1.41 0l-1.83 1.83 3.75 3.75 1.83-1.83z"/>
              </svg>
            </button>
            <button
              @click="deleteCategory(category.id)"
              :disabled="deletingCategory"
              class="btn-delete"
            >
              <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
                <path d="M6 19c0 1.1.9 2 2 2h8c1.1 0 2-.9 2-2V7H6v12zM19 4h-3.5l-1-1h-5l-1 1H5v2h14V4z"/>
              </svg>
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑分类模态框 -->
    <div v-if="editingCategory" class="modal-overlay" @click="closeEditModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>编辑分类</h3>
          <button class="modal-close" @click="closeEditModal">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
              <path d="M19 6.41L17.59 5 12 10.59 6.41 5 5 6.41 10.59 12 5 17.59 6.41 19 12 13.41 17.59 19 19 17.59 13.41 12z"/>
            </svg>
          </button>
        </div>
        <form @submit.prevent="saveCategory">
          <div class="form-group">
            <label for="editCategoryName">分类名称</label>
            <input
              id="editCategoryName"
              v-model="editingCategory.name"
              type="text"
              required
              class="form-control"
              placeholder="输入分类名称"
            />
          </div>
          <div class="form-group">
            <label for="editCategoryDescription">分类描述</label>
            <textarea
              id="editCategoryDescription"
              v-model="editingCategory.description"
              class="form-control"
              placeholder="输入分类描述（可选）"
              rows="3"
            ></textarea>
          </div>
          <div class="modal-actions">
            <button type="button" @click="closeEditModal" class="btn-cancel">取消</button>
            <button type="submit" :disabled="updatingCategory" class="btn-submit">
              {{ updatingCategory ? '保存中...' : '保存' }}
            </button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useCategories } from '@/composables/useCategories'
import type { Category } from '@/types/article'

const {
  categories,
  loading: loadingCategories,
  fetchCategories,
  createCategory,
  updateCategory,
  deleteCategory: deleteCategoryApi
} = useCategories()

const newCategory = ref({ name: '', description: '' })
const addingCategory = ref(false)
const deletingCategory = ref(false)
const updatingCategory = ref(false)
const editingCategory = ref<Category | null>(null)

onMounted(() => {
  fetchCategories()
})

const addCategory = async () => {
  if (!newCategory.value.name.trim()) return
  addingCategory.value = true
  try {
    await createCategory(newCategory.value)
    newCategory.value = { name: '', description: '' }
    await fetchCategories()
  } catch (error) {
    console.error('添加分类失败:', error)
    alert('添加分类失败')
  } finally {
    addingCategory.value = false
  }
}

const deleteCategory = async (id: number) => {
  if (!confirm('确定要删除这个分类吗？此操作不可恢复。')) return
  deletingCategory.value = true
  try {
    await deleteCategoryApi(id)
    await fetchCategories()
  } catch (error) {
    console.error('删除分类失败:', error)
    alert('删除分类失败')
  } finally {
    deletingCategory.value = false
  }
}

const editCategory = (category: Category) => {
  editingCategory.value = { ...category }
}

const closeEditModal = () => {
  editingCategory.value = null
}

const saveCategory = async () => {
  if (!editingCategory.value) return
  updatingCategory.value = true
  try {
    await updateCategory(editingCategory.value)
    await fetchCategories()
    closeEditModal()
  } catch (error) {
    console.error('更新分类失败:', error)
    alert('更新分类失败')
  } finally {
    updatingCategory.value = false
  }
}
</script>

<style scoped>
.category-manager {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.header {
  margin-bottom: 30px;
}

.header h2 {
  margin: 0 0 10px 0;
  font-size: 28px;
  color: #2c3e50;
}

.subtitle {
  margin: 0;
  color: #7f8c8d;
  font-size: 16px;
}

.card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 30px;
}

.card h3 {
  margin-top: 0;
  margin-bottom: 20px;
  color: #2c3e50;
  font-size: 20px;
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
  padding: 12px 16px;
  border: 2px solid #e0e0e0;
  border-radius: 8px;
  font-size: 16px;
  transition: all 0.3s ease;
  background-color: #fafafa;
}

.form-control:focus {
  outline: none;
  border-color: #42b983;
  box-shadow: 0 0 0 3px rgba(66, 185, 131, 0.2);
  background-color: white;
}

.btn-submit {
  padding: 8px 16px;
  background: linear-gradient(135deg, #42b983, #359c6d);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(66, 185, 131, 0.3);
}

.btn-submit:hover:not(:disabled) {
  background: linear-gradient(135deg, #359c6d, #2d855c);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(66, 185, 131, 0.4);
}

.btn-submit:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

.list-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.list-header h3 {
  margin: 0;
  color: #2c3e50;
}

.btn-refresh {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 6px 12px;
  background: #f8f9fa;
  color: #2c3e50;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.btn-refresh:hover {
  background: #e9ecef;
  border-color: #adb5bd;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
  color: #7f8c8d;
}

.spinner {
  width: 32px;
  height: 32px;
  border: 4px solid rgba(66, 185, 131, 0.2);
  border-top: 4px solid #42b983;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 16px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.empty {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: #95a5a6;
  text-align: center;
}

.empty svg {
  margin-bottom: 20px;
  opacity: 0.5;
}

.empty p {
  margin: 0;
  font-size: 18px;
}

.categories-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.category-card {
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  padding: 20px;
  border: 1px solid #e0e0e0;
  border-radius: 10px;
  transition: all 0.3s ease;
  background: #fafafa;
}

.category-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.1);
  border-color: #42b983;
}

.category-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 12px;
}

.category-name {
  margin: 0;
  font-size: 18px;
  color: #2c3e50;
  font-weight: 600;
}

.category-count {
  background: #42b983;
  color: white;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 600;
}

.category-description {
  margin: 0 0 20px 0;
  color: #7f8c8d;
  font-size: 14px;
  line-height: 1.5;
}

.category-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
}

.btn-edit, .btn-delete {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 30px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.btn-edit {
  background: #f1f8ff;
  color: #0366d6;
}

.btn-edit:hover {
  background: #0366d6;
  color: white;
}

.btn-delete {
  background: #ffeef0;
  color: #e53935;
}

.btn-delete:hover:not(:disabled) {
  background: #e53935;
  color: white;
}

.btn-delete:disabled {
  opacity: 0.7;
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
  justify-content: center;
  align-items: center;
  z-index: 1000;
  padding: 20px;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 100%;
  max-width: 500px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  animation: modalFadeIn 0.3s ease;
}

@keyframes modalFadeIn {
  from {
    opacity: 0;
    transform: translateY(-20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e0e0e0;
}

.modal-header h3 {
  margin: 0;
  color: #2c3e50;
  font-size: 20px;
}

.modal-close {
  background: none;
  border: none;
  cursor: pointer;
  color: #95a5a6;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.2s ease;
}

.modal-close:hover {
  background: #f1f2f3;
  color: #2c3e50;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 30px;
}

.btn-cancel {
  padding: 8px 16px;
  background: #f8f9fa;
  color: #2c3e50;
  border: 1px solid #e0e0e0;
  border-radius: 8px;
  cursor: pointer;
  font-size: 14px;
  transition: all 0.3s ease;
}

.btn-cancel:hover {
  background: #e9ecef;
  border-color: #adb5bd;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .category-manager {
    padding: 15px;
  }
  
  .header h2 {
    font-size: 24px;
  }
  
  .card {
    padding: 20px;
  }
  
  .categories-grid {
    grid-template-columns: 1fr;
  }
  
  .list-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
  
  .modal-content {
    margin: 20px;
  }
}
</style>