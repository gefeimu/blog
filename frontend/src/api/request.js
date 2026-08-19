import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

// 统一处理：接口直接返回 data
request.interceptors.response.use(
  (response) => response.data,
  (error) => {
    const status = error.response?.status
    if (status === 404) {
      // 页面级 404 由调用方处理
      return Promise.reject(error)
    }
    console.error('[API]', error.message)
    return Promise.reject(error)
  }
)

export default request
