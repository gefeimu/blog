<script setup>
import { ref, onMounted } from 'vue'
import { getCategories } from './api/category'

const categories = ref([])

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
    </div>
  </header>

  <main class="container">
    <router-view />
  </main>
</template>
