package com.ark.proxy.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户配额实体，对应 user_quotas 表
 * <p>管理用户的 Token 配额、请求次数配额和费用配额，支持配额使用率计算</p>
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
@Table(name = "user_quotas")
public class UserQuota {

    /** 配额唯一标识 */
    @Id
    @Column(length = 64)
    private String quotaId;

    /** 用户ID，唯一关联用户 */
    @Column(nullable = false, length = 64, unique = true, name = "user_id")
    private String userId;

    /** 配额类型（如 daily、monthly） */
    @Column(nullable = false, length = 32, name = "quota_type")
    private String quotaType;

    /** Token 总配额 */
    @Column(nullable = false, name = "total_tokens")
    private Long totalTokens;

    /** 已使用 Token 数量 */
    @Column(nullable = false, name = "used_tokens")
    private Long usedTokens;

    /** 请求次数总配额 */
    @Column(nullable = false, name = "total_requests")
    private Integer totalRequests;

    /** 已使用请求次数 */
    @Column(nullable = false, name = "used_requests")
    private Integer usedRequests;

    /** 费用总配额 */
    @Column(nullable = false, precision = 10, scale = 2, name = "total_cost")
    private BigDecimal totalCost;

    /** 已使用费用 */
    @Column(nullable = false, precision = 10, scale = 2, name = "used_cost")
    private BigDecimal usedCost;

    /** 配额重置时间 */
    @Column(nullable = false, name = "reset_at")
    private LocalDateTime resetAt;

    /** 创建时间，不可更新 */
    @Column(nullable = false, name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * JPA 持久化前回调：初始化创建时间、更新时间，以及已用配额默认值
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.usedTokens == null) this.usedTokens = 0L;
        if (this.usedRequests == null) this.usedRequests = 0;
        if (this.usedCost == null) this.usedCost = BigDecimal.ZERO;
    }

    /**
     * JPA 更新前回调：自动刷新更新时间
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 计算配额使用率（百分比）
     *
     * @return 配额使用率，保留4位小数，如 75.5000 表示 75.5%；若总配额为空或零则返回 0
     */
    public BigDecimal getUsageRate() {
        if (totalTokens == null || totalTokens == 0) return BigDecimal.ZERO;
        return new BigDecimal(usedTokens).divide(new BigDecimal(totalTokens), 4, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal(100));
    }
}
