// src/router/index.ts
import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const routes = [
  {
    path: '/',
    name: 'Welcome',
    component: () => import('@/views/Welcome.vue')
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
    meta: { requiresAuth: false }
  },
  {
    path: '/article/:id',
    name: 'ArticleDetail',
    component: () => import('@/views/BlogArticleDetail.vue'),
    meta: { requiresAuth: false }
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
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/categories',
    name: 'Categories',
    component: () => import('@/views/CategoryManager.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/photos',
    name: 'PhotoGallery',
    component: () => import('@/views/PhotoGallery.vue'),
    meta: { requiresAuth: false }
  },
  {
    path: '/admin/dashboard',
    name: 'AdminDashboard',
    component: () => import('@/views/AdminDashboard.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/users',
    name: 'UserManager',
    component: () => import('@/views/UserManager.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
  },
  {
    path: '/admin/comments',
    name: 'CommentManager',
    component: () => import('@/views/CommentManager.vue'),
    meta: { requiresAuth: true, requiresAdmin: true }
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
  } else if (to.meta.requiresAdmin && !authStore.isAdmin) {
    alert('您没有权限访问此页面。')
    next('/') // 重定向到首页
  } else {
    next()
  }
})

export default router
