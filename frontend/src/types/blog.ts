/**
 * 领域类型定义：与后端 API 的 JSON 结构一一对应
 * 前端所有页面/组件/API 层共享，改动一处全局生效
 */

/** 分类 */
export interface Category {
  id: number
  name: string
  sort: number
  createdAt: string
}

/** 标签（后台编辑页下拉） */
export interface Tag {
  id: number
  name: string
}

/** 文章扩展字段（DB ext JSON 列） */
export interface ArticleExt {
  /** 封面图 URL（banner 布局顶部展示） */
  cover?: string
  [key: string]: unknown
}

/** 展示布局 */
export type ArticleLayout = 'default' | 'minimal' | 'banner'

/** 文章摘要（列表项） */
export interface ArticleSummary {
  id: number
  title: string
  categoryId: number | null
  categoryName?: string
  summary?: string
  status: number
  viewCount: number
  createdAt: string
  updatedAt: string
  tags?: string[]
}

/** 文章详情 */
export interface ArticleDetail extends ArticleSummary {
  content?: string
  contentHtml?: string
  layout?: ArticleLayout | string
  ext?: ArticleExt
  tagIds?: number[]
}

/** 分页结果 */
export interface PageResult<T> {
  list: T[]
  total: number
}

/** 文章列表查询参数 */
export interface ArticleQuery {
  page: number
  size: number
  keyword?: string
  status?: number | string
  categoryId?: number | string
}

/** 文章创建/更新入参 */
export interface ArticlePayload {
  title: string
  categoryId: number
  tagIds?: number[]
  summary?: string
  status: number
  layout: string
  ext: ArticleExt
  content: string
}

/** 登录结果 */
export interface LoginResult {
  token: string
  username: string
  nickname?: string
}

/** 分类创建/更新入参 */
export interface CategoryPayload {
  name: string
  sort: number
}
