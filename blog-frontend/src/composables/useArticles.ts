// blog-frontend/src/composables/useArticles.ts
// 文章相关的组合式 API，用于在 Vue 组件中管理文章数据
import { ref } from 'vue'
import type { Ref } from 'vue'
import type { Article, CreateArticleDTO } from '@/types/article'
import {
  getArticles,
  getArticleById,
  createArticle,
  updateArticle,
  deleteArticle
} from '@/api/articleService'

// 定义文章列表的组合式函数
export const useArticles = () => {
  // 使用 ref 创建响应式数据
  const articles: Ref<Article[]> = ref([])
  const loading = ref(false)
  const error = ref<string | null>(null)

  // 获取所有文章
  const fetchArticles = async () => {
    loading.value = true
    error.value = null
    try {
      const response = await getArticles()
      if (response.code === 200) {
        articles.value = response.data
      } else {
        error.value = response.message
      }
    } catch (err) {
      error.value = '获取文章列表失败'
      console.error('Failed to fetch articles:', err)
    } finally {
      loading.value = false
    }
  }

  // 返回响应式数据和方法
  return {
    articles,
    loading,
    error,
    fetchArticles
  }
}

// 定义单个文章的组合式函数
export const useArticle = () => {
  const article: Ref<Article | null> = ref(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  // 根据 ID 获取文章详情
  const fetchArticle = async (id: number) => {
    loading.value = true
    error.value = null
    try {
      const response = await getArticleById(id)
      if (response.code === 200) {
        article.value = response.data
      } else {
        error.value = response.message
      }
    } catch (err) {
      error.value = '获取文章详情失败'
      console.error('Failed to fetch article:', err)
    } finally {
      loading.value = false
    }
  }

  // 创建文章
  const create = async (articleData: CreateArticleDTO) => {
    loading.value = true
    error.value = null
    try {
      const response = await createArticle(articleData)
      if (response.code === 200) {
        article.value = response.data
        return { success: true, data: response.data }
      } else {
        error.value = response.message
        return { success: false, error: response.message }
      }
    } catch (err) {
      error.value = '创建文章失败'
      console.error('Failed to create article:', err)
      return { success: false, error: '创建文章失败' }
    } finally {
      loading.value = false
    }
  }

  // 返回响应式数据和方法
  return {
    article,
    loading,
    error,
    fetchArticle,
    create
  }
}
