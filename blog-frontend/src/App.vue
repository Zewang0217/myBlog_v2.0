<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { computed, ref, onMounted } from 'vue'
import SettingsPanel from '@/components/blog/SettingsPanel.vue'
import { useTheme } from '@/composables/useTheme'
import { useArticles } from '@/composables/useArticles'

const authStore = useAuthStore() // 获取用户认证状态
const isAuthenticated = computed(() => authStore.isAuthenticated) // 获取用户是否已登录
const isAdmin = computed(() => authStore.user?.role === 'ADMIN' || authStore.user?.role === 'ROLE_ADMIN') // 管理员身份检查

// 初始化主题
const { themeSettings, toggleTheme } = useTheme()

// 设置面板引用
const settingsPanel = ref<InstanceType<typeof SettingsPanel> | null>(null)

// 搜索相关
const { searchArticles: searchArticlesFunc, searchResults } = useArticles()
const searchQuery = ref('')
const isSearchFocused = ref(false)
const showSearchResults = ref(false)

const logout = () => {
  authStore.logout()
}

// 打开设置面板
const openSettings = () => {
  if (settingsPanel.value) {
    settingsPanel.value.openPanel()
  }
}

// 搜索处理
const handleSearch = async () => {
  if (searchQuery.value.trim()) {
    const result = await searchArticlesFunc(searchQuery.value.trim())
    if (result.success) {
      // 搜索结果已经在useArticles中处理
      showSearchResults.value = true
    }
  }
}

// 隐藏搜索结果
const hideSearchResults = () => {
  setTimeout(() => {
    showSearchResults.value = false
  }, 200)
}

// 移动端菜单状态
const mobileMenuOpen = ref(false)

onMounted(() => {
  // 点击文档其他部分隐藏搜索结果
  document.addEventListener('click', (e) => {
    const searchInput = document.querySelector('.search-input')
    if (showSearchResults.value && searchInput && !searchInput.contains(e.target as Node)) {
      hideSearchResults()
    }
  })
})
</script>

