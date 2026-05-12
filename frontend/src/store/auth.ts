/**
 * 认证状态管理：token/user 状态、登录/登出/鉴权
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import { login as loginApi, refreshToken as refreshTokenApi } from '@/api/auth'
import type { LoginRequest, UserInfo } from '@/types/auth'

const TOKEN_KEY = 'access_token'
const USER_KEY = 'user_info'

export const useAuthStore = defineStore('auth', () => {
  // ---- State ----
  const token = ref<string>(localStorage.getItem(TOKEN_KEY) || '')
  const user = ref<UserInfo | null>(null)
  const loading = ref(false)

  // ---- Getters ----
  const isLoggedIn = computed(() => !!token.value)
  const userRole = computed(() => user.value?.role || '')
  const isAdmin = computed(() => userRole.value === 'admin')
  const username = computed(() => user.value?.username || '')

  /** 检查是否拥有指定角色 */
  function hasRole(role: string | string[]): boolean {
    if (Array.isArray(role)) return role.includes(userRole.value)
    return userRole.value === role
  }

  // ---- Actions ----

  /** 登录 */
  async function doLogin(payload: LoginRequest): Promise<void> {
    loading.value = true
    try {
      const res = await loginApi(payload)
      token.value = res.accessToken
      localStorage.setItem(TOKEN_KEY, res.accessToken)
      user.value = {
        userId: res.userId,
        username: res.username,
        role: res.role,
      }
      localStorage.setItem(USER_KEY, JSON.stringify(user.value))
    } finally {
      loading.value = false
    }
  }

  /** 登出 */
  function doLogout(): void {
    token.value = ''
    user.value = null
    localStorage.removeItem(TOKEN_KEY)
    localStorage.removeItem(USER_KEY)
  }

  /** 检查登录状态（应用启动时调用） */
  function checkAuth(): void {
    const savedToken = localStorage.getItem(TOKEN_KEY)
    const savedUser = localStorage.getItem(USER_KEY)
    if (savedToken) {
      token.value = savedToken
    }
    if (savedUser) {
      try {
        user.value = JSON.parse(savedUser)
      } catch {
        user.value = null
      }
    }
  }

  /** 刷新令牌 */
  async function doRefreshToken(): Promise<void> {
    if (!token.value) return
    try {
      const res = await refreshTokenApi(token.value)
      token.value = res.accessToken
      localStorage.setItem(TOKEN_KEY, res.accessToken)
    } catch {
      doLogout()
    }
  }

  return {
    token,
    user,
    loading,
    isLoggedIn,
    userRole,
    isAdmin,
    username,
    hasRole,
    doLogin,
    doLogout,
    checkAuth,
    doRefreshToken,
  }
})
