<script setup lang="ts">
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import { Sunny, Moon } from '@element-plus/icons-vue'
import { useTheme } from './composables/useTheme'

const { theme, toggleTheme } = useTheme()

// 顶部水平导航（参考 Tambouille 顶部：左 logo + 右菜单 + 月亮）
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
      <!-- 顶部水平导航（参考 Tambouille） -->
      <header class="app-topbar">
        <router-link to="/" class="brand" title="回到首页">
          <img src="/avatar.png" alt="歌斐木" class="brand-avatar" />
          <span>歌斐木</span>
        </router-link>

        <nav class="nav">
          <router-link
            v-for="item in navItems"
            :key="item.to"
            :to="item.to"
            class="nav-item"
          >
            {{ item.label }}
          </router-link>

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
        </nav>
      </header>

      <main class="app-main">
        <router-view />
      </main>
    </div>
  </el-config-provider>
</template>

<style scoped>
/* 主题切换按钮（顶部最右） */
.theme-toggle {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 50%;
  background: transparent;
  color: #f59e0b;
  cursor: pointer;
  transition: color 0.2s, transform 0.2s;
}
.theme-toggle:hover {
  color: var(--color-primary);
  transform: scale(1.08);
}
</style>
