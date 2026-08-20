import { http } from './request'
import type { LoginResult } from '../types/blog'

export const login = (username: string, password: string) =>
  http.post<LoginResult>('/auth/login', { username, password })
