import { ref, watch, onMounted } from 'vue'

// 定义主题类型
export type Theme = 'light' | 'dark'
export type FontSize = 'small' | 'medium' | 'large'
export type ReadingMode = 'normal' | 'reading'

// 主题设置接口
export interface ThemeSettings {
  theme: Theme
  primaryColor: string
  fontSize: FontSize
  readingMode: ReadingMode
}

const DEFAULT_SETTINGS: ThemeSettings = {
  theme: 'light',
  primaryColor: '#409eff',
  fontSize: 'medium',
  readingMode: 'normal'
}

// 创建响应式主题设置
const themeSettings = ref<ThemeSettings>({ ...DEFAULT_SETTINGS })

// 可用的主题色选项
export const colorOptions = [
  { name: '默认蓝', value: '#409eff' },
  { name: '科技蓝', value: '#1890ff' },
  { name: '活力橙', value: '#ff7a45' },
  { name: '优雅紫', value: '#722ed1' },
  { name: '清新绿', value: '#52c41a' },
  { name: '热情红', value: '#f5222d' }
]

// 字体大小选项
export const fontSizeOptions = [
  { name: '小号', value: 'small' },
  { name: '中号', value: 'medium' },
  { name: '大号', value: 'large' }
]

// 阅读模式选项
export const readingModeOptions = [
  { name: '标准模式', value: 'normal' },
  { name: '阅读模式', value: 'reading' }
]

export function useTheme() {
  // 初始化主题设置
  const initTheme = () => {
    const savedSettings = localStorage.getItem('blog-theme-settings')
    if (savedSettings) {
      try {
        const parsedSettings = JSON.parse(savedSettings)
        themeSettings.value = { ...DEFAULT_SETTINGS, ...parsedSettings }
      } catch (e) {
        console.error('Failed to parse theme settings', e)
      }
    }
    
    // 应用主题设置
    applyThemeSettings()
  }

  // 应用主题设置到DOM
  const applyThemeSettings = () => {
    // 设置主题
    document.documentElement.setAttribute('data-theme', themeSettings.value.theme)
    
    // 设置主题色
    document.documentElement.style.setProperty('--primary-color', themeSettings.value.primaryColor)
    
    // 设置字体大小
    let fontSizeValue = ''
    switch (themeSettings.value.fontSize) {
      case 'small':
        fontSizeValue = '13px'
        break
      case 'large':
        fontSizeValue = '17px'
        break
      case 'medium':
      default:
        fontSizeValue = '15px'
        break
    }
    document.documentElement.style.setProperty('--font-size-base', fontSizeValue)
    
    // 设置阅读模式
    if (themeSettings.value.readingMode === 'reading') {
      document.body.classList.add('reading-mode')
    } else {
      document.body.classList.remove('reading-mode')
    }
  }

  // 切换主题
  const toggleTheme = () => {
    themeSettings.value.theme = themeSettings.value.theme === 'light' ? 'dark' : 'light'
    saveAndApplySettings()
  }

  // 设置主题色
  const setPrimaryColor = (color: string) => {
    themeSettings.value.primaryColor = color
    saveAndApplySettings()
  }

  // 设置字体大小
  const setFontSize = (size: FontSize) => {
    themeSettings.value.fontSize = size
    saveAndApplySettings()
  }

  // 设置阅读模式
  const setReadingMode = (mode: ReadingMode) => {
    themeSettings.value.readingMode = mode
    saveAndApplySettings()
  }

  // 重置为默认设置
  const resetToDefault = () => {
    themeSettings.value = { ...DEFAULT_SETTINGS }
    saveAndApplySettings()
  }

  // 保存设置并应用
  const saveAndApplySettings = () => {
    localStorage.setItem('blog-theme-settings', JSON.stringify(themeSettings.value))
    applyThemeSettings()
  }

  // 监听设置变化并保存
  watch(themeSettings, saveAndApplySettings, { deep: true })

  // 组件挂载时初始化
  onMounted(() => {
    initTheme()
  })

  return {
    themeSettings,
    toggleTheme,
    setPrimaryColor,
    setFontSize,
    setReadingMode,
    resetToDefault,
    colorOptions,
    fontSizeOptions,
    readingModeOptions
  }
}