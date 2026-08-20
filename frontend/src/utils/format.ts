/** 格式化 ISO 时间为 YYYY-MM-DD */
export function formatDate(value: string | null | undefined): string {
  if (!value) return ''
  const d = new Date(value)
  if (Number.isNaN(d.getTime())) return ''
  const y = d.getFullYear()
  const m = String(d.getMonth() + 1).padStart(2, '0')
  const day = String(d.getDate()).padStart(2, '0')
  return `${y}-${m}-${day}`
}

/**
 * 估算阅读时长（分钟）
 * 规则：剔除代码块/图片后，中文按 300 字/分钟、英文数字按 200 词/分钟，向上取整，最少 1 分钟
 */
export function calcReadingTime(content: string | undefined | null): number {
  if (!content) return 1
  const text = content
    .replace(/```[\s\S]*?```/g, '')
    .replace(/!\[[^\]]*\]\([^)]*\)/g, '')
    .replace(/[#>*`_\-\[\]()!|~]/g, ' ')
  const cjk = (text.match(/[\u4e00-\u9fa5]/g) || []).length
  const words = (text.match(/[a-zA-Z0-9]+/g) || []).length
  return Math.max(1, Math.ceil(cjk / 300 + words / 200))
}
