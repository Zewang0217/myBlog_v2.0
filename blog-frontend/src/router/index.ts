// src/router/index.ts
import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  {
    path: '/',
    redirect: '/articles'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/articles',
    name: 'Articles',
    component: () => import('@/views/BlogArticles.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/article/:id',
    name: 'ArticleDetail',
    component: () => import('@/views/BlogArticleDetail.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/article/create',
    name: 'ArticleCreate',
    component: () => import('@/views/BlogArticleCreate.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/article/edit/:id',
    name: 'ArticleEdit',
    component: () => import('@/views/BlogArticleCreate.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/drafts',
    name: 'Drafts',
    component: () => import('@/views/BlogArticleDrafts.vue'),
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 添加路由守卫
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore()

  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next('/login')
  } else {
    next()
  }
})

export default router