<template>
  <div class="app-layout">
    <!-- 顶部导航栏 -->
    <header class="app-header">
      <div class="container">
        <div class="header-content">
          <!-- 移动端菜单按钮 -->
          <button class="mobile-menu-btn" @click="mobileMenuOpen = !mobileMenuOpen">
            <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <line x1="3" y1="12" x2="21" y2="12"></line>
              <line x1="3" y1="6" x2="21" y2="6"></line>
              <line x1="3" y1="18" x2="21" y2="18"></line>
            </svg>
          </button>
          
          <h1 class="app-title">
            <RouterLink to="/" class="title-link">我的博客</RouterLink>
          </h1>
          
          <!-- 搜索栏 -->
          <div class="search-container">
            <div class="search-wrapper">
              <input
                v-model="searchQuery"
                @input="handleSearch"
                @focus="isSearchFocused = true; showSearchResults = true"
                class="search-input"
                type="text"
                placeholder="搜索文章..."
              />
              <button class="search-btn" @click="handleSearch">
                <svg xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                  <circle cx="11" cy="11" r="8"></circle>
                  <line x1="21" y1="21" x2="16.65" y2="16.65"></line>
                </svg>
              </button>
            </div>
            
            <!-- 搜索结果下拉 -->
            <div v-if="showSearchResults && searchQuery" class="search-results">
              <div v-if="searchResults && searchResults.length > 0">
                <RouterLink 
                  v-for="article in searchResults" 
                  :key="article.id" 
                  :to="`/article/${article.id}`"
                  class="search-result-item"
                  @click="showSearchResults = false; searchQuery = ''"
                >
                  <div class="search-result-title">{{ article.title }}</div>
                  <div class="search-result-meta">
                    <span>{{ new Date(article.createTime).toLocaleDateString() }}</span>
                  </div>
                </RouterLink>
              </div>
              <div v-else class="search-no-results">
                没有找到相关文章
              </div>
            </div>
          </div>
          
          <div class="header-actions">
            <!-- 主题切换按钮 -->
            <button class="btn-theme" @click="toggleTheme" title="切换主题">
              <svg v-if="themeSettings.theme === 'light'" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="5"></circle>
                <line x1="12" y1="1" x2="12" y2="3"></line>
                <line x1="12" y1="21" x2="12" y2="23"></line>
                <line x1="4.22" y1="4.22" x2="5.64" y2="5.64"></line>
                <line x1="18.36" y1="18.36" x2="19.78" y2="19.78"></line>
                <line x1="1" y1="12" x2="3" y2="12"></line>
                <line x1="21" y1="12" x2="23" y2="12"></line>
                <line x1="4.22" y1="19.78" x2="5.64" y2="18.36"></line>
                <line x1="18.36" y1="5.64" x2="19.78" y2="4.22"></line>
              </svg>
              <svg v-else xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M21 12.79A9 9 0 1 1 11.21 3 7 7 0 0 0 21 12.79z"></path>
              </svg>
            </button>
            
            <!-- 设置按钮 -->
            <button class="btn-settings" @click="openSettings" title="个性化设置">
              <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <circle cx="12" cy="12" r="3"></circle>
                <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1 0 2.83 2 2 0 0 1-2.83 0l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-2 2 2 2 0 0 1-2-2v-.09A1.65 1.65 0 0 0 9 19.4a1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 0 1-2.83 0 2 2 0 0 1 0-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1-2-2 2 2 0 0 1 2-2h.09A1.65 1.65 0 0 0 4.6 9a1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 0 1 0-2.83 2 2 0 0 1 2.83 0l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 2-2 2 2 0 0 1 2 2v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 0 1 2.83 0 2 2 0 0 1 0 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9a1.65 1.65 0 0 0 1.51 1H21a2 2 0 0 1 2 2 2 2 0 0 1-2 2h-.09a1.65 1.65 0 0 0-1.51 1z"></path>
              </svg>
            </button>
            
            <template v-if="isAuthenticated">
              <span class="user-info"> 欢迎， {{ authStore.user?.username }}</span>
              <button @click="logout" class="btn-logout">退出登录</button>
            </template>
            <template v-else>
              <RouterLink to="/login" class="btn-login">登录</RouterLink>
            </template>
          </div>
        </div>
        
        <!-- 移动端导航菜单 -->
        <div v-if="mobileMenuOpen" class="mobile-nav-menu">
          <ul class="mobile-nav-list">
            <li class="nav-item">
              <RouterLink to="/" class="nav-link" active-class="active" @click="mobileMenuOpen = false">首页</RouterLink>
            </li>
            <li class="nav-item">
              <RouterLink to="/articles" class="nav-link" active-class="active" @click="mobileMenuOpen = false">文章</RouterLink>
            </li>
            <li v-if="isAuthenticated" class="nav-item">
              <RouterLink to="/article/create" class="nav-link" active-class="active" @click="mobileMenuOpen = false">撰写文章</RouterLink>
            </li>
            <li v-if="isAdmin" class="nav-item">
              <RouterLink to="/drafts" class="nav-link" active-class="active" @click="mobileMenuOpen = false">草稿箱</RouterLink>
            </li>
            <li class="nav-item">
              <RouterLink to="/categories" class="nav-link" active-class="active" @click="mobileMenuOpen = false">分类管理</RouterLink>
            </li>
            <li class="nav-item">
              <RouterLink to="/photos" class="nav-link" active-class="active" @click="mobileMenuOpen = false">摄影相册</RouterLink>
            </li>
          </ul>
        </div>
      </div>
    </header>

    <!-- 主要内容区域 -->
    <div class="app-main-container">
      <!-- 侧边导航栏 -->
      <nav class="side-nav">
        <ul class="nav-list">
          <li class="nav-item">
            <RouterLink to="/" class="nav-link" active-class="active">首页</RouterLink>
          </li>
          <li class="nav-item">
            <RouterLink to="/articles" class="nav-link" active-class="active">文章</RouterLink>
          </li>
          <li v-if="isAuthenticated" class="nav-item">
            <RouterLink to="/article/create" class="nav-link" active-class="active">撰写文章</RouterLink>
          </li>
          <li v-if="isAdmin" class="nav-item">
            <RouterLink to="/drafts" class="nav-link" active-class="active">草稿箱</RouterLink>
          </li>
          <li class="nav-item">
            <RouterLink to="/categories" class="nav-link" active-class="active">分类管理</RouterLink>
          </li>
          <li class="nav-item">
            <RouterLink to="/photos" class="nav-link" active-class="active">摄影相册</RouterLink>
          </li>
        </ul>
      </nav>

      <!-- 主内容区 -->
      <main class="app-main">
        <RouterView />
      </main>
    </div>

    <!-- 底部信息 -->
    <footer class="app-footer">
      <div class="container">
        <p class="footer-text">© 2025 我的博客. 保留所有权利.</p>
      </div>
    </footer>
    
    <!-- 设置面板 -->
    <SettingsPanel ref="settingsPanel" />
  </div>
