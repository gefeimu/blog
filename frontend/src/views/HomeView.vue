<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { getArticles } from '../api/article'
import type { ArticleSummary } from '../types/blog'
import ArticleCard from '../components/ArticleCard.vue'

const articles = ref<ArticleSummary[]>([])
const loading = ref(true)

// 首页只展示最近 5 篇（参考 Tambouille 列表截断节奏）
const PREVIEW_SIZE = 5

onMounted(async () => {
  try {
    const data = await getArticles({
      page: 1,
      size: PREVIEW_SIZE,
      status: 1,
    })
    articles.value = data.list
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <section>
    <!-- Hero 区：保留个人风格 + Tambouille 的标题样式 -->
    <div class="hero-block">
      <h1 class="hero-title">Hi，我是歌斐木</h1>
      <p class="hero-sig">
        May your coffee be strong and your bugs be few.
      </p>
    </div>

    <!-- 最新文章列表 -->
    <ul v-if="articles.length" class="list-divider">
      <ArticleCard
        v-for="a in articles"
        :key="a.id"
        :article="a"
      />
    </ul>

    <div v-else-if="loading" class="loading">加载中...</div>
    <div v-else class="empty-tip">还没有文章</div>

    <!-- 右下角"查看全部 →" -->
    <div v-if="articles.length" class="list-footer">
      <router-link to="/articles" class="read-more">
        查看全部 →
      </router-link>
    </div>
  </section>
</template>
