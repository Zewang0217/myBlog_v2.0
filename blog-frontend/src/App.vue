<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <div class="app-layout">
    <!-- 顶部导航栏 -->
    <header class="app-header">
      <div class="container">
        <div class="header-content">
          <h1 class="app-title">
            <RouterLink to="/articles" class="title-link">我的博客</RouterLink>
          </h1>
          <nav class="main-nav">
            <RouterLink to="/articles" class="nav-link">文章列表</RouterLink>
          </nav>
        </div>
      </div>
    </header>

    <!-- 主要内容区域 -->
    <main class="app-main">
      <RouterView />
    </main>

    <!-- 底部信息 -->
    <footer class="app-footer">
      <div class="container">
        <p class="footer-text">© 2025 我的博客. 保留所有权利.</p>
      </div>
    </footer>
  </div>
</template>

<style scoped>
.app-layout {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.app-header {
  background-color: var(--background-color-white);
  box-shadow: var(--box-shadow-base);
  position: sticky;
  top: 0;
  z-index: 100;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 var(--spacing-large);
}

.header-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  height: 60px;
}

.app-title {
  margin: 0;
  font-size: var(--font-size-large);
}

.title-link {
  color: var(--text-color-primary);
  text-decoration: none;
  font-weight: 600;
}

.title-link:hover {
  color: var(--primary-color);
}

.main-nav {
  display: flex;
  gap: var(--spacing-medium);
}

.nav-link {
  color: var(--text-color-regular);
  text-decoration: none;
  font-weight: 500;
  padding: var(--spacing-small) var(--spacing-base);
  border-radius: var(--border-radius-base);
  transition: all 0.3s;
}

.nav-link:hover,
.nav-link.router-link-exact-active {
  color: var(--primary-color);
  background-color: var(--background-color-base);
}

.app-main {
  flex: 1;
  padding: var(--spacing-large) 0;
}

.app-footer {
  background-color: var(--background-color-base);
  padding: var(--spacing-medium) 0;
  border-top: 1px solid var(--border-color-light);
}

.footer-text {
  margin: 0;
  text-align: center;
  color: var(--text-color-secondary);
  font-size: var(--font-size-small);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .container {
    padding: 0 var(--spacing-medium);
  }

  .header-content {
    height: 50px;
  }

  .app-title {
    font-size: var(--font-size-base);
  }

  .main-nav {
    gap: var(--spacing-small);
  }

  .nav-link {
    padding: var(--spacing-small);
    font-size: var(--font-size-small);
  }

  .app-main {
    padding: var(--spacing-medium) 0;
  }
}
</style>
