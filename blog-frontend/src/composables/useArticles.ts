// blog-frontend/src/composables/useArticles.ts
// 文章相关的组合式 API，用于在 Vue 组件中管理文章数据
import { ref } from 'vue'
import type { Ref } from 'vue'
import type { Article, CreateArticleDTO } from '@/types/article'
import {
  getArticles,
  getPublishedArticles,
  getDraftArticles,
  getArticleById,
  createArticle,
  updateArticle,
  publishArticle,
  deleteArticle
} from '@/api/articleService'
import { ArticleStatus } from '@/types/article'

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

  // 获取已发布的文章
  const fetchPublishedArticles = async () => {
    loading.value = true
    error.value = null
    try {
      const response = await getPublishedArticles()
      if (response.code === 200) {
        articles.value = response.data
      } else {
        error.value = response.message
      }
    } catch (err: any) {
      // 更细致的错误处理
      if (err.response?.status === 401) {
        // 对于公开API，401可能是后端配置问题，尝试继续处理
        error.value = '获取文章列表失败，请稍后重试'
      } else {
        error.value = err.message || '获取已发布文章列表失败'
      }
      console.error('Failed to fetch published articles:', err)
    } finally {
      loading.value = false
    }
  }

  // 获取草稿文章
  const fetchDraftArticles = async () => {
    loading.value = true
    error.value = null
    try {
      const response = await getDraftArticles()
      if (response.code === 200) {
        articles.value = response.data
      } else {
        error.value = response.message
      }
    } catch (err) {
      error.value = '获取草稿列表失败'
      console.error('Failed to fetch draft articles:', err)
    } finally {
      loading.value = false
    }
  }

  // 返回响应式数据和方法
  return {
    articles,
    loading,
    error,
    fetchArticles,
    fetchPublishedArticles,
    fetchDraftArticles
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
        return { success: true, data: response.data } // 添加返回值
      } else {
        error.value = response.message
        return { success: false, error: response.message } // 添加返回值
      }
    } catch (err) {
      error.value = '获取文章详情失败'
      console.error('Failed to fetch article:', err)
      return { success: false, error: '获取文章详情失败' } // 添加返回值
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

  // 更新文章
  const update = async (id: number, articleData: CreateArticleDTO) => {
    loading.value = true
    error.value = null
    try {
      const response = await updateArticle(id, articleData)
      if (response.code === 200) {
        article.value = response.data
        return { success: true, data: response.data }
      } else {
        error.value = response.message
        return { success: false, error: response.message }
      }
    } catch (err) {
      error.value = '更新文章失败'
      console.error('Failed to update article:', err)
      return { success: false, error: '更新文章失败' }
    } finally {
      loading.value = false
    }
  }

  // 发布文章
  const publish = async (id: number) => {
    loading.value = true
    error.value = null
    try {
      const response = await publishArticle(id)
      if (response.code === 200) {
        article.value = response.data
        return { success: true, data: response.data }
      } else {
        error.value = response.message
        return { success: false, error: response.message }
      }
    } catch (err) {
      error.value = '发布文章失败'
      console.error('Failed to publish article:', err)
      return { success: false, error: '发布文章失败' }
    } finally {
      loading.value = false
    }
  }

  // 删除文章
  const deleteArticleById = async (id: number) => {
    loading.value = true
    error.value = null
    try {
      const response = await deleteArticle(id)
      if (response.code === 200) {
        return { success: true, data: response.data }
      } else {
        error.value = response.message
        return { success: false, error: response.message }
      }
    } catch (err) {
      error.value = '删除文章失败'
      console.error('Failed to delete article:', err)
      return { success: false, error: '删除文章失败' }
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
    create,
    update,
    publish,
    deleteArticleById
  }

}