</template>

<style scoped>
/* 全局样式重置 */
.app-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  font-family: var(--font-family-main);
  line-height: 1.6;
}

/* CSS变量定义 - 支持深色主题 */
:root {
  /* 基础颜色 - 浅色主题 */
  --primary-color: #667eea;
  --primary-color-dark: #5a67d8;
  --secondary-color: #764ba2;
  --accent-color: #f093fb;
  
  /* 背景色 */
  --background-primary: #ffffff;
  --background-secondary: #f8fafc;
  --background-glass: rgba(255, 255, 255, 0.85);
  --background-card: rgba(255, 255, 255, 0.95);
  
  /* 文字颜色 */
  --text-color-primary: #1a202c;
  --text-color-secondary: #4a5568;
  --text-color-regular: #2d3748;
  --text-color-light: #718096;
  
  /* 边框颜色 */
  --border-color-base: #e2e8f0;
  --border-color-light: #f1f5f9;
  --border-color-lighter: rgba(226, 232, 240, 0.5);
  
  /* 阴影效果 */
  --box-shadow-base: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
  --box-shadow-hover: 0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05);
  --box-shadow-glass: 0 8px 32px rgba(31, 38, 135, 0.15);
  --box-shadow-elevated: 0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04);
  
  /* 其他变量 */
  --border-radius-base: 8px;
  --border-radius-large: 16px;
  --spacing-small: 8px;
  --spacing-medium: 16px;
  --spacing-large: 24px;
  --spacing-extra-large: 32px;
  --font-family-main: 'Inter', -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Roboto', sans-serif;
  --font-size-base: 15px;
  --font-size-medium: 14px;
  --font-size-small: 12px;
  --font-size-large: 18px;
  --font-size-extra-large: 24px;
  --transition-normal: 0.3s;
}

/* 深色主题样式 */
[data-theme="dark"] {
  /* 基础颜色 - 深色主题 */
  --primary-color: #818cf8;
  --primary-color-dark: #6366f1;
  --secondary-color: #a78bfa;
  --accent-color: #f472b6;
  
  /* 背景色 */
  --background-primary: #0f172a;
  --background-secondary: #1e293b;
  --background-glass: rgba(15, 23, 42, 0.85);
  --background-card: rgba(30, 41, 59, 0.95);
  
  /* 文字颜色 */
  --text-color-primary: #f8fafc;
  --text-color-secondary: #cbd5e1;
  --text-color-regular: #e2e8f0;
  --text-color-light: #94a3b8;
  
  /* 边框颜色 */
  --border-color-base: #334155;
  --border-color-light: #475569;
  --border-color-lighter: rgba(71, 85, 105, 0.5);
  
  /* 阴影效果 - 深色主题更柔和 */
  --box-shadow-base: 0 4px 6px -1px rgba(0, 0, 0, 0.3), 0 2px 4px -1px rgba(0, 0, 0, 0.2);
  --box-shadow-hover: 0 10px 15px -3px rgba(0, 0, 0, 0.4), 0 4px 6px -2px rgba(0, 0, 0, 0.3);
  --box-shadow-glass: 0 8px 32px rgba(2, 8, 20, 0.4);
  --box-shadow-elevated: 0 20px 25px -5px rgba(0, 0, 0, 0.5), 0 10px 10px -5px rgba(0, 0, 0, 0.4);
}

