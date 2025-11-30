<template>
  <div class="clock-widget card">
    <div class="clock-header">
      <h3 class="clock-title">
        <svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
          <circle cx="12" cy="12" r="10"></circle>
          <polyline points="12 6 12 12 16 14"></polyline>
        </svg>
        时钟
      </h3>
      <div class="clock-date">{{ formattedDate }}</div>
    </div>
    <div class="clock-body">
      <div class="clock-container">
        <div class="clock-face">
          <!-- 时钟刻度 -->
          <div v-for="i in 12" :key="i" class="clock-tick" :style="{ transform: `rotate(${i * 30}deg)` }">
            <div class="tick-inner"></div>
          </div>
          
          <!-- 时钟指针 -->
          <div class="clock-hands">
            <div class="clock-hand hour-hand" :style="{ transform: `rotate(${hourAngle}deg)` }">
              <div class="hand-inner"></div>
            </div>
            <div class="clock-hand minute-hand" :style="{ transform: `rotate(${minuteAngle}deg)` }">
              <div class="hand-inner"></div>
            </div>
            <div class="clock-hand second-hand" :style="{ transform: `rotate(${secondAngle}deg)` }">
              <div class="hand-inner"></div>
            </div>
            <div class="clock-center"></div>
          </div>
        </div>
      </div>
      <div class="digital-clock">
        {{ formattedTime }}
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, computed } from 'vue'

// 响应式时间数据
const now = ref(new Date())

// 计算时钟指针角度
const hourAngle = computed(() => {
  const hours = now.value.getHours() % 12
  const minutes = now.value.getMinutes()
  return (hours * 30) + (minutes * 0.5)
})

const minuteAngle = computed(() => {
  const minutes = now.value.getMinutes()
  const seconds = now.value.getSeconds()
  return (minutes * 6) + (seconds * 0.1)
})

const secondAngle = computed(() => {
  return now.value.getSeconds() * 6
})

// 格式化时间
const formattedTime = computed(() => {
  return now.value.toLocaleTimeString('zh-CN', {
    hour12: false,
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  })
})

// 格式化日期
const formattedDate = computed(() => {
  return now.value.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
    weekday: 'long'
  })
})

// 定时器引用
let timer: number | null = null

// 更新时间
const updateTime = () => {
  now.value = new Date()
}

// 组件挂载时启动定时器
onMounted(() => {
  timer = window.setInterval(updateTime, 1000)
})

// 组件卸载时清除定时器
onUnmounted(() => {
  if (timer) {
    clearInterval(timer)
  }
})
</script>

<style scoped>
.clock-widget {
  width: 100%;
  max-width: 320px;
  padding: 20px;
  background: linear-gradient(135deg, var(--background-gradient-start), var(--background-gradient-end));
  border: 1px solid var(--border-color-light);
  box-shadow: var(--box-shadow-base);
  transition: all 0.3s ease;
}

.clock-widget:hover {
  transform: translateY(-5px);
  box-shadow: var(--box-shadow-hover);
}

.clock-header {
  margin-bottom: 20px;
  text-align: center;
}

.clock-title {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 1.2rem;
  font-weight: 600;
  color: var(--text-color-primary);
  margin: 0 0 8px 0;
}

.clock-date {
  font-size: 0.9rem;
  color: var(--text-color-secondary);
}

.clock-body {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 20px;
}

.clock-container {
  width: 200px;
  height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.clock-face {
  width: 100%;
  height: 100%;
  background: var(--background-color-white);
  border-radius: 50%;
  position: relative;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  border: 3px solid var(--primary-color);
}

.clock-tick {
  position: absolute;
  top: 0;
  left: 50%;
  width: 2px;
  height: 100%;
  transform-origin: center;
}

.tick-inner {
  width: 100%;
  height: 10px;
  background: var(--primary-color);
  position: absolute;
  top: 10px;
  left: 50%;
  transform: translateX(-50%);
}

/* 每3小时的刻度更粗 */
.clock-tick:nth-child(3n+1) .tick-inner {
  height: 15px;
  width: 3px;
  background: var(--primary-color-dark);
}

.clock-hands {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.clock-hand {
  position: absolute;
  top: 0;
  left: 50%;
  width: 100%;
  height: 100%;
  transform-origin: center center;
  display: flex;
  align-items: center;
  justify-content: center;
}

.hand-inner {
  background: var(--text-color-primary);
  position: absolute;
  transform-origin: bottom center;
  border-radius: 4px;
}

.hour-hand .hand-inner {
  width: 6px;
  height: 50px;
  background: var(--primary-color-dark);
  bottom: 50%;
  left: 50%;
  transform: translateX(-50%);
}

.minute-hand .hand-inner {
  width: 4px;
  height: 70px;
  background: var(--text-color-primary);
  bottom: 50%;
  left: 50%;
  transform: translateX(-50%);
}

.second-hand .hand-inner {
  width: 2px;
  height: 80px;
  background: var(--danger-color);
  bottom: 50%;
  left: 50%;
  transform: translateX(-50%);
}

.clock-center {
  width: 12px;
  height: 12px;
  background: var(--primary-color);
  border-radius: 50%;
  position: absolute;
  z-index: 10;
  box-shadow: 0 0 0 2px var(--background-color-white), 0 0 0 4px var(--primary-color);
}

.digital-clock {
  font-size: 2rem;
  font-weight: 700;
  color: var(--text-color-primary);
  font-family: 'Courier New', Courier, monospace;
  background: linear-gradient(135deg, var(--primary-color), var(--primary-color-dark));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
  text-fill-color: transparent;
  padding: 10px 20px;
  border-radius: 8px;
  background-color: rgba(99, 102, 241, 0.1);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .clock-widget {
    max-width: 100%;
  }
  
  .clock-container {
    width: 160px;
    height: 160px;
  }
  
  .hour-hand .hand-inner {
    height: 40px;
  }
  
  .minute-hand .hand-inner {
    height: 55px;
  }
  
  .second-hand .hand-inner {
    height: 65px;
  }
  
  .digital-clock {
    font-size: 1.5rem;
  }
}
</style>
