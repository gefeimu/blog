<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import type { ArticleSummary, Tag } from '../types/blog'

/**
 * ArticleSummary 本身不含 cover/ext（详情才有），
 * 但为了文章卡片的视觉效果，列表场景依然尝试从外部属性兜底取（更稳）。
 * 没有时显示 emoji/渐变占位。
 */
type CoverCapable = ArticleSummary & {
  cover?: string
  ext?: { cover?: string }
}

const props = defineProps<{
  article: ArticleSummary
  /** 标签名 -> 标签完整信息的映射，用于标签点击跳转 */
  tagMap?: Map<string, Tag>
}>()
const router = useRouter()

const coverUrl = computed(() => {
  const a = props.article as CoverCapable
  return a.cover || a.ext?.cover || ''
})

// 列表无封面时的 fallback：渐变色块 + emoji
const fallbackEmoji = '📝'

// 格式化日期：May 30, 2024
const formattedDate = computed(() => {
  const d = new Date(props.article.createdAt)
  return d.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  })
})

function onClickTag(tagName: string) {
  // 通过 tagMap 找到对应标签 id，跳转到 /tag/:id
  const tag = props.tagMap?.get(tagName)
  if (tag) {
    router.push(`/tag/${tag.id}`)
  }
}
</script>

<template>
  <li class="list-item">
    <article class="post-card">
      <!-- 顶部行：日期 + Read more -->
      <div class="post-meta-row">
        <span class="post-date">{{ formattedDate }}</span>
        <a
          class="read-more"
          :href="`/article/${article.id}`"
          @click.stop.prevent="router.push(`/article/${article.id}`)"
        >
          Read more →
        </a>
      </div>

      <!-- 标题 -->
      <h2 class="post-title">
        <a
          :href="`/article/${article.id}`"
          @click.stop.prevent="router.push(`/article/${article.id}`)"
        >
          {{ article.title }}
        </a>
      </h2>

      <!-- 正文预览（摘要） -->
      <p class="post-preview">
        {{ article.summary || '（暂无摘要）' }}
      </p>

      <!-- 底部标签：可点击的 pill -->
      <div v-if="article.tags?.length" class="post-tags">
        <span
          v-for="t in article.tags"
          :key="t"
          class="post-tag-pill"
          :class="{ 'is-clickable': tagMap?.has(t) }"
          @click="onClickTag(t)"
        >
          {{ t }}
        </span>
      </div>
    </article>
  </li>
</template>

<style scoped>
.list-item {
  padding: 32px 0;
  border-bottom: 1px solid var(--color-border);
}

.post-card {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* 顶部元信息行：日期 + Read more */
.post-meta-row {
  display: flex;
  align-items: center;
  justify-content: space-between;
}
.post-date {
  font-size: 13px;
  color: var(--color-text-light);
  white-space: nowrap;
}

/* 标题 */
.post-title {
  font-size: 26px;
  font-weight: 700;
  line-height: 1.3;
  letter-spacing: -0.01em;
  margin: 0;
}
.post-title a {
  color: var(--color-text);
  transition: color 0.2s;
}
.post-title a:hover {
  color: var(--color-primary);
}

/* 正文预览 */
.post-preview {
  font-size: 15px;
  color: var(--color-text-light);
  line-height: 1.7;
  margin: 0;
  /* 限制最多显示 3 行，超出截断 */
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 底部标签 pill */
.post-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 4px;
}
.post-tag-pill {
  display: inline-block;
  padding: 3px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
  background-color: rgba(0, 0, 0, 0.05);
  color: var(--color-text-light);
  transition: background-color 0.15s, color 0.15s;
}
html.dark .post-tag-pill {
  background-color: rgba(255, 255, 255, 0.08);
}
.post-tag-pill.is-clickable {
  cursor: pointer;
}
.post-tag-pill.is-clickable:hover {
  background-color: var(--color-primary);
  color: #fff;
}

.read-more {
  display: inline-flex;
  align-items: center;
  font-size: 13px;
  font-weight: 500;
  color: var(--color-text-light);
  white-space: nowrap;
  transition: color 0.15s;
}
.read-more:hover {
  color: var(--color-primary);
}

/* 移动端 */
@media (max-width: 768px) {
  .list-item {
    padding: 24px 0;
  }
  .post-title {
    font-size: 20px;
  }
  .post-preview {
    font-size: 14px;
    -webkit-line-clamp: 2;
  }
}
</style>
