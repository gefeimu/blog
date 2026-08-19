import request from './request'

export const getArticles = (params) => request.get('/articles', { params })

export const getArticle = (id) => request.get(`/articles/${id}`)
