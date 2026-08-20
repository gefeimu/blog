import request from './request'

export const getCategories = () => request.get('/categories')

export const getCategory = (id) => request.get(`/categories/${id}`)

// ---------- 管理（需登录） ----------

export const createCategory = (data) => request.post('/admin/categories', data)

export const updateCategory = (id, data) => request.put(`/admin/categories/${id}`, data)

export const deleteCategory = (id) => request.delete(`/admin/categories/${id}`)
