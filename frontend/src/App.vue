<script setup lang="ts">
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import { Sunny, Moon } from '@element-plus/icons-vue'
import { useTheme } from './composables/useTheme'

const { theme, toggleTheme } = useTheme()

// 左侧固定导航（参考截图布局：左侧导航 + 主区，顶部右角昵称跳首页）
const navItems = [
  { to: '/articles', label: '博客' },
  { to: '/tags', label: '标签' },
  { to: '/projects', label: '项目' },
  { to: '/about', label: '关于' },
]
</script>

<template>
  <!-- 全局 Element Plus 中文本地化（按需引入模式下替代 app.use(ElementPlus, { locale })） -->
  <el-config-provider :locale="zhCn">
    <div class="app-layout">
      <!-- 左侧固定导航 -->
      <aside class="app-sidebar">
        <nav class="sidebar-nav">
          <router-link
            v-for="item in navItems"
            :key="item.to"
            :to="item.to"
            class="sidebar-nav-item"
          >
            {{ item.label }}
          </router-link>
        </nav>
        <button
          class="theme-toggle"
          :title="theme === 'dark' ? '切换到亮色模式' : '切换到暗色模式'"
          @click="toggleTheme"
        >
          <el-icon :size="18">
            <Sunny v-if="theme === 'dark'" />
            <Moon v-else />
          </el-icon>
        </button>
      </aside>

      <!-- 右侧主区：顶部右角昵称 + 内容 -->
      <div class="app-main">
        <header class="app-topbar">
          <router-link to="/" class="nickname" title="回到首页">歌斐木</router-link>
        </header>
        <router-view />
      </div>
    </div>
  </el-config-provider>
</template>

<style scoped>
/* 主题切换按钮（左侧导航底部） */
.theme-toggle {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: 1px solid var(--color-border);
  border-radius: 50%;
  background: transparent;
  color: var(--color-text-light);
  cursor: pointer;
  transition: color 0.2s, border-color 0.2s;
  margin-top: 24px;
}
.theme-toggle:hover {
  color: var(--color-primary);
  border-color: var(--color-primary);
}
</style>