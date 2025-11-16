<template>
  <div class="welcome-container">
    <section class="hero-section">
      <div class="hero-content">
        <h1 class="hero-title">欢迎来到我的博客</h1>
        <p class="hero-subtitle">一个现代化、功能丰富的博客平台</p>
        <p class="hero-description">
          在这里，您可以轻松创建、管理和分享您的想法和知识。
          享受流畅的写作体验和优雅的阅读界面。
        </p>
        <div class="hero-buttons">
          <RouterLink to="/articles" class="btn btn-primary">开始浏览</RouterLink>
          <template v-if="!isAuthenticated">
            <RouterLink to="/login" class="btn btn-secondary">立即登录</RouterLink>
          </template>
          <template v-else>
            <RouterLink to="/article/create" class="btn btn-secondary">撰写文章</RouterLink>
          </template>
        </div>
      </div>
    </section>

    <section class="features-section">
      <div class="container">
        <h2 class="section-title">特色功能</h2>
        <div class="features-grid">
          <div class="feature-card">
            <div class="feature-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M12 20a8 8 0 1 0 0-16 8 8 0 0 0 0 16z"></path>
                <path d="M12 14a2 2 0 1 0 0-4 2 2 0 0 0 0 4z"></path>
                <path d="M12 2v2"></path>
                <path d="M12 22v-2"></path>
                <path d="m17 20.66-1-1.73"></path>
                <path d="M11 10.27 7 3.34"></path>
                <path d="m20.66 17-1.73-1"></path>
                <path d="m3.34 7 1.73 1"></path>
                <path d="M14 12h8"></path>
                <path d="M2 12h2"></path>
                <path d="m20.66 7-1.73 1"></path>
                <path d="m3.34 17 1.73-1"></path>
                <path d="m17 3.34-1 1.73"></path>
                <path d="m11 13.73-4 6.93"></path>
              </svg>
            </div>
            <h3 class="feature-title">现代化UI</h3>
            <p class="feature-description">
              采用最新的UI设计原则，提供流畅、直观的用户体验
            </p>
          </div>

          <div class="feature-card">
            <div class="feature-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M12 20a8 8 0 1 0 0-16 8 8 0 0 0 0 16z"></path>
                <path d="M12 14a2 2 0 1 0 0-4 2 2 0 0 0 0 4z"></path>
                <path d="M12 2v2"></path>
                <path d="M12 22v-2"></path>
                <path d="m17 20.66-1-1.73"></path>
                <path d="M11 10.27 7 3.34"></path>
                <path d="m20.66 17-1.73-1"></path>
                <path d="m3.34 7 1.73 1"></path>
                <path d="M14 12h8"></path>
                <path d="M2 12h2"></path>
                <path d="m20.66 7-1.73 1"></path>
                <path d="m3.34 17 1.73-1"></path>
                <path d="m17 3.34-1 1.73"></path>
                <path d="m11 13.73-4 6.93"></path>
              </svg>
            </div>
            <h3 class="feature-title">Markdown编辑器</h3>
            <p class="feature-description">
              强大的Markdown编辑器，支持实时预览和丰富的格式选项
            </p>
          </div>

          <div class="feature-card">
            <div class="feature-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
              </svg>
            </div>
            <h3 class="feature-title">安全可靠</h3>
            <p class="feature-description">
              采用JWT认证和安全的后端架构，保护您的数据安全
            </p>
          </div>

          <div class="feature-card">
            <div class="feature-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M12 20a8 8 0 1 0 0-16 8 8 0 0 0 0 16z"></path>
                <path d="M12 14a2 2 0 1 0 0-4 2 2 0 0 0 0 4z"></path>
                <path d="M12 2v2"></path>
                <path d="M12 22v-2"></path>
                <path d="m17 20.66-1-1.73"></path>
                <path d="M11 10.27 7 3.34"></path>
                <path d="m20.66 17-1.73-1"></path>
                <path d="m3.34 7 1.73 1"></path>
                <path d="M14 12h8"></path>
                <path d="M2 12h2"></path>
                <path d="m20.66 7-1.73 1"></path>
                <path d="m3.34 17 1.73-1"></path>
                <path d="m17 3.34-1 1.73"></path>
                <path d="m11 13.73-4 6.93"></path>
              </svg>
            </div>
            <h3 class="feature-title">响应式设计</h3>
            <p class="feature-description">
              完美适配各种设备，无论桌面还是移动设备都能获得最佳体验
            </p>
          </div>
        </div>
      </div>
    </section>

    <section class="stats-section">
      <div class="container">
        <div class="stats-grid">
          <div class="stat-item">
            <div class="stat-number">100+</div>
            <div class="stat-label">文章</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">50+</div>
            <div class="stat-label">作者</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">10K+</div>
            <div class="stat-label">读者</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">99.9%</div>
            <div class="stat-label">可用性</div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useAuthStore } from '@/stores/auth'

