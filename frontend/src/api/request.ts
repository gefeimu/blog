import axios from 'axios'
import type { AxiosRequestConfig } from 'axios'

/** 统一错误信息提取：后端 {error} 或浏览器错误优先，兜底 fallback */
export function getErrorMessage(e: unknown, fallback = '请求失败'): string {
  if (typeof e === 'object' && e !== null) {
    const msg = (e as { message?: unknown }).message
    if (typeof msg === 'string' && msg) return msg
  }
  return fallback
}

const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

// 请求拦截：自动附带 JWT
request.interceptors.request.use((config) => {
  const token = localStorage.getItem('blog_token')
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

// 响应拦截：接口直接返回 data；401 跳后台登录
request.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const status = (error as { response?: { status?: number } }).response?.status
    const data = (error as { response?: { data?: { error?: string } } }).response?.data
    if (status === 401) {
      localStorage.removeItem('blog_token')
      localStorage.removeItem('blog_nickname')
      if (location.pathname.startsWith('/admin')) {
        location.href = '/admin/login'
      }
      return Promise.reject(error)
    }
    if (status === 404) {
      return Promise.reject(error)
    }
    // 统一把后端 {error: "..."} 塞进 message，调用方 catch 后直接展示
    if (data?.error) {
      error.message = data.error
    }
    console.error('[API]', error.message)
    return Promise.reject(error)
  }
)

/**
 * 泛型 http 封装：响应拦截器已把 AxiosResponse 剥成 data，
 * 这里用类型断言抹平运行时与类型系统的差异，调用方直接拿到精确类型。
 */
export const http = {
  get<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return request.get(url, config) as unknown as Promise<T>
  },
  post<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
    return request.post(url, data, config) as unknown as Promise<T>
  },
  put<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
    return request.put(url, data, config) as unknown as Promise<T>
  },
  delete<T>(url: string, config?: AxiosRequestConfig): Promise<T> {
    return request.delete(url, config) as unknown as Promise<T>
  },
}

export default request
