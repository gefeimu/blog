<script setup lang="ts">
// 项目展示页（占位，后续可在后台维护）
interface Project {
  name: string
  desc: string
  link?: string
  tags: string[]
}

const projects = [
  {
    name: 'blog-frontend',
    desc: '本站前端，基于 Vue 3 + Vite + Element Plus。',
    link: 'https://github.com/gefeimu/blog',
    tags: ['Vue 3', 'TypeScript'],
  },
  {
    name: 'blog-backend',
    desc: '本站后端，基于 Spring Boot 3 + MyBatis + MySQL 8，Docker Compose 编排。',
    link: 'https://github.com/gefeimu/blog',
    tags: ['Spring Boot', 'MyBatis'],
  },
] as Project[]
</script>

<template>
  <section class="projects-page">
    <h1 class="page-title">项目</h1>
    <p class="page-sub">这里收录我做的一些小东西。</p>

    <div class="project-grid">
      <article v-for="p in projects" :key="p.name" class="card project-card">
        <h2 class="project-name">
          <a v-if="p.link" :href="p.link" target="_blank" rel="noopener noreferrer">
            {{ p.name }} ↗
          </a>
          <span v-else>{{ p.name }}</span>
        </h2>
        <p class="project-desc">{{ p.desc }}</p>
        <div class="project-tags">
          <span v-for="t in p.tags" :key="t" class="tag">{{ t }}</span>
        </div>
      </article>
    </div>
  </section>
</template>

<style scoped>
.projects-page {
  padding: 32px 0 48px;
}
.page-sub {
  color: var(--color-text-light);
  font-size: 14px;
  margin-bottom: 20px;
}
.project-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}
.project-card {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
.project-name {
  font-size: 18px;
  font-weight: 700;
}
.project-name a {
  color: var(--color-text);
}
.project-name a:hover {
  color: var(--color-primary);
}
.project-desc {
  color: var(--color-text-light);
  font-size: 13px;
  line-height: 1.6;
  flex: 1;
}
.project-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}
</style>