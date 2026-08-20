<script setup lang="ts">
import { formatDate } from '../../utils/format'
import type { ArticleDetail } from '../../types/blog'

defineProps<{ article: ArticleDetail; html?: string }>()
</script>

<template>
  <article class="card minimal-card">
    <h1 class="article-title minimal-title">{{ article.title }}</h1>
    <div class="minimal-meta">{{ formatDate(article.createdAt) }}</div>

    <div v-if="html" class="article-content" v-html="html"></div>
    <div v-else class="empty-tip" style="padding: 32px 0">
      暂无正文
    </div>
  </article>
</template>

<style scoped>
.minimal-card {
  padding-top: 32px;
}
.minimal-title {
  font-size: 24px;
  font-weight: 700;
  letter-spacing: 0.5px;
}
.minimal-meta {
  margin-top: 8px;
  font-size: 12px;
  color: var(--color-text-light);
}
</style>