const authStore = useAuthStore()
const isAuthenticated = computed(() => authStore.isAuthenticated)
</script>

<style scoped>
.welcome-container {
  min-height: calc(100vh - 116px);
  background: linear-gradient(135deg, var(--primary-color-light, #409eff) 0%, var(--primary-color, #1890ff) 100%);
  color: white;
}

.hero-section {
  padding: 80px 0;
  text-align: center;
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 20px;
}

.hero-title {
  font-size: 3rem;
  margin-bottom: 1rem;
  font-weight: 700;
  text-shadow: 0 2px 4px rgba(0,0,0,0.1);
}

.hero-subtitle {
  font-size: 1.5rem;
  margin-bottom: 1rem;
  opacity: 0.9;
}

.hero-description {
  font-size: 1.1rem;
  margin-bottom: 2rem;
  line-height: 1.6;
  opacity: 0.85;
}

.hero-buttons {
  display: flex;
  gap: 1rem;
  justify-content: center;
  flex-wrap: wrap;
}

.btn {
  padding: 12px 30px;
  border-radius: var(--border-radius-large, 8px);
  text-decoration: none;
  font-weight: 600;
  transition: all 0.3s;
  border: 2px solid transparent;
  display: inline-block;
  cursor: pointer;
}

.btn-primary {
  background: linear-gradient(135deg, var(--primary-color), var(--primary-color-dark));
  color: var(--background-color-white);
  border: none;
}

.btn-primary:hover {
  background: linear-gradient(135deg, var(--primary-color-light), var(--primary-color));
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(67, 56, 202, 0.3);
}

.btn-secondary {
  background-color: transparent;
  color: white;
  border-color: white;
}

.btn-secondary:hover {
  background-color: rgba(255, 255, 255, 0.1);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.features-section {
  padding: 80px 0;
  background-color: white;
  color: var(--text-color-primary, #303133);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.section-title {
  text-align: center;
  font-size: 2.5rem;
  margin-bottom: 3rem;
  color: var(--text-color-primary, #303133);
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 2rem;
}

.feature-card {
  text-align: center;
  padding: 2rem;
  border-radius: var(--border-radius-large, 8px);
  background: var(--background-color-white, #ffffff);
  box-shadow: var(--box-shadow-base, 0 2px 12px 0 rgba(0, 0, 0, 0.1));
  transition: all 0.3s;
}

.feature-card:hover {
  transform: translateY(-5px);
  box-shadow: var(--box-shadow-hover, 0 4px 24px 0 rgba(0, 0, 0, 0.15));
}

.feature-icon {
  margin-bottom: 1rem;
}

.feature-title {
  font-size: 1.5rem;
  margin-bottom: 1rem;
  color: var(--text-color-primary, #303133);
}

.feature-description {
  color: var(--text-color-regular, #606266);
  line-height: 1.6;
}

.stats-section {
  padding: 60px 0;
  background: linear-gradient(to right, var(--primary-color, #1890ff), var(--primary-color-dark, #096dd9));
  color: white;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 2rem;
  text-align: center;
}

.stat-item {
  padding: 1.5rem;
}

.stat-number {
  font-size: 2.5rem;
  font-weight: bold;
  margin-bottom: 0.5rem;
}

.stat-label {
  font-size: 1.2rem;
  opacity: 0.9;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .hero-title {
    font-size: 2rem;
  }
  
  .hero-subtitle {
    font-size: 1.2rem;
  }
  
  .section-title {
    font-size: 2rem;
  }
  
  .hero-buttons {
    flex-direction: column;
    align-items: center;
  }
  
  .btn {
    width: 200px;
  }
}
</style>