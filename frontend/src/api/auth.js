import request from './request'

export const login = (username, password) =>
  request.post('/auth/login', { username, password })