/* 深色主题特定样式 */
[data-theme="dark"] .app-header {
  background-color: var(--background-glass);
  border-bottom: 1px solid var(--border-color-base);
}

[data-theme="dark"] .search-wrapper {
  background-color: var(--background-glass);
  border: 2px solid var(--border-color-base);
}

[data-theme="dark"] .search-input {
  color: var(--text-color-primary);
}

[data-theme="dark"] .search-input::placeholder {
  color: var(--text-color-light);
}

[data-theme="dark"] .search-results {
  background-color: var(--background-glass);
  border: 1px solid var(--border-color-base);
}

[data-theme="dark"] .search-result-item {
  color: var(--text-color-primary);
  border-bottom: 1px solid var(--border-color-light);
}

[data-theme="dark"] .search-result-item:hover {
  background-color: rgba(129, 140, 248, 0.1);
  border-left-color: var(--primary-color);
}

[data-theme="dark"] .search-result-meta {
  color: var(--text-color-light);
}

[data-theme="dark"] .search-no-results {
  color: var(--text-color-light);
}

[data-theme="dark"] .btn-theme,
[data-theme="dark"] .btn-settings {
  background-color: var(--background-glass);
  border: 2px solid var(--border-color-base);
  color: var(--text-color-primary);
}

[data-theme="dark"] .btn-theme:hover,
[data-theme="dark"] .btn-settings:hover {
  background-color: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
}

[data-theme="dark"] .btn-logout,
[data-theme="dark"] .btn-login {
  background-color: var(--background-glass);
  border: 2px solid var(--border-color-base);
  color: var(--text-color-primary);
}

[data-theme="dark"] .btn-logout:hover,
[data-theme="dark"] .btn-login:hover {
  background: linear-gradient(135deg, var(--primary-color), var(--primary-color-dark));
  color: white;
}

[data-theme="dark"] .side-nav {
  background-color: var(--background-glass);
  border-right: 1px solid var(--border-color-base);
}

[data-theme="dark"] .nav-link {
  color: var(--text-color-regular);
}

[data-theme="dark"] .nav-link:hover,
[data-theme="dark"] .nav-link.active {
  color: var(--primary-color);
  background-color: rgba(129, 140, 248, 0.1);
  border-left-color: var(--primary-color);
}

[data-theme="dark"] .app-footer {
  background-color: var(--background-glass);
  border-top: 1px solid var(--border-color-base);
}

[data-theme="dark"] .footer-text {
  color: var(--text-color-light);
}

/* 大气风格的头部导航 */
.app-header {
  background-color: var(--background-glass);
  backdrop-filter: blur(16px);
  box-shadow: var(--box-shadow-glass);
  position: sticky;
  top: 0;
  z-index: 1000;
  border-bottom: 1px solid var(--border-color-light);
  transition: all var(--transition-normal) ease;
}

/* 头部悬停效果 - 轻微上浮 */
.app-header:hover {
  box-shadow: var(--box-shadow-hover);
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 var(--spacing-large);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 72px;
  padding: 0 16px;
  gap: var(--spacing-large);
}

/* 大气的标题样式 */
.app-title {
  margin: 0;
  font-size: var(--font-size-extra-large);
}

.title-link {
  color: var(--primary-color);
  text-decoration: none;
  font-weight: 800;
  transition: all var(--transition-normal) ease;
  letter-spacing: -0.5px;
  background: linear-gradient(135deg, var(--primary-color), var(--secondary-color));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-fill-color: transparent;
  display: inline-block;
  padding: 4px 0;
}

.title-link:hover {
  transform: scale(1.02);
  filter: brightness(1.1);
}

