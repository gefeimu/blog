<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import MarkdownIt from 'markdown-it'
import { getArticle } from '../api/article'
import { getCategories } from '../api/category'
import Sidebar from '../components/Sidebar.vue'
import ArticleLayoutDefault from '../components/article/ArticleLayoutDefault.vue'
import ArticleLayoutMinimal from '../components/article/ArticleLayoutMinimal.vue'
import ArticleLayoutBanner from '../components/article/ArticleLayoutBanner.vue'

const route = useRoute()
const md = new MarkdownIt({ html: false, linkify: true, breaks: true })

const article = ref(null)
const notFound = ref(false)
const loading = ref(true)
const html = ref('')
const categories = ref([])

// 布局驱动：文章 layout 字段决定渲染组件，未知值回退默认布局
const layoutMap = {
  default: ArticleLayoutDefault,
  minimal: ArticleLayoutMinimal,
  banner: ArticleLayoutBanner,
}
const currentLayout = computed(() => layoutMap[article.value?.layout] || ArticleLayoutDefault)

onMounted(async () => {
  try {
    article.value = await getArticle(route.params.id)
    // 优先使用后端渲染管线产物 contentHtml（commonmark，含懒加载/外链处理）；
    // 旧数据或异常时回退前端 markdown-it 渲染
    if (article.value.contentHtml) {
      html.value = article.value.contentHtml
    } else if (article.value.content) {
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

      <component
        :is="currentLayout"
        v-else-if="article"
        :article="article"
        :html="html"
      />
    </div>

    <Sidebar :categories="categories" />
  </div>
</template>
