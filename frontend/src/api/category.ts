import { http } from './request'
import type { Category, CategoryPayload } from '../types/blog'

export const getCategories = () => http.get<Category[]>('/categories')

export const getCategory = (id: number | string) => http.get<Category>(`/categories/${id}`)

// ---------- 管理（需登录） ----------

export const createCategory = (data: CategoryPayload) =>
  http.post<Category>('/admin/categories', data)

export const updateCategory = (id: number, data: CategoryPayload) =>
  http.put<Category>(`/admin/categories/${id}`, data)

export const deleteCategory = (id: number) => http.delete<null>(`/admin/categories/${id}`)
