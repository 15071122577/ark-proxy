package com.ark.proxy.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * AK 申请请求 DTO
 *
 * <p>用于用户提交 AK（Access Key）申请的请求参数封装，包含使用目的、厂商、模型、预估用量和申请理由。</p>
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AKApplyRequest {

    /** 使用目的 */
    private String purpose;

    /** 厂商ID */
    private String providerId;

    /** 申请使用的模型名称 */
    private String model;

    /** 预估用量 */
    private String estimatedUsage;

    /** 申请理由 */
    private String reason;
}
