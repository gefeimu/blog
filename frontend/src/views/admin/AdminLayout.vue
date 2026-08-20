<script setup lang="ts">
import { useRoute, useRouter } from 'vue-router'
import { Document, FolderOpened, EditPen, SwitchButton, ArrowLeftBold } from '@element-plus/icons-vue'

const route = useRoute()
const router = useRouter()

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
        <el-menu-item index="/admin/categories">
          <el-icon><FolderOpened /></el-icon>
          <span>分类管理</span>
        </el-menu-item>
      </el-menu>
    </el-aside>

    <el-container class="admin-right">
      <el-header class="admin-header">
        <router-link to="/" class="back-front">
          <el-icon><ArrowLeftBold /></el-icon>
          <span>返回前台</span>
        </router-link>
        <div class="header-actions">
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
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
  padding: 0 20px;
}
.back-front {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  color: #606266;
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
  color: #606266;
  font-size: 14px;
}
.admin-main {
  --el-main-padding: 0;
  background: #f5f7fa;
  overflow: auto;
}
</style>
