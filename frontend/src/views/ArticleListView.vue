<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute } from 'vue-router'
import { getArticles, getTags } from '../api/article'
import type { ArticleSummary, Tag } from '../types/blog'
import ArticleCard from '../components/ArticleCard.vue'

const route = useRoute()
const articles = ref<ArticleSummary[]>([])
const tags = ref<Tag[]>([])
const total = ref(0)
const loading = ref(true)
const page = ref(1)
const size = 10
const hasMore = ref(true)

// 路由参数 tagId：/tag/:id 时按标签筛选，/articles 时为 undefined（全部）
const tagId = computed(() =>
  route.params.id ? Number(route.params.id) : undefined
)
const currentTag = computed(() => tags.value.find((t) => t.id === tagId.value))

async function load(append = false) {
  loading.value = true
  try {
    const data = await getArticles({
      page: page.value,
      size,
      status: 1,
      tagId: tagId.value,
    })
    articles.value = append ? [...articles.value, ...data.list] : data.list
    total.value = data.total
    hasMore.value = articles.value.length < data.total
  } catch (e) {
    console.error(e)
  } finally {
    loading.value = false
  }
}

function loadMore() {
  page.value += 1
  load(true)
}

// 切换标签路由时重置分页并重新加载
watch(
  () => route.params.id,
  () => {
    page.value = 1
    load(false)
  }
)

onMounted(async () => {
  load()
  try {
    tags.value = await getTags()
  } catch (e) {
    console.error(e)
  }
})
</script>

<template>
  <section>
    <div class="page-header">
      <div>
        <h1 class="page-title">
          {{ currentTag ? `标签：${currentTag.name}` : '博客' }}
          <span class="page-count">（{{ total }}）</span>
        </h1>
      </div>
      <router-link v-if="currentTag" to="/articles" class="read-more">
        ← 查看全部
      </router-link>
    </div>

    <ul v-if="articles.length" class="list-divider">
      <ArticleCard v-for="a in articles" :key="a.id" :article="a" />
    </ul>

    <div v-if="loading" class="loading">加载中...</div>
    <div v-else-if="!articles.length" class="empty-tip">
      {{ currentTag ? `「${currentTag.name}」下还没有文章` : '还没有文章' }}
    </div>
    <div v-else-if="hasMore" class="load-more" @click="loadMore">
      加载更多
    </div>
  </section>
</template>

<style scoped>
.page-header {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  padding: 36px 0 12px;
  border-bottom: 1px solid var(--color-border);
  margin-bottom: 0;
}
.page-count {
  color: var(--color-text-light);
  font-weight: 400;
  font-size: 14px;
}
.load-more {
  text-align: center;
  color: var(--color-text-light);
  padding: 32px 0;
  font-size: 14px;
  cursor: pointer;
}
.load-more:hover {
  color: var(--color-primary);
}
</style>
