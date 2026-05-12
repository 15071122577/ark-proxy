/**
 * AK 管理 API：申请、查询、审批
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
import http from '@/utils/http'
import type { AKApplyRequest, AKApplyResponse, AKApplication, MyAKInfo, AKApproveRequest } from '@/types/ak'

/** 提交 AK 申请 */
export function applyAK(data: AKApplyRequest): Promise<AKApplyResponse> {
  return http.post('/ak/apply', data)
}

/** 查询我的申请列表 */
export function getMyApplications(): Promise<AKApplication[]> {
  return http.get('/ak/applications')
}

/** 查询申请详情 */
export function getApplicationDetail(applicationId: string): Promise<AKApplication> {
  return http.get(`/ak/applications/${applicationId}`)
}

/** 查看我的 AK */
export function getMyAK(): Promise<MyAKInfo> {
  return http.get('/ak/my')
}

/** 获取待审批列表（管理员） */
export function getPendingApplications(): Promise<AKApplication[]> {
  return http.get('/ak/pending')
}

/** 审批 AK 申请（管理员） */
export function approveAK(data: AKApproveRequest): Promise<void> {
  return http.post('/ak/approve', data)
}
