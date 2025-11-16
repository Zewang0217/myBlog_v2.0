<template>
  <div class="login-container">
    <div class="login-wrapper">
      <div class="login-header">
        <div class="logo">
          <svg width="48" height="48" viewBox="0 0 24 24" fill="currentColor">
            <path d="M12 2L2 7v10c0 5.55 3.84 10.74 9 12 5.16-1.26 9-6.45 9-12V7l-10-5z"/>
          </svg>
        </div>
        <h1>欢迎回来</h1>
        <p>登录您的账户继续探索</p>
      </div>
      
      <div class="login-form">
        <form @submit.prevent="handleLogin">
          <div class="form-group">
            <label for="username">用户名</label>
            <div class="input-wrapper">
              <svg class="input-icon" width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                <path d="M12 12c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm0 2c-2.67 0-8 1.34-8 4v2h16v-2c0-2.66-5.33-4-8-4z"/>
              </svg>
              <input
                id="username"
                v-model="loginForm.username"
                type="text"
                required
                class="form-input"
                placeholder="请输入用户名"
              />
            </div>
          </div>

          <div class="form-group">
            <label for="password">密码</label>
            <div class="input-wrapper">
              <svg class="input-icon" width="20" height="20" viewBox="0 0 24 24" fill="currentColor">
                <path d="M18 8h-1V6c0-2.76-2.24-5-5-5S7 3.24 7 6v2H6c-1.1 0-2 .9-2 2v10c0 1.1.9 2 2 2h12c1.1 0 2-.9 2-2V10c0-1.1-.9-2-2-2zm-6 9c-1.1 0-2-.9-2-2s.9-2 2-2 2 .9 2 2-.9 2-2 2zm3.1-9H8.9V6c0-1.71 1.39-3.1 3.1-3.1 1.71 0 3.1 1.39 3.1 3.1v2z"/>
              </svg>
              <input
                id="password"
                v-model="loginForm.password"
                type="password"
                required
                class="form-input"
                placeholder="请输入密码"
              />
            </div>
          </div>
          
          <button type="submit" :disabled="loading" class="btn btn-primary">
            <svg v-if="loading" class="loading-spinner" width="20" height="20" viewBox="0 0 24 24">
              <path fill="currentColor" d="M12 4V1L8 5l4 4V6c3.31 0 6 2.69 6 6s-2.69 6-6 6-6-2.69-6-6H4c0 4.42 3.58 8 8 8s8-3.58 8-8-3.58-8-8-8z"/>
            </svg>
            {{ loading ? '登录中...' : '登录' }}
          </button>
          
          <div v-if="error" class="error-message">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 15h-2v-2h2v2zm0-4h-2V7h2v6z"/>
            </svg>
            {{ error }}
          </div>
        </form>

        <div class="form-footer">
          <div class="register-link">
            还没有账户？ <router-link to="/register">立即注册</router-link>
          </div>
          <div class="guest-hint">
            <svg width="16" height="16" viewBox="0 0 24 24" fill="currentColor">
              <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm-2 15l-5-5 1.41-1.41L10 14.17l7.59-7.59L19 8l-9 9z"/>
            </svg>
            探索更多功能，请登录您的账户
          </div>
        </div>
      </div>
    </div>
    
    <div class="login-background">
      <div class="bg-element bg-1"></div>
      <div class="bg-element bg-2"></div>
      <div class="bg-element bg-3"></div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = useRouter()
const authStore = useAuthStore()

const loginForm = ref({
  username: '',
  password: ''
})

const loading = ref(false)
const error = ref('')

const handleLogin = async () => {
  loading.value = true
  error.value = ''

  try {
    await authStore.login(loginForm.value)
    router.push('/articles')
  } catch (err: any) {
    // 更详细的错误处理
    if (err.response?.data?.message) {
      error.value = err.response.data.message
    } else if (err.response?.status === 401) {
      error.value = '用户名或密码错误'
    } else if (err.response?.status === 500) {
      error.value = '服务器内部错误，请稍后重试'
    } else if (err.message) {
      error.value = err.message
    } else {
      error.value = '登录失败，请检查用户名和密码'
    }
  } finally {
    loading.value = false
  }
}

</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.login-wrapper {
  width: 100%;
  max-width: 420px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.2);
  position: relative;
  z-index: 10;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.logo svg {
  color: #667eea;
  filter: drop-shadow(0 4px 8px rgba(102, 126, 234, 0.3));
}

.login-header h1 {
  margin: 0 0 10px 0;
  font-size: 2.2em;
  font-weight: 700;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  letter-spacing: -0.5px;
}

.login-header p {
  margin: 0;
  color: #718096;
  font-size: 1.1em;
  font-weight: 500;
}

.form-group {
  margin-bottom: 25px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: 600;
  color: #4a5568;
  font-size: 0.95em;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 15px;
  color: #a0aec0;
  z-index: 1;
}

.form-input {
  width: 100%;
  padding: 15px 15px 15px 45px;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 1rem;
  background: white;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1), 0 4px 12px rgba(102, 126, 234, 0.2);
  transform: translateY(-1px);
}

.form-input::placeholder {
  color: #a0aec0;
  font-weight: 500;
}

.btn {
  display: inline-block;
  padding: 15px 2rem;
  border: none;
  border-radius: 12px;
  cursor: pointer;
  font-size: 1.1rem;
  font-weight: 600;
  text-align: center;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.btn-primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  width: 100%;
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 8px 25px rgba(102, 126, 234, 0.4);
}

.btn-primary:disabled {
  background: #a0aec0;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.loading-spinner {
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.error-message {
  margin-top: 1rem;
  padding: 12px 16px;
  background: linear-gradient(135deg, #fff5f5, #fed7d7);
  color: #e53e3e;
  border-radius: 8px;
  text-align: center;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-weight: 500;
  border: 1px solid #feb2b2;
}

.form-footer {
  margin-top: 30px;
  text-align: center;
}

.register-link {
  margin-bottom: 15px;
}

.register-link a {
  color: #667eea;
  text-decoration: none;
  font-weight: 600;
  transition: color 0.3s ease;
}

.register-link a:hover {
  color: #764ba2;
  text-decoration: underline;
}

.guest-hint {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #718096;
  font-size: 0.9em;
  font-weight: 500;
}

/* 背景动画元素 */
.login-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1;
  pointer-events: none;
}

.bg-element {
  position: absolute;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.1);
  animation: float 6s ease-in-out infinite;
}

.bg-1 {
  width: 80px;
  height: 80px;
  top: 20%;
  left: 10%;
  animation-delay: 0s;
}

.bg-2 {
  width: 120px;
  height: 120px;
  top: 60%;
  right: 10%;
  animation-delay: 2s;
}

.bg-3 {
  width: 60px;
  height: 60px;
  bottom: 20%;
  left: 80%;
  animation-delay: 4s;
}

@keyframes float {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(180deg); }
}

/* 响应式设计 */
@media (max-width: 480px) {
  .login-wrapper {
    margin: 20px;
    padding: 30px 20px;
  }
  
  .login-header h1 {
    font-size: 1.8em;
  }
  
  .form-input {
    padding: 12px 12px 12px 40px;
  }
  
  .input-icon {
    left: 12px;
  }
}
</style>
