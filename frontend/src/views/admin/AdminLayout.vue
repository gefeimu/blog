<script setup>
import { useRoute, useRouter } from 'vue-router'
import { Document, FolderOpened, EditPen, SwitchButton } from '@element-plus/icons-vue'

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
    <el-aside width="200px" class="admin-aside">
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

    <el-container>
      <el-header class="admin-header">
        <span class="header-nickname">{{ nickname }}</span>
        <el-button link type="danger" :icon="SwitchButton" @click="logout">退出登录</el-button>
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
}
.aside-title {
  height: 60px;
  line-height: 60px;
  text-align: center;
  color: #fff;
  font-size: 16px;
  font-weight: 600;
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
.admin-header {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 12px;
  background: #fff;
  border-bottom: 1px solid #e4e7ed;
}
.header-nickname {
  color: #606266;
  font-size: 14px;
}
.admin-main {
  background: #f5f7fa;
}
</style>
