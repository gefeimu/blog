<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { login } from '../../api/auth'
import { getErrorMessage } from '../../api/request'

const router = useRouter()
const form = ref({ username: '', password: '' })
const loading = ref(false)

const onSubmit = async () => {
  if (!form.value.username || !form.value.password) {
    ElMessage.warning('请输入用户名和密码')
    return
  }
  loading.value = true
  try {
    const res = await login(form.value.username, form.value.password)
    localStorage.setItem('blog_token', res.token)
    localStorage.setItem('blog_nickname', res.nickname || res.username || '')
    ElMessage.success('登录成功')
    router.push('/admin/articles')
  } catch (e) {
    ElMessage.error(getErrorMessage(e, '登录失败'))
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="admin-login">
    <el-card class="login-card">
      <template #header>
        <div class="login-title">博客后台管理</div>
      </template>
      <el-form label-position="top" @submit.prevent="onSubmit">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="admin" autocomplete="username" />
        </el-form-item>
        <el-form-item label="密码">
          <el-input
            v-model="form.password"
            type="password"
            show-password
            placeholder="请输入密码"
            autocomplete="current-password"
            @keyup.enter="onSubmit"
          />
        </el-form-item>
        <el-button type="primary" class="login-btn" :loading="loading" @click="onSubmit">
          登 录
        </el-button>
      </el-form>
    </el-card>
    <router-link to="/" class="back-home">← 返回前台</router-link>
  </div>
</template>

<style scoped>
.admin-login {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e9f0 100%);
}
:global(html.dark) .admin-login {
  background: linear-gradient(135deg, #161616 0%, #1c2330 100%);
}
.login-card {
  width: 360px;
}
.login-title {
  text-align: center;
  font-size: 18px;
  font-weight: 600;
}
.login-btn {
  width: 100%;
}
.back-home {
  margin-top: 16px;
  color: #909399;
  font-size: 13px;
  text-decoration: none;
}
.back-home:hover {
  color: #409eff;
}
</style>
