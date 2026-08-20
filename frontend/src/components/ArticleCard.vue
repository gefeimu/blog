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

// 列表无封面时的 fallback：渐变色块 + emoji
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

      <!-- 右侧文案：hover 时整列轻微右移（标题不高亮） -->
      <div class="post-body">
        <h2 class="post-title">
          <!-- SPA 路由跳转：阻止 a 默认整页刷新，交由 vue-router 接管 -->
          <a
            :href="`/article/${article.id}`"
            @click.stop.prevent="router.push(`/article/${article.id}`)"
          >
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
          @click.stop.prevent="router.push(`/article/${article.id}`)"
        >
          查看详情 →
        </a>
      </div>
    </article>
  </li>
</template>

<style scoped>
.post-cover-wrap {
  position: relative;
  border-radius: 6px;
  overflow: hidden;
}

/* 鼠标放在整张卡上：只有右侧文字列（post-body）轻微右移，
   标题保持原色不变色，封面不动 */
.post-body {
  min-width: 0;
  transition: transform 0.25s cubic-bezier(0.2, 0.8, 0.2, 1);
}
.list-item:hover .post-body {
  transform: translateX(6px);
}
</style>