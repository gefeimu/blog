import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

// 本地开发：后端在笔记本上时，把 VITE_PROXY_TARGET 配成笔记本 IP（如 http://192.168.x.x）
// 例：.env.development 里写 VITE_PROXY_TARGET=http://192.168.1.10
const proxyTarget = process.env.VITE_PROXY_TARGET || 'http://localhost:80'

export default defineConfig({
  plugins: [
    vue(),
    // Element Plus 按需自动导入：模板中的 el-* 组件、ElMessage 等 API、v-loading 指令
    // 样式随组件自动引入，替换原先的全量 element-plus/dist/index.css，显著减小首屏体积
    // dts 输出到 src/ 下，供 tsconfig include（vue-tsc 类型检查需要）
    AutoImport({
      resolvers: [ElementPlusResolver()],
      dts: 'src/auto-imports.d.ts',
    }),
    Components({
      resolvers: [ElementPlusResolver()],
      dts: 'src/components.d.ts',
    }),
  ],
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
