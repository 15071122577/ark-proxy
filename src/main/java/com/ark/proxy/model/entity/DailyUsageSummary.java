package com.ark.proxy.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 日用量汇总实体，对应 daily_usage_summary 表
 * <p>按天汇总用户/团队的 API 用量数据，包括请求数、Token 数、费用和成功率等</p>
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
@Table(name = "daily_usage_summary")
public class DailyUsageSummary {

    /** 汇总记录唯一标识 */
    @Id
    @Column(length = 64)
    private String summaryId;

    /** 汇总日期 */
    @Column(nullable = false)
    private LocalDate date;

    /** 用户ID */
    @Column(nullable = false, length = 64, name = "user_id")
    private String userId;

    /** 团队ID */
    @Column(length = 64, name = "team_id")
    private String teamId;

    /** 当日总请求数 */
    @Column(nullable = false, name = "total_requests")
    private Integer totalRequests;

    /** 当日总输入 Token 数 */
    @Column(nullable = false, name = "total_input_tokens")
    private Long totalInputTokens;

    /** 当日总输出 Token 数 */
    @Column(nullable = false, name = "total_output_tokens")
    private Long totalOutputTokens;

    /** 当日总 Token 数（输入+输出） */
    @Column(nullable = false, name = "total_tokens")
    private Long totalTokens;

    /** 当日总费用 */
    @Column(nullable = false, precision = 10, scale = 2, name = "total_cost")
    private BigDecimal totalCost;

    /** 当日平均响应时间（毫秒） */
    @Column(precision = 10, scale = 2, name = "avg_response_time")
    private BigDecimal avgResponseTime;

    /** 当日成功请求数 */
    @Column(nullable = false, name = "success_count")
    private Integer successCount;

    /** 当日失败请求数 */
    @Column(nullable = false, name = "failed_count")
    private Integer failedCount;

    /** 创建时间，不可更新 */
    @Column(nullable = false, name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * JPA 持久化前回调：初始化创建时间和更新时间
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    /**
     * JPA 更新前回调：自动刷新更新时间
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
