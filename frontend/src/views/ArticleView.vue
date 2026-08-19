<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import MarkdownIt from 'markdown-it'
import { getArticle } from '../api/article'
import { getCategories } from '../api/category'
import { formatDate } from '../utils/format'
import Sidebar from '../components/Sidebar.vue'

const route = useRoute()
const md = new MarkdownIt({ html: false, linkify: true, breaks: true })

const article = ref(null)
const notFound = ref(false)
const loading = ref(true)
const html = ref('')
const categories = ref([])

onMounted(async () => {
  try {
    article.value = await getArticle(route.params.id)
    // 正文走 Markdown（content 字段由后端正文存储接口提供，暂为空则占位）
    if (article.value.content) {
      html.value = md.render(article.value.content)
    }
  } catch (e) {
    notFound.value = true
  } finally {
    loading.value = false
  }
  try {
    categories.value = await getCategories()
  } catch (e) {
    // 侧栏分类拉取失败不阻塞
  }
})
</script>

<template>
  <div class="layout">
    <div>
      <div v-if="loading" class="loading">加载中...</div>

      <div v-else-if="notFound" class="empty-tip">文章不存在或已删除</div>

      <article v-else-if="article" class="card">
        <h1 class="article-title">{{ article.title }}</h1>
        <div class="article-meta">
          <span>{{ article.categoryName }}</span>
          <span>{{ formatDate(article.createdAt) }}</span>
          <span>浏览 {{ article.viewCount ?? 0 }}</span>
          <span v-if="article.tags?.length">
            <span v-for="t in article.tags" :key="t" class="tag">{{ t }}</span>
          </span>
        </div>

        <div v-if="html" class="article-content" v-html="html"></div>
        <div v-else class="empty-tip" style="padding: 32px 0">
          正文待开放（Markdown 正文存储接入后显示）
        </div>
      </article>
    </div>

    <Sidebar :categories="categories" />
  </div>
</template>
