package com.ark.proxy.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 预警规则实体，对应 alert_rules 表
 * <p>定义用量预警的规则配置，支持多级阈值（提醒、警告、阻断）和多通知渠道</p>
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
@Table(name = "alert_rules")
public class AlertRule {

    /** 规则唯一标识 */
    @Id
    @Column(length = 64)
    private String ruleId;

    /** 规则名称 */
    @Column(nullable = false, length = 128, name = "rule_name")
    private String ruleName;

    /** 规则类型（如 quota_usage、rate_limit、cost） */
    @Column(nullable = false, length = 32, name = "rule_type")
    private String ruleType;

    /** 目标类型（如 user、team、system） */
    @Column(nullable = false, length = 32, name = "target_type")
    private String targetType;

    /** 目标ID（为空时表示该类型下所有目标） */
    @Column(length = 64, name = "target_id")
    private String targetId;

    /** 提醒阈值（百分比，如 70.00 表示 70%） */
    @Column(nullable = false, precision = 5, scale = 2, name = "reminder_threshold")
    private BigDecimal reminderThreshold;

    /** 警告阈值（百分比，如 90.00 表示 90%） */
    @Column(nullable = false, precision = 5, scale = 2, name = "warning_threshold")
    private BigDecimal warningThreshold;

    /** 阻断阈值（百分比，如 100.00 表示 100%，达到此阈值将阻断请求） */
    @Column(precision = 5, scale = 2, name = "block_threshold")
    private BigDecimal blockThreshold;

    /** 通知渠道配置（JSON 格式，如 email、webhook、lark） */
    @Column(nullable = false, name = "notification_channels", columnDefinition = "JSON")
    private String notificationChannels;

    /** 是否启用 */
    @Column(nullable = false)
    private Boolean enabled;

    /** 创建时间，不可更新 */
    @Column(nullable = false, name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * JPA 持久化前回调：初始化创建时间、更新时间，以及启用状态的默认值
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.enabled == null) this.enabled = true;
    }

    /**
     * JPA 更新前回调：自动刷新更新时间
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
