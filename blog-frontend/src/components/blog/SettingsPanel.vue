<template>
  <div class="settings-panel" :class="{ 'is-open': isOpen }">
    <div class="settings-overlay" @click="closePanel"></div>
    <div class="settings-content">
      <div class="settings-header">
        <h2>个性化设置</h2>
        <button class="close-btn" @click="closePanel">×</button>
      </div>

      <div class="settings-body">
        <!-- 主题切换 -->
        <div class="setting-group">
          <h3>主题模式</h3>
          <div class="theme-toggle">
            <button
              class="theme-btn"
              :class="{ active: themeSettings.theme === 'light' }"
              @click="setTheme('light')"
            >
              <span class="theme-icon">☀️</span>
              <span>浅色</span>
            </button>
            <button
              class="theme-btn"
              :class="{ active: themeSettings.theme === 'dark' }"
              @click="setTheme('dark')"
            >
              <span class="theme-icon">🌙</span>
              <span>深色</span>
            </button>
          </div>
        </div>

        <!-- 主题色选择 -->
        <div class="setting-group">
          <h3>主题色</h3>
          <div class="color-options">
            <button
              v-for="color in colorOptions"
              :key="color.value"
              class="color-option"
              :class="{ active: themeSettings.primaryColor === color.value }"
              :style="{ backgroundColor: color.value }"
              :title="color.name"
              @click="setPrimaryColor(color.value)"
            ></button>
          </div>
        </div>

        <!-- 字体大小 -->
        <div class="setting-group">
          <h3>字体大小</h3>
          <div class="font-size-options">
            <button
              v-for="size in fontSizeOptions"
              :key="size.value"
              class="font-size-btn"
              :class="{ active: themeSettings.fontSize === size.value }"
              @click="setFontSize(size.value as FontSize)"
            >
              {{ size.name }}
            </button>
          </div>
        </div>

        <!-- 阅读模式 -->
        <div class="setting-group">
          <h3>阅读模式</h3>
          <div class="reading-mode-options">
            <button
              v-for="mode in readingModeOptions"
              :key="mode.value"
              class="reading-mode-btn"
              :class="{ active: themeSettings.readingMode === mode.value }"
              @click="setReadingMode(mode.value as ReadingMode)"
            >
              {{ mode.name }}
            </button>
          </div>
        </div>

        <!-- 重置按钮 -->
        <div class="setting-group">
          <button class="reset-btn" @click="resetToDefault">
            恢复默认设置
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useTheme, type Theme, type FontSize, type ReadingMode } from '@/composables/useTheme'

// 使用主题组合函数
const {
  themeSettings,
  toggleTheme,
  setPrimaryColor,
  setFontSize,
  setReadingMode,
  resetToDefault,
  colorOptions,
  fontSizeOptions,
  readingModeOptions
} = useTheme()

// 设置面板是否打开
const isOpen = ref(false)

// 设置主题
const setTheme = (theme: Theme) => {
  // 直接修改themeSettings的theme属性
  themeSettings.value.theme = theme
}

// 打开设置面板
const openPanel = () => {
  isOpen.value = true
}

// 关闭设置面板
const closePanel = () => {
  isOpen.value = false
}

// 暴露方法给父组件
defineExpose({
  openPanel,
  closePanel
})
</script>

<style scoped>
.settings-panel {
  position: fixed;
  top: 0;
  right: 0;
  width: 100%;
  height: 100%;
  z-index: 1000;
  pointer-events: none;
}

.settings-panel.is-open {
  pointer-events: auto;
}

.settings-overlay {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.5);
  opacity: 0;
  transition: opacity 0.3s ease;
}

.settings-panel.is-open .settings-overlay {
  opacity: 1;
}

.settings-content {
  position: absolute;
  top: 0;
  right: 0;
  width: 100%;
  max-width: 360px;
  height: 100%;
  background-color: var(--background-color-white);
  transform: translateX(100%);
  transition: transform 0.3s ease;
  display: flex;
  flex-direction: column;
  box-shadow: -2px 0 8px rgba(0, 0, 0, 0.1);
}

.settings-panel.is-open .settings-content {
  transform: translateX(0);
}

.settings-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
  height: 60px;
  border-bottom: 1px solid var(--border-color-light);
  flex-shrink: 0;
}

.settings-header h2 {
  margin: 0;
  font-size: var(--font-size-large);
  color: var(--text-color-primary);
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: var(--text-color-secondary);
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  transition: background-color 0.2s;
}

.close-btn:hover {
  background-color: var(--background-color-base);
}

.settings-body {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.setting-group {
  margin-bottom: 30px;
}

.setting-group h3 {
  margin: 0 0 15px 0;
  font-size: var(--font-size-medium);
  color: var(--text-color-primary);
  font-weight: 500;
}

/* 主题切换按钮 */
.theme-toggle {
  display: flex;
  gap: 10px;
}

.theme-btn {
  flex: 1;
  padding: 12px;
  border: 1px solid var(--border-color-base);
  background-color: var(--background-color-white);
  border-radius: var(--border-radius-base);
  cursor: pointer;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  transition: all 0.2s;
}

.theme-btn:hover {
  border-color: var(--primary-color);
}

.theme-btn.active {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
}

.theme-icon {
  font-size: 20px;
}

/* 颜色选项 */
.color-options {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.color-option {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  border: 2px solid var(--border-color-light);
  cursor: pointer;
  transition: transform 0.2s, border-color 0.2s;
}

.color-option:hover {
  transform: scale(1.1);
}

.color-option.active {
  border-color: var(--text-color-primary);
  transform: scale(1.1);
}

/* 字体大小选项 */
.font-size-options {
  display: flex;
  gap: 10px;
}

.font-size-btn {
  flex: 1;
  padding: 10px;
  border: 1px solid var(--border-color-base);
  background-color: var(--background-color-white);
  border-radius: var(--border-radius-base);
  cursor: pointer;
  transition: all 0.2s;
}

.font-size-btn:hover {
  border-color: var(--primary-color);
}

.font-size-btn.active {
  border-color: var(--primary-color);
  background-color: var(--primary-color);
  color: white;
}

/* 阅读模式选项 */
.reading-mode-options {
  display: flex;
  gap: 10px;
}

.reading-mode-btn {
  flex: 1;
  padding: 10px;
  border: 1px solid var(--border-color-base);
  background-color: var(--background-color-white);
  border-radius: var(--border-radius-base);
  cursor: pointer;
  transition: all 0.2s;
}

.reading-mode-btn:hover {
  border-color: var(--primary-color);
}

.reading-mode-btn.active {
  border-color: var(--primary-color);
  background-color: var(--primary-color);
  color: white;
}

/* 重置按钮 */
.reset-btn {
  width: 100%;
  padding: 12px;
  background-color: var(--background-color-base);
  border: 1px solid var(--border-color-base);
  border-radius: var(--border-radius-base);
  cursor: pointer;
  color: var(--text-color-primary);
  font-size: var(--font-size-base);
  transition: all 0.2s;
}

.reset-btn:hover {
  background-color: var(--border-color-light);
}

/* 响应式设计 */
@media (max-width: 480px) {
  .settings-content {
    max-width: 100%;
  }

  .font-size-options,
  .reading-mode-options,
  .theme-toggle {
    flex-direction: column;
    gap: 8px;
  }

  .font-size-btn,
  .reading-mode-btn,
  .theme-btn {
    width: 100%;
  }
}
</style>
