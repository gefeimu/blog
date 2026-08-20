/**
 * 前台 Markdown 渲染管线（统一走 markdown-it）
 *
 * 设计说明：文章详情页的正文与 TOC 必须由同一渲染器、同一 slug 规则生成，
 * 否则 TOC 链接的锚点与正文标题 id 对不上，点击无法跳转。
 * 后端 contentHtml（commonmark）保留在接口中（兼容/未来 SEO 用），
 * 但前台正文一律用本模块渲染，保证 markdown-it-anchor 与
 * markdown-it-toc-done-right 的锚点同源一致。
 *
 * markdown-it-toc-done-right 4.x 通过 callback 回调返回目录 HTML，
 * 故 renderArticle 一次渲染同时产出正文 html 与目录 toc。
 */
import MarkdownIt from 'markdown-it'
import anchor from 'markdown-it-anchor'
import tocDoneRight from 'markdown-it-toc-done-right'

/** 注册外链新窗口 + 图片懒加载规则（替代后端 ExternalLinkProcessor / LazyImageProcessor） */
function registerRules(md: MarkdownIt) {
  const defaultLinkOpen =
    md.renderer.rules.link_open ||
    ((tokens, idx, options, _env, self) => self.renderToken(tokens, idx, options))
  md.renderer.rules.link_open = (tokens, idx, options, env, self) => {
    const href = tokens[idx].attrGet('href') || ''
    if (/^https?:\/\//.test(href)) {
      tokens[idx].attrSet('target', '_blank')
      tokens[idx].attrSet('rel', 'noopener noreferrer')
    }
    return defaultLinkOpen(tokens, idx, options, env, self)
  }

  const defaultImage =
    md.renderer.rules.image ||
    ((tokens, idx, options, _env, self) => self.renderToken(tokens, idx, options))
  md.renderer.rules.image = (tokens, idx, options, env, self) => {
    const token = tokens[idx]
    if (!token.attrGet('loading')) {
      token.attrSet('loading', 'lazy')
    }
    return defaultImage(tokens, idx, options, env, self)
  }
}

/** 渲染文章：返回正文 HTML 与目录 HTML（仅 h2/h3） */
export function renderArticle(source: string): { html: string; toc: string } {
  let toc = ''
  const md = new MarkdownIt({ html: false, linkify: true, breaks: true })
    .use(anchor, {
      // 标题 hover 显示 # 锚点链接（可复制分享）
      permalink: anchor.permalink.linkInsideHeader({
        class: 'header-anchor',
        symbol: '#',
        placement: 'before',
      }),
    })
    .use(tocDoneRight, {
      containerClass: 'toc',
      // 目录仅收录 h2/h3（文章主标题 h1 由布局组件单独展示）
      level: [2, 3],
      callback: (code) => {
        toc = code
      },
    })
  registerRules(md)
  const html = md.render(source || '')
  return { html, toc }
}

/** 目录是否为空（无 h2/h3 时不展示目录卡片） */
export function hasTocContent(tocHtml: string): boolean {
  return tocHtml.includes('<li')
}
