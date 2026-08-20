<script setup lang="ts">
import { useRouter } from 'vue-router'
import { formatDate, calcReadingTime } from '../utils/format'
import type { ArticleSummary } from '../types/blog'

defineProps<{ article: ArticleSummary }>()

const router = useRouter()
</script>

<template>
  <article class="card article-card" @click="router.push(`/article/${article.id}`)">
    <!-- 标签在上（纯展示，点击标签筛选见标签云页） -->
    <div v-if="article.tags?.length" class="card-tags">
      <span v-for="t in article.tags" :key="t" class="tag">{{ t }}</span>
    </div>

    <h2 class="article-title">{{ article.title }}</h2>

    <p class="article-summary">
      {{ article.summary || '（暂无摘要）' }}
    </p>

    <div class="article-meta">
      <span>{{ formatDate(article.createdAt) }}</span>
      <span>· 约 {{ calcReadingTime(article.summary) }} 分钟阅读</span>
      <span>· 浏览 {{ article.viewCount ?? 0 }}</span>
    </div>
  </article>
</template>

<style scoped>
.article-card {
  cursor: pointer;
  transition: box-shadow 0.2s, border-color 0.2s;
}
.article-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
  border-color: var(--color-primary);
}
.card-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
  margin-bottom: 8px;
}
</style>
