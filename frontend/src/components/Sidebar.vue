<script setup>
defineProps({
  categories: { type: Array, default: () => [] },
})
</script>

<template>
  <aside class="sidebar">
    <div class="card">
      <div class="sidebar-title">分类</div>
      <div v-for="c in categories" :key="c.id" class="category-item">
        <router-link :to="`/category/${c.id}`">{{ c.name }}</router-link>
      </div>
    </div>
    <div class="card">
      <div class="sidebar-title">关于</div>
      <div style="font-size: 13px; color: var(--color-text-light)">
        记录开发、模型、骑行、咖啡、木工与随笔。
      </div>
    </div>
  </aside>
</template>
