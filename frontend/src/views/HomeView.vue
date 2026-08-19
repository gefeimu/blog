<script setup>
import { ref, onMounted } from 'vue'
import { getArticles } from '../api/article'
import { getCategories } from '../api/category'
import ArticleCard from '../components/ArticleCard.vue'
import Sidebar from '../components/Sidebar.vue'

const articles = ref([])
const categories = ref([])
const total = ref(0)
const loading = ref(true)
const page = ref(1)
const size = 10
const hasMore = ref(true)

async function load(append = false) {
  loading.value = true
  try {
    const data = await getArticles({ page: page.value, size, status: 1 })
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

onMounted(async () => {
  load()
  try {
    categories.value = await getCategories()
  } catch (e) {
    console.error(e)
  }
})
</script>

<template>
  <div class="layout">
    <div>
      <div class="page-title">全部文章（{{ total }}）</div>

      <ArticleCard v-for="a in articles" :key="a.id" :article="a" />

      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="!articles.length" class="empty-tip">还没有文章</div>
      <div
        v-else-if="hasMore"
        class="loading"
        style="cursor: pointer"
        @click="loadMore"
      >
        加载更多
      </div>
    </div>

    <Sidebar :categories="categories" />
  </div>
</template>
