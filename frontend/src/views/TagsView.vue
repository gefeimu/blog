<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { getTags } from '../api/article'
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
    <h1 class="page-title">标签</h1>
    <p class="page-subtitle">共 {{ tags.length }} 个标签，点击查看对应文章</p>

    <div v-if="loading" class="loading">加载中...</div>

    <div v-else-if="!tags.length" class="empty-tip">还没有标签</div>

    <div v-else class="tag-cloud">
      <button
        v-for="t in tags"
        :key="t.id"
        class="tag-chip"
        :class="{ disabled: !t.count }"
        :disabled="!t.count"
        @click="goTag(t)"
      >
        <span class="tag-name">{{ t.name }}</span>
        <span class="tag-count">{{ t.count ?? 0 }}</span>
      </button>
    </div>
  </div>
</template>

<style scoped>
.tags-page {
  padding: 24px 0 48px;
}
.page-subtitle {
  color: var(--color-text-light);
  font-size: 13px;
  margin-bottom: 24px;
}
.tag-cloud {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}
.tag-chip {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  border: 1px solid var(--color-border);
  border-radius: 16px;
  background: var(--color-card);
  color: var(--color-text);
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}
.tag-chip:hover:not(.disabled) {
  border-color: var(--color-primary);
  color: var(--color-primary);
}
.tag-chip.disabled {
  opacity: 0.5;
  cursor: default;
}
.tag-count {
  font-size: 12px;
  color: var(--color-text-light);
}
</style>
