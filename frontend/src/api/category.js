import request from './request'

export const getCategories = () => request.get('/categories')

export const getCategory = (id) => request.get(`/categories/${id}`)
