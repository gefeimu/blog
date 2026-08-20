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

// 标签名 -> Tag 的映射，传递给 ArticleCard 用于标签点击跳转
const tagMap = computed(() => {
  const m = new Map<string, Tag>()
  for (const t of tags.value) {
    m.set(t.name, t)
  }
  return m
})

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
  <section class="articles-page">
    <!-- 页头：参照 Tambouille，左侧大标题 + 数量 -->
    <header class="list-page-header">
      <div class="list-page-title">
        <h1 class="list-page-heading">{{ pageHeading }}</h1>
        <span class="list-page-count">（{{ total }}）</span>
      </div>
      <!-- 搜索框移到右上角 -->
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
    </header>

    <!-- 主体：左侧标签边栏 + 右侧文章列表 -->
    <div class="articles-body">
      <!-- 左侧标签边栏 -->
      <aside class="topics-sidebar">
        <h3 class="topics-title">标签</h3>
        <nav class="topics-nav">
          <a
            class="topic-item"
            :class="{ 'is-active': !tagId }"
            @click="pickTag(undefined)"
          >
            <span class="topic-name">所有文章</span>
            <span class="topic-count">{{ total }}</span>
          </a>
          <a
            v-for="t in tags"
            :key="t.id"
            class="topic-item"
            :class="{ 'is-active': tagId === t.id }"
            @click="pickTag(t.id)"
          >
            <span class="topic-name">{{ t.name }}</span>
            <span class="topic-count">{{ t.count ?? 0 }}</span>
          </a>
        </nav>
      </aside>

    <!-- 右侧文章列表 -->
    <div class="articles-main">
        <p v-if="!keywordDebounced && !tagId" class="page-description">
          这里记录我感兴趣的话题，也许是技术、也许是生活、也许是随笔。
        </p>

        <ul v-if="articles.length" class="list-divider">
          <ArticleCard v-for="a in articles" :key="a.id" :article="a" :tag-map="tagMap" />
        </ul>

        <div v-if="loading" class="loading">加载中...</div>
        <div v-else-if="!articles.length" class="empty-tip">
          {{ currentTag ? `「${currentTag.name}」下还没有文章` : '还没有文章' }}
        </div>
        <div v-else-if="hasMore" class="load-more" @click="loadMore">
          加载更多
        </div>
      </div>
    </div>
  </section>
</template>

<style scoped>
.articles-page {
  max-width: 1120px;
  margin: 0 auto;
}

/* 页头：标题左 + 搜索框右 */
.list-page-header {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  padding: 48px 0 24px;
  gap: 24px;
}
.list-page-title {
  display: inline-flex;
  align-items: baseline;
  gap: 12px;
}
.list-page-heading {
  font-size: 48px;
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

/* 搜索框（紧凑版，放在标题右侧） */
.search-bar {
  position: relative;
  flex-shrink: 0;
  width: 240px;
}
.search-input {
  width: 100%;
  height: 40px;
  padding: 0 40px 0 14px;
  border: 1px solid var(--color-border);
  border-radius: 8px;
  background: transparent;
  color: var(--color-text);
  font-size: 14px;
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
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  width: 16px;
  height: 16px;
  color: var(--color-text-light);
  pointer-events: none;
}
.search-clear {
  position: absolute;
  right: 34px;
  top: 50%;
  transform: translateY(-50%);
  width: 18px;
  height: 18px;
  border: none;
  background: transparent;
  color: var(--color-text-light);
  font-size: 18px;
  line-height: 1;
  cursor: pointer;
  padding: 0;
}
.search-clear:hover {
  color: var(--color-text);
}

/* 主体两栏布局 */
.articles-body {
  display: grid;
  grid-template-columns: 200px 1fr;
  gap: 48px;
  align-items: start;
}

/* ========== 左侧标签边栏 ========== */
.topics-sidebar {
  position: sticky;
  top: 96px;
  flex-shrink: 0;
}
.topics-title {
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 2px;
  color: var(--color-text-light);
  margin-bottom: 12px;
  text-transform: uppercase;
}
.topics-nav {
  display: flex;
  flex-direction: column;
  gap: 2px;
}
.topic-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 6px 12px;
  border-radius: 6px;
  font-size: 14px;
  color: var(--color-text-light);
  cursor: pointer;
  transition: background-color 0.15s, color 0.15s;
}
.topic-item:hover {
  background-color: rgba(0, 0, 0, 0.04);
  color: var(--color-text);
}
html.dark .topic-item:hover {
  background-color: rgba(255, 255, 255, 0.06);
}
.topic-item.is-active {
  background-color: rgba(0, 0, 0, 0.07);
  color: var(--color-text);
  font-weight: 600;
}
html.dark .topic-item.is-active {
  background-color: rgba(255, 255, 255, 0.1);
}
.topic-name {
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
.topic-count {
  flex-shrink: 0;
  margin-left: 12px;
  font-size: 12px;
  color: var(--color-text-light);
  opacity: 0.7;
}

/* ========== 右侧文章区 ========== */
.articles-main {
  min-width: 0;
}
.page-description {
  color: var(--color-text-light);
  font-size: 16px;
  line-height: 1.6;
  margin-bottom: 32px;
  max-width: 560px;
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

/* 移动端：单栏布局，边栏折叠到顶部或隐藏 */
@media (max-width: 768px) {
  .list-page-header {
    flex-direction: column;
    padding: 32px 0 16px;
    gap: 16px;
  }
  .list-page-heading {
    font-size: 32px;
  }
  .search-bar {
    width: 100%;
  }

  .articles-body {
    grid-template-columns: 1fr;
    gap: 24px;
  }
  .topics-sidebar {
    position: static;
  }
  .topics-nav {
    flex-direction: row;
    flex-wrap: wrap;
    gap: 8px;
  }
  .topic-item {
    padding: 4px 12px;
    background-color: rgba(0, 0, 0, 0.04);
    border-radius: 20px;
    font-size: 13px;
  }
  .topic-item.is-active {
    background-color: var(--color-primary);
    color: #fff;
  }
  .topic-item.is-active .topic-count {
    color: rgba(255, 255, 255, 0.8);
  }
  html.dark .topic-item {
    background-color: rgba(255, 255, 255, 0.08);
  }
  html.dark .topic-item.is-active {
    background-color: var(--color-primary);
  }
  .page-description {
    display: none;
  }
}
</style>
