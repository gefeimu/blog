import axios from 'axios'

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
    const status = error.response?.status
    const data = error.response?.data
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
    error.message = data?.error || error.message
    console.error('[API]', error.message)
    return Promise.reject(error)
  }
)

export default request
