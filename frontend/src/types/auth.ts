/**
 * 认证相关类型
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */

/** 登录请求 */
export interface LoginRequest {
  username: string
  password: string
}

/** 登录响应 */
export interface LoginResponse {
  accessToken: string
  tokenType: string
  expiresIn: number
  userId: string
  username: string
  role: 'admin' | 'user' | 'guest'
}

/** 用户信息 */
export interface UserInfo {
  userId: string
  username: string
  role: 'admin' | 'user' | 'guest'
  departmentId?: string
  email?: string
  status?: string
}
