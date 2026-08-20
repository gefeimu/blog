<script setup lang="ts">
import { useRouter } from 'vue-router'
import { formatDate } from '../utils/format'
import type { ArticleSummary } from '../types/blog'

defineProps<{ article: ArticleSummary }>()

const router = useRouter()
</script>

<template>
  <article class="card" @click="router.push(`/article/${article.id}`)">
    <h2 class="article-title">{{ article.title }}</h2>
    <p class="article-summary">{{ article.summary || '（暂无摘要）' }}</p>
    <div class="article-meta">
      <span>{{ article.categoryName }}</span>
      <span>{{ formatDate(article.createdAt) }}</span>
      <span>浏览 {{ article.viewCount ?? 0 }}</span>
      <span v-if="article.tags?.length">
        <span v-for="t in article.tags" :key="t" class="tag">{{ t }}</span>
      </span>
    </div>
  </article>
</template>

<style scoped>
.card {
  cursor: pointer;
  transition: box-shadow 0.2s;
}

.card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
}
</style>