/* 移动端菜单按钮 */
.mobile-menu-btn {
  display: none;
  background: none;
  border: none;
  cursor: pointer;
  padding: 8px;
  border-radius: var(--border-radius-base);
  color: var(--text-color-primary);
  transition: all var(--transition-normal) ease;
  border: 2px solid transparent;
  min-width: 40px;
  min-height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.mobile-menu-btn:hover {
  background-color: rgba(67, 56, 202, 0.1);
  color: var(--primary-color);
  border-color: rgba(67, 56, 202, 0.2);
  transform: scale(1.05);
}

/* 搜索容器 - 大气风格 */
.search-container {
  position: relative;
  flex: 1;
  max-width: 500px;
  margin: 0 20px;
}

.search-wrapper {
  display: flex;
  align-items: center;
  border: 2px solid var(--border-color-base);
  border-radius: var(--border-radius-large);
  overflow: hidden;
  background-color: var(--background-glass);
  backdrop-filter: blur(8px);
  box-shadow: var(--box-shadow-base);
  transition: all var(--transition-normal) ease;
  min-height: 44px;
}

.search-wrapper:focus-within {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(67, 56, 202, 0.15);
  transform: translateY(-2px);
}

.search-input {
  flex: 1;
  padding: 12px 16px;
  border: none;
  outline: none;
  background: transparent;
  font-size: var(--font-size-base);
  color: var(--text-color-primary);
  font-family: inherit;
}

.search-btn {
  padding: 10px 16px;
  background: none;
  border: none;
  cursor: pointer;
  color: var(--text-color-secondary);
  transition: all var(--transition-normal) ease;
  display: flex;
  align-items: center;
  justify-content: center;
  border-left: 1px solid var(--border-color-light);
}

.search-btn:hover {
  color: var(--primary-color);
  background-color: rgba(67, 56, 202, 0.1);
}

/* 搜索结果下拉 - 精致设计 */
.search-results {
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background-color: var(--background-glass);
  backdrop-filter: blur(12px);
  border: 1px solid var(--border-color-base);
  border-radius: var(--border-radius-base);
  margin-top: 8px;
  box-shadow: var(--box-shadow-elevated);
  padding: 8px 0;
  z-index: 1000;
  max-height: 350px;
  overflow-y: auto;
  animation: slideDown 0.3s ease-out;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.search-result-item {
  display: block;
  padding: 12px 16px;
  text-decoration: none;
  color: var(--text-color-primary);
  border-bottom: 1px solid var(--border-color-light);
  transition: all var(--transition-normal) ease;
  border-left: 3px solid transparent;
}

.search-result-item:last-child {
  border-bottom: none;
}

.search-result-item:hover {
  background-color: rgba(67, 56, 202, 0.08);
  transform: translateX(4px);
  border-left-color: var(--primary-color);
}

.search-result-title {
  font-weight: 600;
  margin-bottom: 6px;
  font-size: var(--font-size-medium);
}

.search-result-meta {
  font-size: var(--font-size-small);
  color: var(--text-color-secondary);
}

.search-no-results {
  padding: 32px 20px;
  text-align: center;
  color: var(--text-color-secondary);
  font-size: var(--font-size-base);
}

/* 按钮样式 - 现代化设计 */
.btn-theme,
.btn-settings {
  background: none;
  border: 2px solid var(--border-color-base);
  cursor: pointer;
  padding: 8px 12px;
  border-radius: var(--border-radius-base);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--text-color-primary);
  transition: all var(--transition-normal) ease;
  margin: 0 6px;
  background-color: var(--background-glass);
  backdrop-filter: blur(8px);
  min-width: 40px;
  min-height: 40px;
}

.btn-theme:hover,
.btn-settings:hover {
  background-color: var(--primary-color);
  color: white;
  border-color: var(--primary-color);
  transform: translateY(-2px) scale(1.05);
  box-shadow: var(--box-shadow-hover);
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-info {
  font-size: var(--font-size-base);
  color: var(--text-color-regular);
  font-weight: 600;
  padding: 0 8px;
}

.btn-logout,
.btn-login {
  padding: 10px 20px;
  border-radius: var(--border-radius-base);
  border: 2px solid var(--border-color-base);
  background-color: var(--background-glass);
  backdrop-filter: blur(8px);
  color: var(--text-color-primary);
  cursor: pointer;
  font-size: var(--font-size-base);
  transition: all var(--transition-normal) ease;
  font-weight: 600;
  min-height: 40px;
}

.btn-logout:hover,
.btn-login:hover {
  background: linear-gradient(135deg, var(--primary-color), var(--primary-color-dark));
  color: white;
  border-color: var(--primary-color);
  transform: translateY(-2px) scale(1.03);
  box-shadow: var(--box-shadow-hover);
}

/* 主容器布局 */
.app-main-container {
  display: flex;
  flex: 1;
  min-height: calc(100vh - 72px - 64px);
}

/* 侧边导航栏 - 现代化设计 */
.side-nav {
  width: 260px;
  background-color: var(--background-glass);
  backdrop-filter: blur(12px);
  padding: 24px 0;
  border-right: 1px solid var(--border-color-light);
  min-height: calc(100vh - 72px - 64px);
  transition: all var(--transition-normal) ease;
  box-shadow: inset -1px 0 0 var(--border-color-lighter);
}

.nav-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.nav-item {
  margin-bottom: 8px;
}

/* 导航链接 - 大气风格 */
.nav-link {
  display: block;
  padding: 14px 24px;
  color: var(--text-color-regular);
  text-decoration: none;
  font-weight: 600;
  transition: all var(--transition-normal) ease;
  border-left: 4px solid transparent;
  display: flex;
  align-items: center;
  position: relative;
  overflow: hidden;
  z-index: 1;
  font-size: var(--font-size-base);
  letter-spacing: 0.5px;
}

/* 导航链接悬停效果 */
.nav-link::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(67, 56, 202, 0.1), transparent);
  transition: left 0.6s ease;
  z-index: -1;
}

