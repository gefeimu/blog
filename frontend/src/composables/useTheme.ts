/**
 * 全局暗色模式
 * - 类名挂在 html 上：html.dark（Element Plus 暗色变量按此选择器生效）
 * - 前台自定义样式在 style.css 里用 html.dark 覆盖 CSS 变量
 * - 偏好存 localStorage，默认跟随系统 prefers-color-scheme
 */
import { ref } from 'vue'

export type Theme = 'light' | 'dark'

const STORAGE_KEY = 'blog_theme'

function getInitialTheme(): Theme {
  const saved = localStorage.getItem(STORAGE_KEY)
  if (saved === 'light' || saved === 'dark') return saved
  return window.matchMedia('(prefers-color-scheme: dark)').matches ? 'dark' : 'light'
}

const theme = ref<Theme>(getInitialTheme())

function apply(t: Theme) {
  theme.value = t
  document.documentElement.classList.toggle('dark', t === 'dark')
  localStorage.setItem(STORAGE_KEY, t)
}

/** 初始化（main.ts 或根组件调用一次） */
export function initTheme() {
  apply(theme.value)
}

/** 切换并返回新主题 */
export function toggleTheme(): Theme {
  apply(theme.value === 'dark' ? 'light' : 'dark')
  return theme.value
}

export function useTheme() {
  return { theme, toggleTheme }
}
