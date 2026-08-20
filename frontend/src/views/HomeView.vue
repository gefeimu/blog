<script setup lang="ts">
// 首页 hero 区：左侧大字 + signature，右侧圆形头像
</script>

<template>
  <section class="hero">
    <div class="hero-text">
      <h1 class="hero-title">Hi，我是歌斐木</h1>
      <p class="hero-sig">
        May your coffee be strong and your bugs be few.
      </p>
    </div>
    <img src="/avatar.png" alt="歌斐木" class="hero-avatar" />
  </section>
</template>

<style scoped>
.hero {
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 48px;
  align-items: center;
  padding: 80px 0 64px;
}
.hero-text {
  min-width: 0;
}
.hero-title {
  font-size: 44px;
  font-weight: 700;
  line-height: 1.2;
  letter-spacing: -0.5px;
  margin-bottom: 16px;
}
.hero-sig {
  font-size: 16px;
  color: var(--color-text-light);
  line-height: 1.6;
}
.hero-avatar {
  width: 180px;
  height: 180px;
  border-radius: 50%;
  object-fit: cover;
  border: 1px solid var(--color-border);
}

@media (max-width: 768px) {
  .hero {
    grid-template-columns: 1fr;
    justify-items: center;
    text-align: center;
    padding: 40px 0;
  }
  .hero-title {
    font-size: 32px;
  }
  .hero-avatar {
    width: 140px;
    height: 140px;
  }
}
</style>