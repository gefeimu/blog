import { http } from './request'

/** 上传响应：后端返回 {url} */
export interface UploadResult {
  url: string
}

export function uploadImage(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return http.post<UploadResult>('/upload/image', formData, {
    headers: { 'Content-Type': 'multipart/form-data' },
    timeout: 30000,
  })
}
