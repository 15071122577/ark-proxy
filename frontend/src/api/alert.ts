/**
 * 预警 API：规则管理、历史查询
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
import http from '@/utils/http'
import type { AlertRule, AlertRuleCreateRequest, AlertHistoryItem } from '@/types/alert'
import type { PaginatedData } from '@/types/api'

/** 获取预警规则列表 */
export function getAlertRules(): Promise<AlertRule[]> {
  return http.get('/alert/rules')
}

/** 创建预警规则 */
export function createAlertRule(data: AlertRuleCreateRequest): Promise<AlertRule> {
  return http.post('/alert/rules', data)
}

/** 更新预警规则 */
export function updateAlertRule(ruleId: string, data: Partial<AlertRuleCreateRequest>): Promise<AlertRule> {
  return http.put(`/alert/rules/${ruleId}`, data)
}

/** 删除预警规则 */
export function deleteAlertRule(ruleId: string): Promise<void> {
  return http.delete(`/alert/rules/${ruleId}`)
}

/** 启用/禁用预警规则 */
export function toggleAlertRule(ruleId: string, enabled: boolean): Promise<void> {
  return http.patch(`/alert/rules/${ruleId}`, { enabled })
}

/** 查询预警历史（分页） */
export function getAlertHistory(params: {
  page?: number
  size?: number
  severity?: string
  status?: string
  startDate?: string
  endDate?: string
}): Promise<PaginatedData<AlertHistoryItem>> {
  return http.get('/alert/history', { params })
}
