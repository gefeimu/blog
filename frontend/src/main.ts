import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './style.css'
// 可插拔字体层（@font-face + 字体族 CSS 变量 + 主题切换）
import './fonts.css'
// Element Plus 暗色变量（html.dark 下生效，配合 useTheme 切换）
import 'element-plus/theme-chalk/dark/css-vars.css'
import { initTheme } from './composables/useTheme'

// 先应用主题再挂载，避免首屏闪烁
initTheme()

// Element Plus 已改为按需引入（unplugin-auto-import + unplugin-vue-components），
// 组件、API、指令、样式均自动按需加载；中文本地化在 App.vue 用 el-config-provider 全局配置。
createApp(App)
  .use(router)
  .mount('#app')
