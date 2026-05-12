package com.ark.proxy.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 模型映射实体，对应 model_mapping 表
 * <p>管理标准模型名称到厂商模型名称的映射关系，包含上下文窗口、最大输出 Token 和单价信息</p>
 * <p>唯一约束：同一厂商下标准模型名称唯一（uk_provider_standard_model）</p>
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
@Table(name = "model_mapping",
        uniqueConstraints = @UniqueConstraint(name = "uk_provider_standard_model",
                columnNames = {"providerId", "standardModelName"}))
public class ModelMapping {

    /** 映射唯一标识 */
    @Id
    @Column(length = 64)
    private String mappingId;

    /** 所属厂商ID */
    @Column(nullable = false, length = 64, name = "provider_id")
    private String providerId;

    /** 标准模型名称（系统统一的模型名称） */
    @Column(nullable = false, length = 128, name = "standard_model_name")
    private String standardModelName;

    /** 厂商模型名称（厂商实际的模型标识） */
    @Column(nullable = false, length = 128, name = "vendor_model_name")
    private String vendorModelName;

    /** 上下文窗口大小（Token 数） */
    @Column(nullable = false, name = "context_window")
    private Integer contextWindow;

    /** 最大输出 Token 数 */
    @Column(nullable = false, name = "max_output_tokens")
    private Integer maxOutputTokens;

    /** 输入单价（每千 Token 价格） */
    @Column(nullable = false, precision = 10, scale = 6, name = "input_price_per_1k")
    private BigDecimal inputPricePer1k;

    /** 输出单价（每千 Token 价格） */
    @Column(nullable = false, precision = 10, scale = 6, name = "output_price_per_1k")
    private BigDecimal outputPricePer1k;

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
