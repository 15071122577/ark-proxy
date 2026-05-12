/**
 * 预警相关类型
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */

/** 预警规则 */
export interface AlertRule {
  ruleId: string
  ruleName: string
  ruleType: string
  targetType: string
  targetId: string | null
  reminderThreshold: number
  warningThreshold: number
  blockThreshold: number | null
  notificationChannels: string
  enabled: boolean
  createdAt: string
  updatedAt: string
}

/** 预警规则创建请求 */
export interface AlertRuleCreateRequest {
  ruleName: string
  ruleType: string
  targetType: string
  targetId: string
  reminderThreshold: number
  warningThreshold: number
  blockThreshold: number
  notificationChannels: string
}

/** 预警历史记录 */
export interface AlertHistoryItem {
  alertId: string
  ruleId: string
  userId: string
  alertType: string
  severity: 'info' | 'warning' | 'critical' | 'emergency'
  thresholdValue: number
  actualValue: number
  message: string
  notificationStatus: 'pending' | 'sent' | 'failed'
  status: 'active' | 'resolved'
  resolvedAt: string | null
  createdAt: string
}
