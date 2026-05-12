/**
 * 厂商管理相关类型
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */

/** 厂商信息 */
export interface ProviderInfo {
  providerId: string
  providerName: string
  apiBaseUrl: string
  authType: string
  authConfig: string
  protocolType: string
  priority: number
  enabled: boolean
}

/** 厂商创建请求 */
export interface ProviderCreateRequest {
  providerId: string
  providerName: string
  apiBaseUrl: string
  authType: string
  authConfig: string
  protocolType: string
  priority: number
}

/** 模型映射 */
export interface ModelMapping {
  mappingId: string
  providerId: string
  standardModelName: string
  vendorModelName: string
  contextWindow: number
  maxOutputTokens: number
  enabled: boolean
}

/** API Key 创建请求 */
export interface ProviderKeyCreateRequest {
  apiKey: string
  keyAlias: string
  rateLimitRpm: number
  rateLimitTpm: number
}
