import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'
import { useTheme } from '@/composables/useTheme'

const app = createApp(App)

app.use(createPinia())
app.use(router)

// 初始化主题设置
const appInstance = app.mount('#app')
useTheme() // 初始化主题设置
