<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getTags } from '../api/tag'
import type { Tag } from '../types/blog'

const router = useRouter()
const tags = ref<Tag[]>([])
const loading = ref(true)

onMounted(async () => {
  try {
    tags.value = await getTags()
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
})

function goTag(t: Tag) {
  router.push(`/tag/${t.id}`)
}
</script>

<template>
  <div class="tags-page">
    <div v-if="loading" class="loading">加载中...</div>

    <div v-else-if="!tags.length" class="empty-tip">还没有标签</div>

    <div v-else class="tags-grid">
      <h1 class="tags-title">Tags</h1>
      <div class="tag-list">
        <button
          v-for="t in tags"
          :key="t.id"
          class="tag-link"
          :class="{ disabled: !t.count }"
          :disabled="!t.count"
          @click="goTag(t)"
        >
          {{ (t.name || '').toUpperCase() }}
          <span class="tag-num">({{ t.count ?? 0 }})</span>
        </button>
      </div>
    </div>
  </div>
</template>

<style scoped>
/* 参照 Tambouille /tags 布局：左大字 Tags + 右上方对齐的标签云 */
.tags-page {
  padding: 80px 0;
}
.tags-grid {
  display: grid;
  grid-template-columns: 200px 1fr;
  gap: 48px;
  align-items: center;
  min-height: 220px;
}
.tags-title {
  font-size: 60px;
  font-weight: 800;
  line-height: 1.1;
  letter-spacing: -0.025em;
  color: var(--color-text);
  text-align: right;
  margin: 0;
}
.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 18px 32px;
  border-left: 1px solid var(--color-border);
  padding: 4px 0 4px 48px;
}
.tag-link {
  display: inline-flex;
  /* 中英文字形高度差异：CJK 字形填满 em 方块、Latin caps 只占 cap-height，
     baseline 对齐时 Chinese 字顶会高出 English 字顶 → 视觉上"不对齐"。
     改为 center + 固定行高，让两边在视觉中心对齐 */
  align-items: center;
  line-height: 1.2;
  gap: 6px;
  background: transparent;
  border: none;
  padding: 2px 0;
  font-size: 18px;
  font-weight: 600;
  color: var(--color-primary);
  text-transform: uppercase;
  letter-spacing: 0.04em;
  cursor: pointer;
  transition: color 0.2s;
}
.tag-link:hover:not(.disabled) {
  color: var(--color-primary-hover);
}
.tag-link.disabled {
  opacity: 0.4;
  cursor: default;
}
.tag-num {
  color: var(--color-text-light);
  font-weight: 500;
  font-size: 14px;
}

@media (max-width: 768px) {
  .tags-page {
    padding: 36px 0;
  }
  .tags-grid {
    grid-template-columns: 1fr;
    gap: 24px;
    min-height: auto;
  }
  .tags-title {
    font-size: 40px;
    text-align: left;
  }
  .tag-list {
    border-left: none;
    padding-left: 0;
    gap: 12px 20px;
  }
}
</style>
