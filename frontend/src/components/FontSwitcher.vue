<script setup lang="ts">
import { onMounted, ref } from 'vue'

/**
 * 字体切换器（右下角 Aa 按钮）
 * 给 <html> 加 data-font 属性切换字体主题，选择存 localStorage。
 * 选项与 fonts.css 里的 html[data-font='xxx'] 主题一一对应，
 * 加新字体 = fonts.css 加主题块 + 这里加一个选项。
 */
const STORAGE_KEY = 'blog-font'

interface FontOption {
  key: string
  label: string
  desc: string
}

const options: FontOption[] = [
  { key: '', label: '系统默认', desc: '各设备原生字体' },
  { key: 'smiley', label: '得意黑', desc: '标题+正文粗斜体（web 字体）' },
  { key: 'kaiti', label: '楷体', desc: '全文楷体（系统字体）' },
  { key: 'serif', label: '宋体', desc: '全文衬线（系统字体）' },
]

const open = ref(false)
const current = ref('')

function applyFont(key: string) {
  current.value = key
  const root = document.documentElement
  if (key) {
    root.setAttribute('data-font', key)
  } else {
    root.removeAttribute('data-font')
  }
  try {
    localStorage.setItem(STORAGE_KEY, key)
  } catch {
    /* localStorage 不可用时忽略 */
  }
}

onMounted(() => {
  let saved = ''
  try {
    saved = localStorage.getItem(STORAGE_KEY) || ''
  } catch {
    /* ignore */
  }
  if (options.some((o) => o.key === saved)) {
    applyFont(saved)
  }
})
</script>

<template>
  <div class="font-switcher">
    <button
      class="fs-trigger"
      :title="'切换字体（当前：' + (options.find((o) => o.key === current)?.label || '系统默认') + '）'"
      @click="open = !open"
    >
      Aa
    </button>

    <Transition name="fs-pop">
      <div v-if="open" class="fs-panel" @click.stop>
        <p class="fs-title">字体预览</p>
        <button
          v-for="opt in options"
          :key="opt.key"
          class="fs-option"
          :class="{ active: current === opt.key }"
          @click="applyFont(opt.key); open = false"
        >
          <span class="fs-label">{{ opt.label }}</span>
          <span class="fs-desc">{{ opt.desc }}</span>
        </button>
      </div>
    </Transition>
  </div>
</template>

<style scoped>
.font-switcher {
  position: fixed;
  right: 20px;
  bottom: 20px;
  z-index: 1000;
}
.fs-trigger {
  width: 44px;
  height: 44px;
  border-radius: 50%;
  border: 1px solid var(--color-border);
  background: var(--color-card);
  color: var(--color-text);
  font-size: 14px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.15s, border-color 0.15s;
}
.fs-trigger:hover {
  transform: scale(1.06);
  border-color: var(--color-primary);
}
.fs-panel {
  position: absolute;
  right: 0;
  bottom: 52px;
  width: 230px;
  background: var(--color-card);
  border: 1px solid var(--color-border);
  border-radius: 10px;
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.15);
  padding: 10px;
}
.fs-title {
  font-size: 12px;
  color: var(--color-text-light);
  margin: 0 0 8px;
  padding: 0 6px;
}
.fs-option {
  display: flex;
  align-items: baseline;
  gap: 10px;
  width: 100%;
  padding: 8px 10px;
  margin: 2px 0;
  border: none;
  border-radius: 8px;
  background: transparent;
  color: var(--color-text);
  text-align: left;
  cursor: pointer;
  transition: background-color 0.15s;
}
.fs-option:hover {
  background-color: rgba(0, 0, 0, 0.05);
}
html.dark .fs-option:hover {
  background-color: rgba(255, 255, 255, 0.08);
}
.fs-option.active {
  background-color: var(--color-primary);
  color: #fff;
}
.fs-option.active .fs-desc {
  color: rgba(255, 255, 255, 0.8);
}
.fs-label {
  font-size: 15px;
  font-weight: 600;
}
.fs-desc {
  font-size: 12px;
  color: var(--color-text-light);
}

.fs-pop-enter-active,
.fs-pop-leave-active {
  transition: opacity 0.15s, transform 0.15s;
}
.fs-pop-enter-from,
.fs-pop-leave-to {
  opacity: 0;
  transform: translateY(6px);
}
</style>
