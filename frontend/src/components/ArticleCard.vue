<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import type { ArticleSummary, Tag } from '../types/blog'

const props = defineProps<{
  article: ArticleSummary
  /** 标签名 -> 标签完整信息的映射，用于标签点击跳转 */
  tagMap?: Map<string, Tag>
}>()
const router = useRouter()

// 封面图：从 ext.cover 取（列表接口已返回 ext）
const coverUrl = computed(() => props.article.ext?.cover || '')

// 格式化日期：Aug 20, 2026
const formattedDate = computed(() => {
  const d = new Date(props.article.createdAt)
  return d.toLocaleDateString('en-US', {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  })
})

function onClickTag(tagName: string) {
  const tag = props.tagMap?.get(tagName)
  if (tag) {
    router.push(`/tag/${tag.id}`)
  }
}
</script>

<template>
  <li class="list-item">
    <article class="post-card" @click="router.push(`/article/${article.id}`)">
      <!-- 左侧日期 + 封面 -->
      <div class="post-date-col">
        <span class="post-date">{{ formattedDate }}</span>
        <div class="post-cover-wrap">
          <img
            v-if="coverUrl"
            :src="coverUrl"
            :alt="article.title"
            class="post-cover"
            loading="lazy"
            @error="(e) => (e.target as HTMLImageElement).style.display = 'none'"
          />
          <div v-else class="post-cover-fallback">📝</div>
        </div>
      </div>

      <!-- 中间主内容：标题 + 摘要 + 标签 -->
      <div class="post-content">
        <h2 class="post-title">
          <a
            :href="`/article/${article.id}`"
            @click.stop.prevent="router.push(`/article/${article.id}`)"
          >
            {{ article.title }}
          </a>
        </h2>

        <p class="post-preview">
          {{ article.summary || '（暂无摘要）' }}
        </p>

        <div v-if="article.tags?.length" class="post-tags">
          <span
            v-for="t in article.tags"
            :key="t"
            class="post-tag-pill"
            :class="{ 'is-clickable': tagMap?.has(t) }"
            @click.stop="onClickTag(t)"
          >
            {{ t }}
          </span>
        </div>
      </div>

      <!-- 右侧 Read more -->
      <a
        class="read-more"
        :href="`/article/${article.id}`"
        @click.stop.prevent="router.push(`/article/${article.id}`)"
      >
        Read more →
      </a>
    </article>
  </li>
</template>

<style scoped>
.list-item {
  padding: 28px 0;
  border-bottom: 1px solid var(--color-border);
}

/* 横排三栏：日期+封面 | 主内容 | Read more */
.post-card {
  display: grid;
  grid-template-columns: 140px 1fr auto;
  gap: 24px;
  align-items: start;
  transition: transform 0.25s cubic-bezier(0.2, 0.8, 0.2, 1);
}
/* hover 时整体向左移动 */
.list-item:hover .post-card {
  transform: translateX(-6px);
}

/* 左侧日期 + 封面列 */
.post-date-col {
  flex-shrink: 0;
  padding-top: 2px;
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.post-date {
  font-size: 13px;
  color: var(--color-text-light);
  white-space: nowrap;
}
.post-cover-wrap {
  border-radius: 6px;
  overflow: hidden;
}
.post-cover {
  width: 140px;
  height: 88px;
  object-fit: cover;
  background: #e5e7eb;
  display: block;
}
html.dark .post-cover {
  background: #2a2a2a;
}
.post-cover-fallback {
  width: 140px;
  height: 88px;
  border-radius: 6px;
  background: linear-gradient(135deg, #eef2ff 0%, #f0f9ff 100%);
  display: inline-flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: #94a3b8;
}
html.dark .post-cover-fallback {
  background: linear-gradient(135deg, #1e293b 0%, #0f172a 100%);
}

/* 中间主内容区 */
.post-content {
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

/* 标题：不高亮 */
.post-title {
  font-size: 22px;
  font-weight: 700;
  line-height: 1.3;
  letter-spacing: -0.01em;
  margin: 0;
}
.post-title a {
  color: var(--color-text);
}
/* 标题 hover 不变色 */
.post-title a:hover {
  color: var(--color-text);
}

/* 正文预览 */
.post-preview {
  font-size: 15px;
  color: var(--color-text-light);
  line-height: 1.7;
  margin: 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

/* 底部标签 pill */
.post-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: auto;
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

/* 右侧 Read more */
.read-more {
  display: inline-flex;
  align-items: center;
  font-size: 13px;
  font-weight: 500;
  color: var(--color-text-light);
  white-space: nowrap;
  padding-top: 2px;
  transition: color 0.15s;
}
.read-more:hover {
  color: var(--color-primary);
}

/* 移动端 */
@media (max-width: 768px) {
  .list-item {
    padding: 20px 0;
  }
  .post-card {
    grid-template-columns: 1fr auto;
    gap: 12px 16px;
  }
  .post-date-col {
    grid-column: 1 / -1;
    padding-top: 0;
  }
  .post-content {
    gap: 6px;
  }
  .post-title {
    font-size: 18px;
  }
  .post-preview {
    font-size: 14px;
    -webkit-line-clamp: 2;
  }
}
</style>
