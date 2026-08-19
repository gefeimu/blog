import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// 本地开发：后端在笔记本上时，把 VITE_PROXY_TARGET 配成笔记本 IP（如 http://192.168.x.x）
// 例：.env.development 里写 VITE_PROXY_TARGET=http://192.168.1.10
const proxyTarget = process.env.VITE_PROXY_TARGET || 'http://localhost:80'

export default defineConfig({
  plugins: [vue()],
  build: {
    // 构建产物直接输出到 nginx 挂载目录，笔记本 pull 后即生效
    // 注意：不开 emptyOutDir（Windows 下清空外部目录会被安全机制拦截），目录空则直接写入
    outDir: '../nginx/html',
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: proxyTarget,
        changeOrigin: true,
      },
    },
  },
})
