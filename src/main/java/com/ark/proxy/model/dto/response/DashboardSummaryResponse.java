package com.ark.proxy.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 看板汇总响应 DTO
 *
 * <p>看板页面汇总数据响应，包含请求量、Token 用量、费用、配额使用率和请求成功率等统计指标。</p>
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardSummaryResponse {

    /** 当日请求总数 */
    private long dailyRequests;

    /** 当月请求总数 */
    private long monthlyRequests;

    /** 当日消耗 Token 总数 */
    private long dailyTokens;

    /** 当月消耗 Token 总数 */
    private long monthlyTokens;

    /** 当日费用（单位：美元） */
    private BigDecimal dailyCost;

    /** 当月费用（单位：美元） */
    private BigDecimal monthlyCost;

    /** 配额使用率（百分比，如 75.5 表示 75.5%） */
    private BigDecimal quotaUsageRate;

    /** 请求成功率（0.0 ~ 1.0） */
    private double requestSuccessRate;
}
