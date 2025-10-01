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
  deleteArticle,
  getArticlesByCategories
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

  // 根据分类获取文章
  const getArticlesByCategoriesApi = async (categoryIds: string[]) => {
    try {
      const response = await getArticlesByCategories(categoryIds)
      return response
    } catch (err) {
      console.error('Failed to fetch articles by categories:', err)
      throw err
    }
  }

  // 返回响应式数据和方法
  return {
    articles,
    loading,
    error,
    fetchArticles,
    fetchPublishedArticles,
    fetchDraftArticles,
    getArticlesByCategories: getArticlesByCategoriesApi
  }
}

// 定义单个文章的组合式函数
export const useArticle = () => {
  const article: Ref<Article | null> = ref(null)
  const loading = ref(false)
  const error = ref<string | null>(null)

  // 根据 ID 获取文章详情
  const fetchArticle = async (id: string | number) => {
    const articleId = typeof id === 'number' ? id.toString() : id;

    // 验证ID有效性
    if (!articleId || articleId.trim() === '') {
      error.value = '无效的文章ID'
      return { success: false, error: '无效的文章ID' }
    }

    loading.value = true
    error.value = null
    try {
      // console.log('准备调用 getArticleById');
      const response = await getArticleById(articleId)
      // console.log('getArticleById 返回结果:', response);
      if (response.code === 200) {
        article.value = response.data
        return { success: true, data: response.data }
      } else {
        error.value = response.message
        return { success: false, error: response.message }
      }
    } catch (err: any) {
      console.error('fetchArticle 发生错误:', err);
      // 更详细的错误处理
      if (err.response?.status === 404) {
        error.value = '文章不存在或已被删除'
      } else if (err.response?.status === 500) {
        error.value = '服务器内部错误，请稍后重试'
      } else {
        error.value = '获取文章详情失败'
      }
      console.error('Failed to fetch article:', err)
      return { success: false, error: error.value }
    } finally {
      loading.value = false
      console.log('fetchArticle 执行完成');
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
  const update = async (id: string, articleData: CreateArticleDTO) => {
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
  const publish = async (id: string) => {
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
  const deleteArticleById = async (id: string) => {
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