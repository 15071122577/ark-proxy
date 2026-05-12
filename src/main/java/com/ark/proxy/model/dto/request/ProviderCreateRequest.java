package com.ark.proxy.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 厂商创建请求 DTO
 *
 * <p>用于创建 AI 厂商配置的请求参数封装，包含厂商基本信息、认证方式和协议类型等。</p>
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProviderCreateRequest {

    /** 厂商唯一标识 */
    private String providerId;

    /** 厂商名称 */
    private String providerName;

    /** 厂商 API 基础地址 */
    private String apiBaseUrl;

    /** 认证类型（如 bearer、api-key 等） */
    private String authType;

    /** 认证配置（JSON 格式，包含密钥等信息） */
    private String authConfig;

    /** 协议类型（如 anthropic、openai 等） */
    private String protocolType;

    /** 优先级（数值越大优先级越高） */
    private Integer priority;
}
