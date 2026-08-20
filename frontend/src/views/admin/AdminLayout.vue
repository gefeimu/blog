<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'
import { Document, CollectionTag, EditPen, SwitchButton, ArrowLeftBold, Sunny, Moon } from '@element-plus/icons-vue'
import { useTheme } from '../../composables/useTheme'

const route = useRoute()
const router = useRouter()
const { theme, toggleTheme } = useTheme()

const nickname = localStorage.getItem('blog_nickname') || 'admin'

const logout = () => {
  localStorage.removeItem('blog_token')
  localStorage.removeItem('blog_nickname')
  router.push('/admin/login')
}
</script>

<template>
  <el-container class="admin-layout">
    <el-aside width="220px" class="admin-aside">
      <div class="aside-title">博客后台</div>
      <el-menu :default-active="route.path" router class="aside-menu">
        <el-menu-item index="/admin/articles">
          <el-icon><Document /></el-icon>
          <span>文章管理</span>
        </el-menu-item>
        <el-menu-item index="/admin/articles/new">
          <el-icon><EditPen /></el-icon>
          <span>写文章</span>
        </el-menu-item>
        <el-menu-item index="/admin/tags">
          <el-icon><CollectionTag /></el-icon>
          <span>标签管理</span>
        </el-menu-item>
        <!-- 分类管理入口已隐藏（前台已移除分类体系），路由保留可直达 /admin/categories 清理旧数据 -->
      </el-menu>
    </el-aside>

    <el-container class="admin-right">
      <el-header class="admin-header">
        <router-link to="/" class="back-front">
          <el-icon><ArrowLeftBold /></el-icon>
          <span>返回前台</span>
        </router-link>
        <div class="header-actions">
          <el-button
            circle
            class="theme-btn"
            :title="theme === 'dark' ? '切换到亮色模式' : '切换到暗色模式'"
            @click="toggleTheme"
          >
            <el-icon>
              <Sunny v-if="theme === 'dark'" />
              <Moon v-else />
            </el-icon>
          </el-button>
          <span class="header-nickname">{{ nickname }}</span>
          <el-button link type="danger" :icon="SwitchButton" @click="logout">退出登录</el-button>
        </div>
      </el-header>
      <el-main class="admin-main">
        <router-view />
      </el-main>
    </el-container>
  </el-container>
</template>

<style scoped>
.admin-layout {
  height: 100vh;
}
.admin-aside {
  background: #001529;
  box-shadow: 2px 0 8px rgba(0, 0, 0, 0.15);
  z-index: 1;
}
.aside-title {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
  letter-spacing: 1px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}
.aside-menu {
  border-right: none;
  background: transparent;
}
.aside-menu :deep(.el-menu-item) {
  color: rgba(255, 255, 255, 0.75);
}
.aside-menu :deep(.el-menu-item:hover) {
  background: rgba(255, 255, 255, 0.08);
}
.aside-menu :deep(.el-menu-item.is-active) {
  background: #409eff;
  color: #fff;
}
.admin-right {
  min-width: 0;
}
.admin-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  background: var(--el-bg-color);
  border-bottom: 1px solid var(--el-border-color-light);
  padding: 0 20px;
}
.back-front {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: var(--el-text-color-regular);
  font-size: 14px;
}
.back-front:hover {
  color: #409eff;
}
.header-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}
.header-nickname {
  color: var(--el-text-color-regular);
  font-size: 14px;
}
.admin-main {
  --el-main-padding: 0;
  background: var(--el-bg-color-page);
  overflow: auto;
}
</style>