.nav-link:hover::before {
  left: 100%;
}

.nav-link:hover,
.nav-link.active {
  color: var(--primary-color);
  background-color: rgba(67, 56, 202, 0.05);
  border-left-color: var(--primary-color);
  transform: translateX(4px);
}

.nav-link.active {
  font-weight: 700;
  background: linear-gradient(135deg, rgba(67, 56, 202, 0.05), transparent);
  box-shadow: 0 4px 8px -2px rgba(0, 0, 0, 0.03);
}

/* 主内容区 - 宽敞布局 */
.app-main {
  flex: 1;
  padding: var(--spacing-extra-large);
  transition: all var(--transition-normal) ease;
}

/* 大气风格的页脚 */
.app-footer {
  background-color: var(--background-glass);
  backdrop-filter: blur(12px);
  padding: var(--spacing-large) 0;
  border-top: 1px solid var(--border-color-lighter);
  box-shadow: 0 -4px 12px -2px rgba(0, 0, 0, 0.03);
  transition: all var(--transition-normal) ease;
}

.footer-text {
  margin: 0;
  text-align: center;
  color: var(--text-color-secondary);
  font-size: var(--font-size-base);
  font-weight: 600;
  letter-spacing: 0.5px;
}

/* 移动端导航菜单 - 精致设计 */
.mobile-nav-menu {
  display: none;
  background-color: var(--background-glass);
  backdrop-filter: blur(12px);
  border-bottom: 1px solid var(--border-color-light);
  padding: 16px 0;
  box-shadow: 0 10px 20px -8px rgba(0, 0, 0, 0.1);
  animation: slideDown 0.3s ease-out;
}

.mobile-nav-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.mobile-nav-list .nav-item {
  margin-bottom: 0;
}

.mobile-nav-list .nav-link {
  padding: 16px 24px;
  border-left: none !important;
  border-bottom: 1px solid var(--border-color-light);
  transition: all var(--transition-normal) ease;
  font-size: var(--font-size-medium);
}

.mobile-nav-list .nav-link:hover,
.mobile-nav-list .nav-link.active {
  background-color: rgba(67, 56, 202, 0.08);
  color: var(--primary-color);
  transform: translateX(8px);
}

.mobile-nav-list .nav-link:last-child {
  border-bottom: none;
}

