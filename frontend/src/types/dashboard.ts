/**
 * 看板相关类型
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */

/** 个人看板汇总数据 */
export interface PersonalSummary {
  dailyRequests: number
  monthlyRequests: number
  dailyTokens: number
  monthlyTokens: number
  dailyCost: number
  monthlyCost: number
  quotaUsageRate: number
  requestSuccessRate: number
  avgResponseTime: number
}

/** 趋势数据点 */
export interface TrendPoint {
  timestamp: string
  requests: number
  tokens: number
  cost: number
}

/** 分布数据项（饼图） */
export interface DistributionItem {
  name: string
  value: number
}

/** 团队看板汇总数据 */
export interface TeamSummary {
  dailyRequests: number
  dailyTokens: number
  dailyCost: number
  budgetUsageRate: number
  memberCount: number
  avgResponseTime: number
}

/** 团队成员排行 */
export interface MemberRanking {
  userId: string
  username: string
  requestCount: number
  tokenCount: number
  cost: number
}

/** 系统实时数据 */
export interface SystemRealtime {
  currentQps: number
  systemAvailability: number
  errorRate: number
  p95ResponseTime: number
  p99ResponseTime: number
  activeAlerts: number
  totalKeys: number
  healthyKeys: number
}

/** 厂商健康状态 */
export interface VendorHealth {
  providerId: string
  providerName: string
  status: 'healthy' | 'degraded' | 'down'
  latency: number
  errorRate: number
}
