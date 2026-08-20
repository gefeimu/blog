<script setup lang="ts">
import { ref, onMounted } from 'vue'
import zhCn from 'element-plus/es/locale/lang/zh-cn'
import { Sunny, Moon } from '@element-plus/icons-vue'
import { getCategories } from './api/category'
import { useTheme } from './composables/useTheme'
import type { Category } from './types/blog'

const categories = ref<Category[]>([])
const { theme, toggleTheme } = useTheme()

onMounted(async () => {
  try {
    categories.value = await getCategories()
  } catch (e) {
    // 分类拉取失败不阻塞页面
    console.error(e)
  }
})
</script>

<template>
  <!-- 全局 Element Plus 中文本地化（按需引入模式下替代 app.use(ElementPlus, { locale })） -->
  <el-config-provider :locale="zhCn">
    <header class="site-header">
      <div class="container">
        <router-link to="/" class="site-title">白工的博客</router-link>
        <nav class="site-nav">
          <router-link to="/">首页</router-link>
          <router-link
            v-for="c in categories"
            :key="c.id"
            :to="`/category/${c.id}`"
          >
            {{ c.name }}
          </router-link>
        </nav>
        <button
          class="theme-toggle"
          :title="theme === 'dark' ? '切换到亮色模式' : '切换到暗色模式'"
          @click="toggleTheme"
        >
          <el-icon :size="16">
            <Sunny v-if="theme === 'dark'" />
            <Moon v-else />
          </el-icon>
        </button>
      </div>
    </header>

    <main class="container">
      <router-view />
    </main>
  </el-config-provider>
</template>

<style scoped>
/* 主题切换按钮：亮色显示月亮（点暗），暗色显示太阳（点亮） */
.theme-toggle {
  margin-left: auto;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 30px;
  height: 30px;
  border: 1px solid var(--color-border);
  border-radius: 50%;
  background: transparent;
  color: var(--color-text-light);
  cursor: pointer;
  transition: color 0.2s, border-color 0.2s;
}
.theme-toggle:hover {
  color: var(--color-primary);
  border-color: var(--color-primary);
}
</style>
