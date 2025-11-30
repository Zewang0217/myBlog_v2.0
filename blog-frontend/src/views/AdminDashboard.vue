<template>
  <div class="admin-dashboard">
    <h1 class="dashboard-title">管理员仪表盘</h1>
    
    <!-- 统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card" @click="handleStatClick('users')">
        <div class="stat-icon users">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"></path>
            <circle cx="9" cy="7" r="4"></circle>
            <path d="M22 21v-2a4 4 0 0 0-3-3.87"></path>
            <path d="M16 3.13a4 4 0 0 1 0 7.75"></path>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.totalUsers || 0 }}</div>
          <div class="stat-label">总用户数</div>
        </div>
      </div>
      
      <div class="stat-card" @click="handleStatClick('articles')">
        <div class="stat-icon articles">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
            <polyline points="14 2 14 8 20 8"></polyline>
            <line x1="16" y1="13" x2="8" y2="13"></line>
            <line x1="16" y1="17" x2="8" y2="17"></line>
            <polyline points="10 9 9 9 8 9"></polyline>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.totalArticles || 0 }}</div>
          <div class="stat-label">总文章数</div>
        </div>
      </div>
      
      <div class="stat-card" @click="handleStatClick('published')">
        <div class="stat-icon published">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <circle cx="12" cy="12" r="10"></circle>
            <polyline points="12 6 12 12 16 14"></polyline>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.publishedArticles || 0 }}</div>
          <div class="stat-label">已发布</div>
        </div>
      </div>
      
      <div class="stat-card" @click="handleStatClick('comments')">
        <div class="stat-icon comments">
          <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
            <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
          </svg>
        </div>
        <div class="stat-content">
          <div class="stat-number">{{ stats.totalComments || 0 }}</div>
          <div class="stat-label">总评论数</div>
        </div>
      </div>
    </div>
    
    <!-- 图表区域 -->
    <div class="charts-container">
      <div class="chart-card">
        <h2 class="chart-title">用户注册趋势</h2>
        <div class="chart-wrapper">
          <Line
            v-if="registrationChartData"
            :data="registrationChartData"
            :options="chartOptions"
          />
        </div>
      </div>
      
      <div class="chart-card">
        <h2 class="chart-title">文章分类统计</h2>
        <div class="chart-wrapper">
          <Doughnut
            v-if="categoryChartData"
            :data="categoryChartData"
            :options="doughnutOptions"
          />
        </div>
      </div>
    </div>
    
    <!-- 最近活动 -->
    <div class="activity-section">
      <h2 class="section-title">最近活动</h2>
      <div class="activity-list">
        <div v-if="recentActivities.length === 0" class="no-activities">
          <p>暂无最近活动</p>
        </div>
        <div v-for="(activity, index) in recentActivities" :key="index" class="activity-item">
          <div class="activity-icon" :class="activity.type">
            <svg v-if="activity.type === 'comment'" xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path>
            </svg>
            <svg v-else xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
              <path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"></path>
              <polyline points="14 2 14 8 20 8"></polyline>
              <line x1="16" y1="13" x2="8" y2="13"></line>
              <line x1="16" y1="17" x2="8" y2="17"></line>
              <polyline points="10 9 9 9 8 9"></polyline>
            </svg>
          </div>
          <div class="activity-content">
            <div class="activity-text">
              <template v-if="activity.type === 'comment'">
                用户 <strong>{{ activity.username }}</strong> 发表了评论
              </template>
              <template v-else>
                用户 <strong>{{ activity.username }}</strong> 发布了新内容
              </template>
            </div>
            <div class="activity-time">{{ formatTime(activity.createTime) }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { Line, Doughnut } from 'vue-chartjs'
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  ArcElement
} from 'chart.js'
import { useAuthStore } from '@/stores/auth'
import apiClient from '@/api/apiClient'

const router = useRouter()

// 注册Chart.js组件
ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  ArcElement
)

const authStore = useAuthStore()
const stats = ref<any>({})  
const loading = ref(true)
const recentActivities = ref<any[]>([])

// 图表数据
const registrationChartData = computed(() => {
  if (!stats.value.userRegistrationTrend) return null
  
  const trend = stats.value.userRegistrationTrend
  return {
    labels: trend.map((item: any) => item.date),
    datasets: [
      {
        label: '注册用户数',
        data: trend.map((item: any) => item.count),
        borderColor: '#409eff',
        backgroundColor: 'rgba(64, 158, 255, 0.1)',
        tension: 0.4,
        fill: true
      }
    ]
  }
})

const categoryChartData = computed(() => {
  if (!stats.value.articleCategoryStats) return null
  
  const categories = stats.value.articleCategoryStats
  return {
    labels: categories.map((item: any) => item.category),
    datasets: [
      {
        data: categories.map((item: any) => item.count),
        backgroundColor: [
          '#409eff',
          '#67c23a',
          '#e6a23c',
          '#f56c6c',
          '#909399',
          '#722ed1'
        ],
        borderWidth: 1
      }
    ]
  }
})

