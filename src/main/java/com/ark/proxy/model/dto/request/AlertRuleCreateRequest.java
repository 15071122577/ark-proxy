package com.ark.proxy.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 预警规则创建请求 DTO
 *
 * <p>用于创建配额预警规则的请求参数封装，支持多级阈值（提醒/警告/阻断）和多种通知渠道。</p>
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertRuleCreateRequest {

    /** 规则名称 */
    private String ruleName;

    /** 规则类型（如 quota-usage、error-rate 等） */
    private String ruleType;

    /** 监控目标类型（如 user、team、system 等） */
    private String targetType;

    /** 监控目标ID */
    private String targetId;

    /** 提醒阈值（百分比，如 70.0 表示 70%） */
    private BigDecimal reminderThreshold;

    /** 警告阈值（百分比，如 90.0 表示 90%） */
    private BigDecimal warningThreshold;

    /** 阻断阈值（百分比，如 100.0 表示 100%，达到后自动阻断请求） */
    private BigDecimal blockThreshold;

    /** 通知渠道（逗号分隔，如 email,webhook,slack） */
    private String notificationChannels;
}
