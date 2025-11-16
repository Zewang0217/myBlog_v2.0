// src/composables/useCategories.ts
import {ref} from 'vue';
import type {Ref} from 'vue';
import {
  getCategories,
  createCategory as createCategoryApi,
  deleteCategory as deleteCategoryApi,
  getArticlesByCategories
} from '@/api/articleService'; // 修改导入路径
import type {Category} from '@/types/article'; // 添加 type 关键字
interface UseCategoriesReturn {
  categories: Ref<Category[]>;
  loading: Ref<boolean>;
  error: Ref<string | null>;
  fetchCategories: () => Promise<void>;
  createCategory: (category: Omit<Category, 'id' | 'createTime' | 'updateTime'>) => Promise<void>;
  deleteCategory: (id: string) => Promise<void>;
  updateCategory: (category: Category) => Promise<void>;
  getArticlesByCategories: (categoryIds: string[]) => Promise<any>;
}

export const useCategories = (): UseCategoriesReturn => {
  const categories = ref<Category[]>([]);
  const loading = ref(false);
  const error = ref<string | null>(null);
  const fetchCategories = async () => {
    loading.value = true;
    error.value = null;
    try {
      const response = await getCategories();
      if (response.code === 200) {
        categories.value = response.data;
      } else {
        error.value = response.message;
      }
    } catch (err: any) {
      error.value = err.message || '获取分类列表失败';
      console.error('Failed to fetch categories:', err);
    } finally {
      loading.value = false;
    }
  };
  const createCategory = async (category: Omit<Category, 'id' | 'createTime' | 'updateTime'>) => {
    loading.value = true;
    error.value = null;
    try {
      const response = await createCategoryApi(category);
      if (response.code !== 200) {
        throw new Error(response.message);
      }
    } catch (err: any) {
      error.value = err.message || '创建分类失败';
      throw err;
    } finally {
      loading.value = false;
    }
  };
  const deleteCategory = async (id: string) => {
    loading.value = true;
    error.value = null;
    try {
      const response = await deleteCategoryApi(id);
      if (response.code !== 200) {
        throw new Error(response.message);
      }
    } catch (err: any) {
      error.value = err.message || '删除分类失败';
      throw err;
    } finally {
      loading.value = false;
    }
  };
  const updateCategory = async (category: Category) => {
    loading.value = true;
    error.value = null;
    try {
      // 假设有一个更新分类的API
      const response = await createCategoryApi({
        name: category.name,
        description: category.description
      });
      if (response.code !== 200) {
        throw new Error(response.message);
      }
    } catch (err: any) {
      error.value = err.message || '更新分类失败';
      throw err;
    } finally {
      loading.value = false;
    }
  };
  
  // 根据分类获取文章
  const getArticlesByCategoriesApi = async (categoryIds: string[]) => {
    try {
      const response = await getArticlesByCategories(categoryIds);
      return response;
    } catch (err) {
      console.error('Failed to fetch articles by categories:', err);
      throw err;
    }
  };

  return {
    categories, 
    loading, 
    error, 
    fetchCategories, 
    createCategory, 
    deleteCategory,
    updateCategory,
    getArticlesByCategories: getArticlesByCategoriesApi
  };
};