// 图表配置
const chartOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'top' as const
    }
  },
  scales: {
    y: {
      beginAtZero: true,
      ticks: {
        precision: 0
      }
    }
  }
}

const doughnutOptions = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      position: 'right' as const
    }
  }
}

// 格式化时间为相对时间
const formatTime = (timeString: string) => {
  const date = new Date(timeString)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  
  const minutes = Math.floor(diff / (1000 * 60))
  const hours = Math.floor(diff / (1000 * 60 * 60))
  const days = Math.floor(diff / (1000 * 60 * 60 * 24))
  
  if (minutes < 60) {
    return `${minutes}分钟前`
  } else if (hours < 24) {
    return `${hours}小时前`
  } else if (days < 30) {
    return `${days}天前`
  } else {
    return date.toLocaleDateString()
  }
}

// 处理统计卡片点击事件
const handleStatClick = (type: string) => {
  switch (type) {
    case 'users':
      // 查看、新增、删除用户功能
      router.push('/admin/users')
      break
    case 'articles':
      // 查看管理文章功能
      router.push('/articles')
      break
    case 'published':
      // 查看发布文章功能
      router.push('/articles')
      break
    case 'comments':
      // 查看最近评论功能
      router.push('/admin/comments')
      break
    default:
      break
  }
}

// 获取仪表盘数据
const fetchDashboardStats = async () => {
  try {
    const response = await apiClient.get('/api/admin/dashboard/stats')
    stats.value = response.data.data
  } catch (error) {
    console.error('Failed to fetch dashboard stats:', error)
  } finally {
    loading.value = false
  }
}

// 获取最近活动
const fetchRecentActivities = async () => {
  try {
    const response = await apiClient.get('/api/admin/dashboard/recent-activities')
    recentActivities.value = response.data.data
  } catch (error) {
    console.error('Failed to fetch recent activities:', error)
  }
}

onMounted(async () => {
  await fetchDashboardStats()
  await fetchRecentActivities()
})
</script>

<style scoped>
.admin-dashboard {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.dashboard-title {
  font-size: 2rem;
  margin-bottom: 2rem;
  color: var(--text-color-primary);
  text-align: center;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  gap: 20px;
}

.stat-card {
  cursor: pointer;
  transition: all 0.3s ease;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
}

.stat-icon.users {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-icon.articles {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-icon.published {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon.comments {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 2rem;
  font-weight: bold;
  color: var(--text-color-primary);
  margin-bottom: 4px;
}

.stat-label {
  font-size: 0.9rem;
  color: var(--text-color-regular);
}

.charts-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(500px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.chart-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.chart-title {
  font-size: 1.2rem;
  margin-bottom: 20px;
  color: var(--text-color-primary);
}

.chart-wrapper {
  height: 300px;
  width: 100%;
}

.activity-section {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
}

.section-title {
  font-size: 1.2rem;
  margin-bottom: 20px;
  color: var(--text-color-primary);
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.activity-item {
  display: flex;
  align-items: flex-start;
  gap: 16px;
  padding: 16px;
  border-radius: 8px;
  background: #f5f7fa;
  transition: all 0.3s ease;
}

.activity-item:hover {
  background: #ecf5ff;
}

.activity-icon {
  margin-top: 2px;
  color: #409eff;
}

.activity-content {
  flex: 1;
}

.activity-text {
  font-size: 0.95rem;
  color: var(--text-color-primary);
  margin-bottom: 4px;
}

.activity-time {
  font-size: 0.8rem;
  color: var(--text-color-regular);
}

/* 深色主题样式 */
[data-theme="dark"] .admin-dashboard {
  background-color: var(--background-secondary);
  color: var(--text-color-primary);
}

[data-theme="dark"] .stat-card {
  background-color: var(--background-glass);
  border: 1px solid var(--border-color-base);
  box-shadow: var(--box-shadow-base);
}

[data-theme="dark"] .chart-card {
  background-color: var(--background-glass);
  border: 1px solid var(--border-color-base);
  box-shadow: var(--box-shadow-base);
}

[data-theme="dark"] .activity-section {
  background-color: var(--background-glass);
  border: 1px solid var(--border-color-base);
  box-shadow: var(--box-shadow-base);
}

[data-theme="dark"] .activity-item {
  background-color: var(--background-secondary);
  border: 1px solid var(--border-color-base);
}

[data-theme="dark"] .activity-item:hover {
  background-color: rgba(129, 140, 248, 0.1);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .charts-container {
    grid-template-columns: 1fr;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 480px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .stat-card {
    flex-direction: column;
    text-align: center;
  }
}
</style>
