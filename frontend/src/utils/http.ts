/**
 * Axios 实例封装：JWT 注入、401 重定向、响应解包
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
import axios from 'axios'
import type { AxiosInstance, InternalAxiosRequestConfig, AxiosResponse } from 'axios'
import { ElMessage } from 'element-plus'
import type { ApiResponse } from '@/types/api'

const BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api/v1'

/** Axios 单例 */
const http: AxiosInstance = axios.create({
  baseURL: BASE_URL,
  timeout: 30000,
  headers: { 'Content-Type': 'application/json' },
})

// ---- 请求拦截器：注入 JWT ----
http.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = localStorage.getItem('access_token')
    if (token && config.headers) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error),
)

// ---- 响应拦截器：解包 ApiResponse + 401 处理 ----
http.interceptors.response.use(
  (response: AxiosResponse) => {
    const body = response.data as ApiResponse<unknown>

    // 如果后端返回标准 ApiResponse，检查 code
    if (body && typeof body.code === 'number') {
      if (body.code === 200) {
        // 解包：调用方直接拿到 data
        return body.data as any
      }
      // 业务异常
      const msg = body.message || '请求失败'
      ElMessage.error(msg)
      return Promise.reject(new Error(msg))
    }

    // 非 ApiResponse 格式（如文件下载），原样返回
    return response.data
  },
  (error) => {
    if (error.response) {
      const { status, data } = error.response
      if (status === 401) {
        localStorage.removeItem('access_token')
        localStorage.removeItem('user_info')
        // 避免重复跳转
        if (window.location.hash !== '#/login') {
          ElMessage.warning('登录已过期，请重新登录')
          window.location.hash = '#/login'
        }
      } else if (status === 403) {
        ElMessage.error('无权限访问')
      } else if (status === 429) {
        ElMessage.warning('请求过于频繁，请稍后再试')
      } else {
        const msg = data?.message || `服务器错误 (${status})`
        ElMessage.error(msg)
      }
    } else {
      ElMessage.error('网络异常，请检查网络连接')
    }
    return Promise.reject(error)
  },
)

export default http
