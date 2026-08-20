<script setup lang="ts">
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getArticles } from '../api/article'
import { getTags } from '../api/tag'
import type { ArticleSummary, Tag } from '../types/blog'
import ArticleCard from '../components/ArticleCard.vue'

const route = useRoute()
const router = useRouter()
const articles = ref<ArticleSummary[]>([])
const tags = ref<Tag[]>([])
const total = ref(0)
const loading = ref(true)
const page = ref(1)
const size = 10
const hasMore = ref(true)

// 搜索关键字（防抖后回传 API）
const keyword = ref('')
const keywordDebounced = ref('')
let kwTimer: number | undefined

// 路由参数 tagId：/tag/:id 时按标签筛选，/articles 时为 undefined（全部）
const tagId = computed(() =>
  route.params.id ? Number(route.params.id) : undefined
)
const currentTag = computed(() => tags.value.find((t) => t.id === tagId.value))

// 顶部页标题：未筛选标签时显示"所有文章"
const pageHeading = computed(() => (currentTag.value ? `标签：${currentTag.value.name}` : '所有文章'))

async function load(append = false) {
  loading.value = true
  try {
    const data = await getArticles({
      page: page.value,
      size,
      status: 1,
      tagId: tagId.value,
      keyword: keywordDebounced.value || undefined,
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

// 输入防抖：300ms 后才请求
function onKeywordInput() {
  if (kwTimer) window.clearTimeout(kwTimer)
  kwTimer = window.setTimeout(() => {
    keywordDebounced.value = keyword.value.trim()
  }, 300)
}

function clearKeyword() {
  keyword.value = ''
  keywordDebounced.value = ''
}

function pickTag(id?: number) {
  router.push(id ? `/tag/${id}` : '/articles')
}

// 切换标签 / 关键字变化时重置分页并重新加载
watch(
  () => [route.params.id, keywordDebounced.value],
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
    <!-- 页头：参照 Tambouille，左侧大标题 + 数量 -->
    <header class="list-page-header">
      <div class="list-page-title">
        <h1 class="list-page-heading">{{ pageHeading }}</h1>
        <span class="list-page-count">（{{ total }}）</span>
      </div>
    </header>

    <!-- 搜索框 + 标签过滤行 -->
    <div class="list-filters">
      <div class="search-bar">
        <input
          v-model="keyword"
          type="text"
          class="search-input"
          placeholder="搜索文章"
          @input="onKeywordInput"
          @keyup.enter="onKeywordInput"
        />
        <button v-if="keyword" class="search-clear" type="button" @click="clearKeyword" aria-label="清空">
          ×
        </button>
        <svg class="search-icon" viewBox="0 0 24 24" aria-hidden="true">
          <circle cx="11" cy="11" r="7" fill="none" stroke="currentColor" stroke-width="2" />
          <path d="M20 20l-3.5-3.5" stroke="currentColor" stroke-width="2" stroke-linecap="round" />
        </svg>
      </div>

      <div v-if="tags.length" class="tag-filter">
        <button
          type="button"
          class="tag-filter-item"
          :class="{ 'is-active': !tagId }"
          @click="pickTag(undefined)"
        >
          全部
        </button>
        <button
          v-for="t in tags"
          :key="t.id"
          type="button"
          class="tag-filter-item"
          :class="{ 'is-active': tagId === t.id }"
          @click="pickTag(t.id)"
        >
          {{ t.name }}<span class="tag-filter-count">{{ t.count ?? 0 }}</span>
        </button>
      </div>
    </div>

    <!-- 文章列表 -->
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
.list-page-header {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  padding: 48px 0 20px;
}
.list-page-title {
  display: inline-flex;
  align-items: baseline;
  gap: 12px;
}
.list-page-heading {
  font-size: 60px;
  font-weight: 800;
  line-height: 1.15;
  letter-spacing: -0.025em;
  color: var(--color-text);
  margin: 0;
}
.list-page-count {
  color: var(--color-text-light);
  font-size: 18px;
  font-weight: 400;
}

.list-filters {
  padding-bottom: 20px;
  border-bottom: 1px solid var(--color-border);
}

/* 搜索框（仿 Tambouille 圆角描边 + 右侧放大镜） */
.search-bar {
  position: relative;
  margin: 8px 0 20px;
}
.search-input {
  width: 100%;
  height: 44px;
  padding: 0 40px 0 16px;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  background: transparent;
  color: var(--color-text);
  font-size: 15px;
  font-family: inherit;
  transition: border-color 0.15s, box-shadow 0.15s;
}
.search-input:focus {
  outline: none;
  border-color: var(--color-primary);
  box-shadow: 0 0 0 3px rgba(59, 130, 246, 0.12);
}
.search-icon {
  position: absolute;
  right: 14px;
  top: 50%;
  transform: translateY(-50%);
  width: 18px;
  height: 18px;
  color: var(--color-text-light);
  pointer-events: none;
}
.search-clear {
  position: absolute;
  right: 40px;
  top: 50%;
  transform: translateY(-50%);
  width: 20px;
  height: 20px;
  border: none;
  background: transparent;
  color: var(--color-text-light);
  font-size: 20px;
  line-height: 1;
  cursor: pointer;
  padding: 0;
}
.search-clear:hover {
  color: var(--color-text);
}

/* 标题下的标签过滤（蓝色无背景，可点击） */
.tag-filter {
  display: flex;
  flex-wrap: wrap;
  gap: 18px;
  margin-bottom: 8px;
}
.tag-filter-item {
  display: inline-flex;
  align-items: baseline;
  gap: 4px;
  border: none;
  background: transparent;
  padding: 4px 0;
  font-size: 13px;
  font-weight: 500;
  color: var(--color-primary);
  text-transform: uppercase;
  letter-spacing: 0.04em;
  cursor: pointer;
  font-family: inherit;
  transition: color 0.15s, transform 0.15s;
}
.tag-filter-item:hover {
  color: var(--color-primary-hover);
}
.tag-filter-item.is-active {
  color: var(--color-text);
  border-bottom: 2px solid var(--color-text);
}
.tag-filter-count {
  font-size: 11px;
  font-weight: 400;
  color: var(--color-text-light);
  text-transform: none;
  letter-spacing: 0;
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

/* 移动端 */
@media (max-width: 768px) {
  .list-page-header {
    padding: 32px 0 12px;
  }
  .list-page-heading {
    font-size: 36px;
  }
  .search-input {
    height: 40px;
    font-size: 14px;
  }
  .tag-filter {
    gap: 12px;
  }
}
</style>