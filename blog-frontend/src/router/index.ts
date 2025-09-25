import { createRouter, createWebHistory } from 'vue-router'

// 导入博客页面组件
const BlogArticles = () => import('../views/BlogArticles.vue')
const BlogArticleDetail = () => import('../views/BlogArticleDetail.vue')
const BlogArticleCreate = () => import('../views/BlogArticleCreate.vue')
const BlogArticleDrafts = () => import('../views/BlogArticleDrafts.vue')

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      redirect: '/articles' // 默认重定向到文章列表
    },
    // 博客文章列表页面
    {
      path: '/articles',
      name: 'articles',
      component: BlogArticles
    },
    // 草稿箱页面
    {
      path: '/articles/drafts',
      name: 'drafts',
      component: BlogArticleDrafts
    },
    // 博客文章详情页面
    {
      path: '/article/:id',
      name: 'article-detail',
      component: BlogArticleDetail,
      props: true
    },
    // 创建文章页面
    {
      path: '/article/create',
      name: 'article-create',
      component: BlogArticleCreate
    },
    // 编辑文章页面
    {
      path: '/article/edit/:id',
      name: 'article-edit',
      component: BlogArticleCreate,
      props: true
    }
  ]
})

export default router
