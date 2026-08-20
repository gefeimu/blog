import request from './request'

// ---------- 公开读 ----------

export const getArticles = (params) => request.get('/articles', { params })

export const getArticle = (id) => request.get(`/articles/${id}`)

export const getTags = () => request.get('/tags')

// ---------- 管理（需登录） ----------

export const createArticle = (data) => request.post('/articles', data)

export const updateArticle = (id, data) => request.put(`/articles/${id}`, data)

export const deleteArticle = (id) => request.delete(`/articles/${id}`)
