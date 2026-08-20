<script setup lang="ts">
import { computed } from 'vue'
import { formatDate, calcReadingTime } from '../../utils/format'
import type { ArticleDetail } from '../../types/blog'

const props = defineProps<{ article: ArticleDetail; html?: string }>()

// 横幅封面图：ext.cover（扩展字段 JSON，编辑页可填）
const cover = computed(() => props.article?.ext?.cover || '')
const readingTime = computed(() => calcReadingTime(props.article.content))
</script>

<template>
  <article class="card banner-card">
    <div v-if="cover" class="banner-cover">
      <img :src="cover" :alt="article.title" />
    </div>
    <div class="banner-body">
      <h1 class="article-title banner-title">{{ article.title }}</h1>
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
    </div>
  </article>
</template>

<style scoped>
.banner-card {
  padding: 0;
  overflow: hidden;
}
.banner-cover {
  width: 100%;
  max-height: 360px;
  overflow: hidden;
  background: #f0f0f0;
}
.banner-cover img {
  width: 100%;
  height: 100%;
  max-height: 360px;
  object-fit: cover;
  display: block;
}
.banner-body {
  padding: 20px 22px;
}
.banner-title {
  font-size: 24px;
}
</style>
