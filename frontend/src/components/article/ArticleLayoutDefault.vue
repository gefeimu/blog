<script setup lang="ts">
import { computed } from 'vue'
import { formatDate, calcReadingTime } from '../../utils/format'
import type { ArticleDetail } from '../../types/blog'

const props = defineProps<{ article: ArticleDetail; html?: string }>()

const readingTime = computed(() => calcReadingTime(props.article.content))
</script>

<template>
  <article class="card">
    <h1 class="article-title">{{ article.title }}</h1>
    <div class="article-meta">
      <span>{{ article.categoryName }}</span>
      <span>{{ formatDate(article.createdAt) }}</span>
      <span>浏览 {{ article.viewCount ?? 0 }}</span>
      <span>约 {{ readingTime }} 分钟</span>
      <span v-if="article.tags?.length">
        <span v-for="t in article.tags" :key="t" class="tag">{{ t }}</span>
      </span>
    </div>

    <div v-if="html" class="article-content" v-html="html"></div>
    <div v-else class="empty-tip" style="padding: 32px 0">
      暂无正文
    </div>
  </article>
</template>
