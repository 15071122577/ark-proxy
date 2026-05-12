package com.ark.proxy.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 预警历史实体，对应 alert_history 表
 * <p>记录每次预警触发的详细信息，包括阈值、实际值、严重级别和通知状态等</p>
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "alert_history")
public class AlertHistory {

    /** 预警记录唯一标识 */
    @Id
    @Column(length = 64)
    private String alertId;

    /** 触发的预警规则ID */
    @Column(nullable = false, length = 64, name = "rule_id")
    private String ruleId;

    /** 相关用户ID */
    @Column(nullable = false, length = 64, name = "user_id")
    private String userId;

    /** 预警类型（如 quota_usage、rate_limit、cost） */
    @Column(nullable = false, length = 32, name = "alert_type")
    private String alertType;

    /** 严重级别（如 reminder、warning、block） */
    @Column(nullable = false, length = 16)
    private String severity;

    /** 触发阈值 */
    @Column(nullable = false, precision = 10, scale = 2, name = "threshold_value")
    private BigDecimal thresholdValue;

    /** 实际触发值 */
    @Column(nullable = false, precision = 10, scale = 2, name = "actual_value")
    private BigDecimal actualValue;

    /** 预警消息内容 */
    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;

    /** 通知状态（如 pending、sent、failed） */
    @Column(nullable = false, length = 32, name = "notification_status")
    private String notificationStatus;

    /** 预警状态（如 active、resolved） */
    @Column(nullable = false, length = 16)
    private String status;

    /** 预警解决时间 */
    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    /** 创建时间，不可更新 */
    @Column(nullable = false, name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * JPA 持久化前回调：初始化创建时间，以及通知状态和预警状态的默认值
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.notificationStatus == null) this.notificationStatus = "pending";
        if (this.status == null) this.status = "active";
    }
}
