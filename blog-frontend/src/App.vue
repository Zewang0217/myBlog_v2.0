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
        </div>
      </div>
    </header>

    <!-- 主要内容区域 -->
    <div class="app-main-container">
      <!-- 左侧导航栏 -->
      <nav class="side-nav">
        <ul class="nav-list">
          <li class="nav-item">
            <RouterLink to="/articles" class="nav-link" active-class="active">文章列表</RouterLink>
          </li>
          <li class="nav-item">
            <RouterLink to="/articles/drafts" class="nav-link" active-class="active">草稿箱</RouterLink>
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

/* 主容器布局 */
.app-main-container {
  display: flex;
  flex: 1;
}

/* 左侧导航栏 */
.side-nav {
  width: 200px;
  background-color: var(--background-color-base);
  padding: 20px 0;
  border-right: 1px solid var(--border-color-light);
  min-height: calc(100vh - 60px - 56px); /* 减去头部和底部高度 */
}

.nav-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.nav-item {
  margin-bottom: 5px;
}

.nav-link {
  display: block;
  padding: 12px 20px;
  color: var(--text-color-regular);
  text-decoration: none;
  font-weight: 500;
  transition: all 0.3s;
  border-left: 3px solid transparent;
}

.nav-link:hover,
.nav-link.active {
  color: var(--primary-color);
  background-color: var(--background-color-white);
  border-left-color: var(--primary-color);
}

/* 主内容区 */
.app-main {
  flex: 1;
  padding: var(--spacing-large);
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

  .app-main-container {
    flex-direction: column;
  }

  .side-nav {
    width: 100%;
    min-height: auto;
    border-right: none;
    border-bottom: 1px solid var(--border-color-light);
  }

  .nav-list {
    display: flex;
  }

  .nav-item {
    flex: 1;
    margin-bottom: 0;
  }

  .nav-link {
    text-align: center;
    border-left: none;
    border-bottom: 3px solid transparent;
  }

  .nav-link:hover,
  .nav-link.active {
    border-left: none;
    border-bottom-color: var(--primary-color);
  }

  .app-main {
    padding: var(--spacing-medium);
  }
}
</style>
