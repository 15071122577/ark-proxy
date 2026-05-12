/**
 * 核心 API 响应类型
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */

/** 统一 API 响应格式 */
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  timestamp: string
  requestId?: string
}

/** 分页数据结构 */
export interface PaginatedData<T = any> {
  items: T[]
  pagination: {
    page: number
    pageSize: number
    total: number
    totalPages: number
  }
}

/** API 错误详情 */
export interface ApiError {
  type: string
  code: string
  message: string
  param: string | null
  detail: string
}
