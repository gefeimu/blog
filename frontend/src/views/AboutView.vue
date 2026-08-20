<script setup lang="ts">
// 静态关于页：博主简介 + 博客主题 + 联系方式占位
</script>

<template>
  <div class="about-page">
    <article class="card">
      <h1 class="page-title">关于</h1>

      <div class="about-section">
        <h2>博主</h2>
        <p>
          白工，医疗软件公司管理者，全栈开发工程师。
          主技术栈 Spring Boot + Vue + Oracle，兼顾 MyBatis 与 Spring JDBC。
          日常带 16 人团队，做 AI 病历书写和医嘱执行系统。
        </p>
      </div>

      <div class="about-section">
        <h2>博客主题</h2>
        <p>这里记录我感兴趣的话题，月更一篇，自用 + 朋友看：</p>
        <ul>
          <li>开发技术 —— 全栈实践、工具链、踩坑记录</li>
          <li>拼装模型 —— 高达/手办拼装心得</li>
          <li>骑行 —— 南京周边骑行路线与装备</li>
          <li>咖啡 —— 手冲器具与豆子笔记</li>
          <li>木工 —— 业余木作与工具</li>
          <li>随笔 —— 想到什么写什么</li>
        </ul>
      </div>

      <div class="about-section">
        <h2>关于本站</h2>
        <p>
          本站为个人自建博客，后端 Spring Boot 3 + MyBatis + MySQL 8，
          前端 Vue 3 + Vite + Element Plus，Docker Compose 编排部署。
          文章正文走 Markdown 文件存储，数据库只存元数据。
        </p>
      </div>

      <div class="about-section">
        <h2>联系</h2>
        <p>联系方式占位，后续补充。</p>
      </div>
    </article>
  </div>
</template>

<style scoped>
.about-page {
  padding: 24px 0 48px;
}
.about-section {
  margin-bottom: 24px;
}
.about-section h2 {
  font-size: 16px;
  font-weight: 700;
  margin-bottom: 10px;
}
.about-section p {
  font-size: 14px;
  line-height: 1.8;
  color: var(--color-text);
  margin-bottom: 8px;
}
.about-section ul {
  margin: 8px 0 8px 20px;
  font-size: 14px;
  line-height: 1.9;
}
</style>
