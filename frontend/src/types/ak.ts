/**
 * AK 管理相关类型
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */

/** AK 申请请求 */
export interface AKApplyRequest {
  purpose: string
  providerId: string
  model: string
  estimatedUsage: string
  reason: string
}

/** AK 申请响应 */
export interface AKApplyResponse {
  applicationId: string
  status: string
}

/** AK 申请记录 */
export interface AKApplication {
  applicationId: string
  purpose: string
  providerId: string
  model: string
  status: 'pending' | 'approved' | 'rejected'
  applicantId: string
  applicantName: string
  createdAt: string
  updatedAt: string
  comment?: string
}

/** 我的 AK 信息 */
export interface MyAKInfo {
  ak: string
  sk: string
  quota: {
    totalTokens: number
    usedTokens: number
    resetAt: string
  }
  configGuide: string
  status: 'active' | 'quota_exhausted' | 'revoked'
}

/** AK 审批请求 */
export interface AKApproveRequest {
  applicationId: string
  action: 'approve' | 'reject'
  comment: string
}
