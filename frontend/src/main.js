import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './style.css'

// Element Plus 已改为按需引入（unplugin-auto-import + unplugin-vue-components），
// 组件、API、指令、样式均自动按需加载；中文本地化在 App.vue 用 el-config-provider 全局配置。
createApp(App)
  .use(router)
  .mount('#app')