/* 响应式设计 - 大气风格 */
@media (max-width: 1200px) {
  .container {
    max-width: 1200px;
  }
  
  .search-container {
    max-width: 400px;
  }
  
  .side-nav {
    width: 240px;
  }
  
  .nav-link {
    padding: 12px 20px;
  }
}

@media (max-width: 768px) {
  .mobile-menu-btn {
    display: flex;
    margin-right: 10px;
  }
  
  .search-container {
    display: none;
  }
  
  .app-title {
    font-size: var(--font-size-large);
  }

  .header-actions {
    gap: 8px;
  }

  .header-actions .btn-theme,
  .header-actions .btn-settings {
    margin: 0;
    padding: 6px;
    min-width: 36px;
    min-height: 36px;
  }

  .header-actions .user-info {
    display: none;
  }

  .app-main-container {
    flex-direction: column;
  }

  .side-nav {
    display: none;
  }

  .app-main {
    padding: var(--spacing-large);
    min-height: calc(100vh - 72px - 64px);
  }
  
  .mobile-nav-menu {
    display: block;
  }

  .container {
    padding: 0 var(--spacing-medium);
  }
  
  .header-content {
    height: 64px;
    padding: 0 12px;
    gap: var(--spacing-medium);
  }
  
  .app-title {
    margin: 0 6px;
    flex-shrink: 0;
  }
  
  .btn-logout,
  .btn-login {
    padding: 8px 16px;
    font-size: var(--font-size-small);
    min-height: 36px;
  }
}

@media (max-width: 480px) {
  .header-actions {
    gap: 4px;
  }
  
  .btn-logout,
  .btn-login {
    padding: 6px 12px;
    font-size: var(--font-size-small);
  }
  
  .app-main {
    padding: var(--spacing-medium);
  }
  
  .container {
    padding: 0 var(--spacing-small);
  }
  
  .header-content {
    height: 60px;
    padding: 0 8px;
  }
  
  .app-title {
    font-size: var(--font-size-base);
  }
}

/* 通用卡片组件样式 - 大气美观 */
.card {
  background-color: var(--background-glass);
  backdrop-filter: blur(12px);
  border-radius: var(--border-radius-large);
  border: 1px solid var(--border-color-light);
  padding: var(--spacing-large);
  margin-bottom: var(--spacing-large);
  box-shadow: var(--box-shadow-base);
  transition: all var(--transition-normal) ease;
}

.card:hover {
  transform: translateY(-6px);
  box-shadow: var(--box-shadow-elevated);
  border-color: rgba(67, 56, 202, 0.3);
}

.card-header {
  margin-bottom: var(--spacing-medium);
  padding-bottom: var(--spacing-medium);
  border-bottom: 1px solid var(--border-color-light);
}

.card-title {
  font-size: var(--font-size-large);
  font-weight: 700;
  color: var(--text-color-primary);
  margin-bottom: var(--spacing-small);
}

.card-subtitle {
  font-size: var(--font-size-base);
  color: var(--text-color-secondary);
}

/* 按钮组样式 */
.button-group {
  display: flex;
  gap: var(--spacing-small);
  flex-wrap: wrap;
}

/* 加载动画 */
@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

.loading {
  animation: pulse 1.5s cubic-bezier(0.4, 0, 0.6, 1) infinite;
}

/* 标签样式 */
.tag {
  display: inline-flex;
  align-items: center;
  padding: var(--spacing-extra-small) var(--spacing-small);
  border-radius: 9999px;
  font-size: var(--font-size-small);
  font-weight: 600;
  background-color: rgba(67, 56, 202, 0.1);
  color: var(--primary-color);
  transition: all var(--transition-normal) ease;
  margin-right: var(--spacing-small);
  margin-bottom: var(--spacing-small);
  border: 1px solid rgba(67, 56, 202, 0.2);
}

.tag:hover {
  background-color: rgba(67, 56, 202, 0.2);
  transform: translateY(-2px);
  box-shadow: var(--box-shadow-base);
}

/* 分隔线样式 */
.divider {
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--border-color-light), transparent);
  margin: var(--spacing-large) 0;
}
</style>
