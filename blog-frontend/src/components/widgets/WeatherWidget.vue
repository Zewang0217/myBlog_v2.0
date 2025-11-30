<template>
  <div class="weather-widget card">
    <div class="weather-header">
      <h3 class="weather-title">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
        </svg>
        天气
      </h3>
      <div class="weather-location">{{ weatherData.city }}</div>
    </div>
    <div class="weather-body">
      <div v-if="loading" class="weather-loading">
        <div class="loading-spinner"></div>
        <div class="loading-text">加载中...</div>
      </div>
      <div v-else-if="error" class="weather-error">
        <div class="error-text">{{ error }}</div>
      </div>
      <div v-else class="weather-content">
        <div class="weather-main">
          <div class="weather-icon">
            <svg v-if="weatherData.icon === 'sunny'" xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
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
            <svg v-else-if="weatherData.icon === 'cloudy'" xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M17.5 19H9a7 7 0 1 1 6.71-9h1.79a4.5 4.5 0 1 1 0 9Z"></path>
            </svg>
            <svg v-else-if="weatherData.icon === 'rainy'" xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M4 14.899A7 7 0 1 1 15.71 8h1.79a4.5 4.5 0 1 1 0 9H6.5"></path>
              <path d="M6 2v2"></path>
              <path d="M10 2v2"></path>
              <path d="M14 2v2"></path>
            </svg>
            <svg v-else-if="weatherData.icon === 'snowy'" xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M8 16a5 5 0 0 1 7 0"></path>
              <path d="M11 12a5 5 0 0 1 7 0"></path>
              <path d="M14 8a5 5 0 0 1 7 0"></path>
              <path d="M4 14.899A7 7 0 1 1 15.71 8h1.79a4.5 4.5 0 1 1 0 9H6.5"></path>
            </svg>
            <svg v-else xmlns="http://www.w3.org/2000/svg" width="64" height="64" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
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
          </div>
          <div class="weather-temp">{{ weatherData.temperature }}°C</div>
          <div class="weather-desc">{{ weatherData.description }}</div>
        </div>
        <div class="weather-details">
          <div class="weather-detail-item">
            <div class="detail-label">湿度</div>
            <div class="detail-value">{{ weatherData.humidity }}%</div>
          </div>
          <div class="weather-detail-item">
            <div class="detail-label">风速</div>
            <div class="detail-value">{{ weatherData.windSpeed }} km/h</div>
          </div>
          <div class="weather-detail-item">
            <div class="detail-label">气压</div>
            <div class="detail-value">{{ weatherData.pressure }} hPa</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'

// 天气数据接口
interface WeatherData {
  city: string
  temperature: number
  description: string
  humidity: number
  windSpeed: number
  pressure: number
  icon: string
}

// 响应式天气数据
const weatherData = ref<WeatherData>({
  city: '北京',
  temperature: 22,
  description: '晴朗',
  humidity: 55,
  windSpeed: 12,
  pressure: 1013,
  icon: 'sunny'
})

const loading = ref(false)
const error = ref('')

// 获取天气数据（模拟）
const fetchWeatherData = async () => {
  loading.value = true
  error.value = ''
  
  try {
    // 模拟API请求延迟
    await new Promise(resolve => setTimeout(resolve, 500))
    
    // 模拟天气数据，实际项目中应该调用真实的天气API
    const mockData = {
      city: '北京',
      temperature: Math.round(Math.random() * 15 + 15), // 15-30°C
      description: ['晴朗', '多云', '阴天', '小雨', '雷阵雨'][Math.floor(Math.random() * 5)],
      humidity: Math.round(Math.random() * 40 + 40), // 40-80%
      windSpeed: Math.round(Math.random() * 20 + 5), // 5-25 km/h
      pressure: Math.round(Math.random() * 20 + 1000), // 1000-1020 hPa
      icon: ['sunny', 'cloudy', 'cloudy', 'rainy', 'rainy'][Math.floor(Math.random() * 5)]
    }
    
    weatherData.value = mockData
  } catch (err) {
    error.value = '获取天气数据失败'
    console.error('Failed to fetch weather data:', err)
  } finally {
    loading.value = false
  }
}

// 组件挂载时获取天气数据
onMounted(() => {
  fetchWeatherData()
})
</script>

<style scoped>
.weather-widget {
  width: 100%;
  max-width: 320px;
  padding: 20px;
  background: linear-gradient(135deg, var(--background-gradient-start), var(--background-gradient-end));
  border: 1px solid var(--border-color-light);
  box-shadow: var(--box-shadow-base);
  transition: all 0.3s ease;
}

.weather-widget:hover {
  transform: translateY(-5px);
  box-shadow: var(--box-shadow-hover);
}

.weather-header {
  margin-bottom: 20px;
  text-align: center;
}

.weather-title {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 1.2rem;
  font-weight: 600;
  color: var(--text-color-primary);
  margin: 0 0 8px 0;
}

.weather-location {
  font-size: 0.9rem;
  color: var(--text-color-secondary);
}

.weather-body {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.weather-loading, .weather-error {
  text-align: center;
  padding: 20px 0;
  color: var(--text-color-secondary);
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid var(--border-color-light);
  border-top: 3px solid var(--primary-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin: 0 auto 10px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.weather-main {
  text-align: center;
}

.weather-icon {
  margin-bottom: 16px;
  color: var(--primary-color);
}

.weather-temp {
  font-size: 3rem;
  font-weight: 700;
  color: var(--text-color-primary);
  margin-bottom: 8px;
  line-height: 1;
}

.weather-desc {
  font-size: 1.1rem;
  color: var(--text-color-secondary);
  margin-bottom: 16px;
}

.weather-details {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  padding-top: 16px;
  border-top: 1px solid var(--border-color-light);
}

.weather-detail-item {
  text-align: center;
}

.detail-label {
  font-size: 0.85rem;
  color: var(--text-color-secondary);
  margin-bottom: 4px;
}

.detail-value {
  font-size: 1.1rem;
  font-weight: 600;
  color: var(--text-color-primary);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .weather-widget {
    max-width: 100%;
  }
}
</style>
