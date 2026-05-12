/**
 * 厂商管理 API：厂商配置、模型映射、健康检查
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
import http from '@/utils/http'
import type { ProviderInfo, ProviderCreateRequest, ModelMapping, ProviderKeyCreateRequest } from '@/types/provider'
import type { VendorHealth } from '@/types/dashboard'

/** 获取厂商列表 */
export function getProviders(): Promise<ProviderInfo[]> {
  return http.get('/provider/list')
}

/** 创建厂商 */
export function createProvider(data: ProviderCreateRequest): Promise<ProviderInfo> {
  return http.post('/provider', data)
}

/** 更新厂商配置 */
export function updateProvider(providerId: string, data: Partial<ProviderCreateRequest>): Promise<ProviderInfo> {
  return http.put(`/provider/${providerId}`, data)
}

/** 启用/禁用厂商 */
export function toggleProvider(providerId: string, enabled: boolean): Promise<void> {
  return http.patch(`/provider/${providerId}`, { enabled })
}

/** 获取模型映射列表 */
export function getModelMappings(providerId?: string): Promise<ModelMapping[]> {
  return http.get('/provider/models', { params: { providerId } })
}

/** 创建模型映射 */
export function createModelMapping(data: Omit<ModelMapping, 'mappingId' | 'enabled'>): Promise<ModelMapping> {
  return http.post('/provider/models', data)
}

/** 添加 API Key 到厂商 */
export function addProviderKey(providerId: string, data: ProviderKeyCreateRequest): Promise<void> {
  return http.post(`/provider/${providerId}/keys`, data)
}

/** 获取厂商健康状态 */
export function getProviderHealth(): Promise<VendorHealth[]> {
  return http.get('/provider/health')
}
