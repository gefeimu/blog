<script setup>
import { ref, watch, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { getArticles } from '../api/article'
import { getCategory } from '../api/category'
import ArticleCard from '../components/ArticleCard.vue'
import Sidebar from '../components/Sidebar.vue'

const route = useRoute()
const categoryId = ref(route.params.id)
const category = ref(null)
const articles = ref([])
const categories = ref([])
const total = ref(0)
const loading = ref(true)
const page = ref(1)
const size = 10
const hasMore = ref(true)

async function load(reset = false) {
  loading.value = true
  try {
    if (reset) {
      page.value = 1
      articles.value = []
    }
    const data = await getArticles({ page: page.value, size, status: 1, categoryId: categoryId.value })
    articles.value = reset ? data.list : [...articles.value, ...data.list]
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
  load()
}

// 切换分类时重置列表
watch(
  () => route.params.id,
  (id) => {
    categoryId.value = id
    load(true)
    loadCategory()
  }
)

async function loadCategory() {
  try {
    category.value = await getCategory(categoryId.value)
  } catch (e) {
    category.value = null
  }
}

onMounted(() => {
  load(true)
  loadCategory()
  // 侧栏分类列表
  import('../api/category').then(({ getCategories }) =>
    getCategories().then((list) => (categories.value = list))
  )
})
</script>

<template>
  <div class="layout">
    <div>
      <div class="page-title">
        {{ category ? `${category.name}（${total}）` : '加载中...' }}
      </div>

      <ArticleCard v-for="a in articles" :key="a.id" :article="a" />

      <div v-if="loading" class="loading">加载中...</div>
      <div v-else-if="!articles.length" class="empty-tip">这个分类还没有文章</div>
      <div v-else-if="hasMore" class="loading" style="cursor: pointer" @click="loadMore">
        加载更多
      </div>
    </div>

    <Sidebar :categories="categories" />
  </div>
</template>
