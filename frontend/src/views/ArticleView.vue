<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import type { Component } from 'vue'
import { useRoute } from 'vue-router'
import { getArticle } from '../api/article'
import { renderArticle, hasTocContent } from '../utils/markdown'
import type { ArticleDetail } from '../types/blog'
import ArticleLayoutDefault from '../components/article/ArticleLayoutDefault.vue'
import ArticleLayoutMinimal from '../components/article/ArticleLayoutMinimal.vue'
import ArticleLayoutBanner from '../components/article/ArticleLayoutBanner.vue'

const route = useRoute()

const article = ref<ArticleDetail | null>(null)
const notFound = ref(false)
const loading = ref(true)
const html = ref('')
const tocHtml = ref('')

// 布局驱动：文章 layout 字段决定渲染组件，未知值回退默认布局
const layoutMap: Record<string, Component> = {
  default: ArticleLayoutDefault,
  minimal: ArticleLayoutMinimal,
  banner: ArticleLayoutBanner,
}
const currentLayout = computed(
  () => layoutMap[article.value?.layout || 'default'] || ArticleLayoutDefault
)

// 正文与 TOC 由同一 markdown-it 实例渲染，锚点同源一致
const showToc = computed(() => hasTocContent(tocHtml.value))

onMounted(async () => {
  try {
    article.value = await getArticle(String(route.params.id))
    if (article.value.content) {
      const { html: h, toc } = renderArticle(article.value.content)
      html.value = h
      tocHtml.value = toc
    }
  } catch (e) {
    notFound.value = true
  } finally {
    loading.value = false
  }
})
</script>

<template>
  <div class="article-page">
    <div v-if="loading" class="loading">加载中...</div>

    <div v-else-if="notFound" class="empty-tip">文章不存在或已删除</div>

    <template v-else-if="article">
      <!-- 有目录时：正文 + 右侧 sticky 目录（单栏内部子布局） -->
      <div v-if="showToc" class="article-with-toc">
        <component :is="currentLayout" :article="article" :html="html" />
        <aside class="card toc-card">
          <div class="sidebar-title">目录</div>
          <div class="toc" v-html="tocHtml"></div>
        </aside>
      </div>
      <component v-else :is="currentLayout" :article="article" :html="html" />
    </template>
  </div>
</template>

<style scoped>
.article-page {
  padding: 24px 0 48px;
}
.article-with-toc {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 200px;
  gap: 24px;
  align-items: start;
}
.toc-card {
  position: sticky;
  top: 72px;
  max-height: calc(100vh - 96px);
  overflow-y: auto;
  padding: 16px 18px;
  font-size: 13px;
}

@media (max-width: 900px) {
  .article-with-toc {
    grid-template-columns: 1fr;
  }
  .toc-card {
    display: none;
  }
}
</style>
