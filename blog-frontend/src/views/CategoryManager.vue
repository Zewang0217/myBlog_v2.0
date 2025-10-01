<!-- src/components/blog/CategoryManager.vue -->
<template>
  <div class="category-manager"><h3>分类管理</h3>
    <!-- 添加分类表单 -->
    <div class="add-category-form">
      <h4>添加新分类</h4>
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
    <div class="category-list">
      <h4>现有分类</h4>
      <div v-if="loadingCategories" class="loading">加载中...</div>
      <div v-else-if="categories.length === 0" class="empty">暂无分类</div>
      <div v-else class="categories">
        <div
          v-for="category in categories"
          :key="category.id"
          class="category-item"
        >
          <div class="category-info">
            <strong>{{ category.name }}</strong>
            <p v-if="category.description">{{ category.description }}</p>
          </div>
          <div class="category-actions">
            <button
              @click="deleteCategory(category.id)"
              :disabled="deletingCategory"
              class="btn-delete"
            >
              删除
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>
<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useCategories } from '@/composables/useCategories'
import { deleteCategory as deleteCategoryApi } from "@/api/articleService.ts";

const {
  categories,
  loading: loadingCategories,
  fetchCategories,
  createCategory,
  deleteCategory: deleteCategoryApiComposable // 重命名避免冲突
} = useCategories()

const newCategory = ref({ name: '', description: '' })
const addingCategory = ref(false)
const deletingCategory = ref(false)

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
  if (!confirm('确定要删除这个分类吗？')) return
  deletingCategory.value = true
  try {
    await deleteCategoryApiComposable(id)
    await fetchCategories()
  } catch (error) {
    console.error('删除分类失败:', error)
    alert('删除分类失败')
  } finally {
    deletingCategory.value = false
  }
}
</script>

<style scoped> .category-manager {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  margin-bottom: 20px;
}

.add-category-form {
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.add-category-form h4 {
  margin-top: 0;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
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

.btn-submit {
  padding: 10px 20px;
  background: #42b983;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.btn-submit:hover:not(:disabled) {
  background: #359c6d;
}

.btn-submit:disabled {
  opacity: 0.7;
  cursor: not-allowed;
}

.category-list h4 {
  margin-top: 0;
}

.loading, .empty {
  text-align: center;
  padding: 20px;
  color: #999;
}

.categories {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.category-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border: 1px solid #eee;
  border-radius: 4px;
}

.category-info strong {
  display: block;
  margin-bottom: 5px;
}

.category-info p {
  margin: 0;
  color: #666;
  font-size: 14px;
}

.btn-delete {
  padding: 6px 12px;
  background: #e53935;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
}

.btn-delete:hover:not(:disabled) {
  background: #c0392b;
}

.btn-delete:disabled {
  opacity: 0.7;
  cursor: not-allowed;
} </style>
