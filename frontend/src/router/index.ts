/**
 * 路由表 + beforeEach 守卫（认证 + 角色校验）
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
import { createRouter, createWebHashHistory } from 'vue-router'
import type { RouteRecordRaw } from 'vue-router'
import { useAuthStore } from '@/store/auth'

/** 路由表 */
const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/login/LoginView.vue'),
    meta: { requiresAuth: false },
  },
  {
    path: '/',
    component: () => import('@/components/layout/AppLayout.vue'),
    redirect: '/dashboard/personal',
    meta: { requiresAuth: true },
    children: [
      // 看板
      {
        path: 'dashboard/personal',
        name: 'PersonalDashboard',
        component: () => import('@/views/dashboard/PersonalDashboard.vue'),
        meta: { title: '个人看板', icon: 'User' },
      },
      {
        path: 'dashboard/team',
        name: 'TeamDashboard',
        component: () => import('@/views/dashboard/TeamDashboard.vue'),
        meta: { title: '团队看板', icon: 'UserFilled', role: ['admin', 'team_lead'] },
      },
      {
        path: 'dashboard/system',
        name: 'SystemDashboard',
        component: () => import('@/views/dashboard/SystemDashboard.vue'),
        meta: { title: '系统看板', icon: 'Monitor', role: 'admin' },
      },
      // AK 管理
      {
        path: 'ak/apply',
        name: 'AKApply',
        component: () => import('@/views/ak/AKApplyView.vue'),
        meta: { title: 'AK 申请', icon: 'Plus' },
      },
      {
        path: 'ak/applications',
        name: 'MyApplications',
        component: () => import('@/views/ak/MyApplicationsView.vue'),
        meta: { title: '我的申请', icon: 'Document' },
      },
      {
        path: 'ak/my',
        name: 'MyAK',
        component: () => import('@/views/ak/MyAKView.vue'),
        meta: { title: '我的 AK', icon: 'Key' },
      },
      // AK 审批（管理员）
      {
        path: 'admin/ak-approval',
        name: 'AKApproval',
        component: () => import('@/views/admin/AKApprovalView.vue'),
        meta: { title: 'AK 审批', icon: 'Checked', role: 'admin' },
      },
      // 预警
      {
        path: 'alert/rules',
        name: 'AlertRules',
        component: () => import('@/views/alert/AlertRulesView.vue'),
        meta: { title: '预警规则', icon: 'Bell', role: 'admin' },
      },
      {
        path: 'alert/history',
        name: 'AlertHistory',
        component: () => import('@/views/alert/AlertHistoryView.vue'),
        meta: { title: '预警历史', icon: 'Clock' },
      },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/dashboard/personal',
  },
]

const router = createRouter({
  history: createWebHashHistory(),
  routes,
})

// ---- 全局前置守卫 ----
router.beforeEach((to, _from, next) => {
  const auth = useAuthStore()

  // 不需要认证的页面直接放行
  if (to.meta.requiresAuth === false) {
    // 已登录用户访问登录页，跳转首页
    if (to.name === 'Login' && auth.isLoggedIn) {
      next({ path: '/dashboard/personal' })
      return
    }
    next()
    return
  }

  // 未登录，跳转登录页
  if (!auth.isLoggedIn) {
    next({ name: 'Login', query: { redirect: to.fullPath } })
    return
  }

  // 角色校验
  const requiredRole = to.meta.role as string | string[] | undefined
  if (requiredRole && !auth.hasRole(requiredRole)) {
    next({ path: '/dashboard/personal' })
    return
  }

  next()
})

export default router
