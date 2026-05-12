/**
 * 看板 API：个人/团队/系统看板数据
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
import http from '@/utils/http'
import type {
  PersonalSummary,
  TrendPoint,
  DistributionItem,
  TeamSummary,
  MemberRanking,
  SystemRealtime,
  VendorHealth,
} from '@/types/dashboard'

/** 获取个人看板汇总 */
export function getPersonalSummary(): Promise<PersonalSummary> {
  return http.get('/dashboard/personal/summary')
}

/** 获取个人趋势数据 */
export function getPersonalTrend(days = 7): Promise<TrendPoint[]> {
  return http.get('/dashboard/personal/trend', { params: { days } })
}

/** 获取个人用量分布（按功能/模型） */
export function getPersonalDistribution(type: 'function' | 'model'): Promise<DistributionItem[]> {
  return http.get('/dashboard/personal/distribution', { params: { type } })
}

/** 获取个人配额使用率 */
export function getPersonalQuotaUsage(): Promise<number> {
  return http.get('/dashboard/personal/quota-usage')
}

/** 获取团队看板汇总 */
export function getTeamSummary(): Promise<TeamSummary> {
  return http.get('/dashboard/team/summary')
}

/** 获取团队趋势数据 */
export function getTeamTrend(days = 7): Promise<TrendPoint[]> {
  return http.get('/dashboard/team/trend', { params: { days } })
}

/** 获取团队成员排行 */
export function getMemberRanking(): Promise<MemberRanking[]> {
  return http.get('/dashboard/team/ranking')
}

/** 获取团队部门分布 */
export function getTeamDistribution(): Promise<DistributionItem[]> {
  return http.get('/dashboard/team/distribution')
}

/** 获取系统实时数据 */
export function getSystemRealtime(): Promise<SystemRealtime> {
  return http.get('/dashboard/system/realtime')
}

/** 获取厂商健康状态 */
export function getVendorHealth(): Promise<VendorHealth[]> {
  return http.get('/dashboard/system/vendor-health')
}
