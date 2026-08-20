<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import type { ArticleSummary } from '../types/blog'

/**
 * ArticleSummary 本身不含 cover/ext（详情才有），
 * 但为了文章卡片的视觉效果，列表场景依然尝试从外部属性兜底取（更稳）。
 * 没有时显示 emoji/渐变占位。
 */
type CoverCapable = ArticleSummary & {
  cover?: string
  ext?: { cover?: string }
}

const props = defineProps<{ article: ArticleSummary }>()
const router = useRouter()

const coverUrl = computed(() => {
  const a = props.article as CoverCapable
  return a.cover || a.ext?.cover || ''
})

// 列表无日期文字标签时也能美化 fallback：渐变色块 + emoji
const fallbackEmoji = '📝'
</script>

<template>
  <li class="list-item">
    <article
      class="post-grid"
      @click="router.push(`/article/${article.id}`)"
    >
      <!-- 左侧封面：有图则图、无图则 fallback -->
      <div class="post-cover-wrap">
        <img
          v-if="coverUrl"
          :src="coverUrl"
          :alt="article.title"
          class="post-cover"
          loading="lazy"
          @error="(e) => (e.target as HTMLImageElement).style.display = 'none'"
        />
        <div v-else class="post-cover-fallback">{{ fallbackEmoji }}</div>
      </div>

      <!-- 右侧文案 -->
      <div class="post-body">
        <h2 class="post-title">
          <a :href="`/article/${article.id}`" @click.stop>
            {{ article.title }}
          </a>
        </h2>

        <div v-if="article.tags?.length" class="post-tags">
          <span
            v-for="t in article.tags"
            :key="t"
            class="post-tag"
          >
            {{ t }}
          </span>
        </div>

        <p class="post-summary">
          {{ article.summary || '（暂无摘要）' }}
        </p>

        <a
          class="read-more"
          :href="`/article/${article.id}`"
          @click.stop
        >
          查看详情 →
        </a>
      </div>
    </article>
  </li>
</template>

<style scoped>
.post-grid {
  cursor: pointer;
  transition: transform 0.2s;
}
.post-grid:hover {
  transform: translateX(2px);
}
.post-cover-wrap {
  position: relative;
  border-radius: 6px;
  overflow: hidden;
}
</style>
