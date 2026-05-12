/**
 * 认证 API：登录、刷新令牌
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
import http from '@/utils/http'
import type { LoginRequest, LoginResponse } from '@/types/auth'

/** 用户登录 */
export function login(data: LoginRequest): Promise<LoginResponse> {
  return http.post('/auth/login', data)
}

/** 刷新令牌 */
export function refreshToken(token: string): Promise<LoginResponse> {
  return http.post('/auth/refresh', { token })
}
