import { http } from './request'
import type { ArticleDetail, ArticlePayload, ArticleQuery, ArticleSummary, PageResult, Tag } from '../types/blog'

// ---------- 公开读 ----------

export const getArticles = (params: ArticleQuery) =>
  http.get<PageResult<ArticleSummary>>('/articles', { params })

export const getArticle = (id: number | string) =>
  http.get<ArticleDetail>(`/articles/${id}`)

export const getTags = () => http.get<Tag[]>('/tags')

// ---------- 管理（需登录） ----------

export const createArticle = (data: ArticlePayload) =>
  http.post<ArticleDetail>('/articles', data)

export const updateArticle = (id: number, data: ArticlePayload) =>
  http.put<ArticleDetail>(`/articles/${id}`, data)

export const deleteArticle = (id: number) => http.delete<null>(`/articles/${id}`)
