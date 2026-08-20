import { http } from './request'
import type { Tag } from '../types/blog'

// ---------- 公开读 ----------

export const getTags = () => http.get<Tag[]>('/tags')

// ---------- 管理（需登录） ----------

export const createTag = (data: { name: string }) =>
  http.post<Tag>('/tags', data)

export const updateTag = (id: number, data: { name: string }) =>
  http.put<Tag>(`/tags/${id}`, data)

export const deleteTag = (id: number) => http.delete<null>(`/tags/${id}`)
