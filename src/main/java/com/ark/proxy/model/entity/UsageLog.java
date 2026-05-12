package com.ark.proxy.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用量日志实体，对应 usage_logs 分区表
 * <p>记录每次 API 调用的详细信息，包括 Token 用量、费用、响应状态等，用于用量统计和监控</p>
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
@Table(name = "usage_logs")
public class UsageLog {

    /** 日志唯一标识 */
    @Id
    @Column(length = 64)
    private String logId;

    /** 发起请求的用户ID */
    @Column(nullable = false, length = 64, name = "user_id")
    private String userId;

    /** 所属团队ID */
    @Column(length = 64, name = "team_id")
    private String teamId;

    /** AI 厂商标识（如 anthropic、openai） */
    @Column(nullable = false, length = 32)
    private String provider;

    /** 调用的模型名称 */
    @Column(nullable = false, length = 64)
    private String model;

    /** 功能特性标识（如 code_completion、chat） */
    @Column(length = 32)
    private String feature;

    /** 输入 Token 数量 */
    @Column(nullable = false, name = "input_tokens")
    private Integer inputTokens;

    /** 输出 Token 数量 */
    @Column(nullable = false, name = "output_tokens")
    private Integer outputTokens;

    /** 总 Token 数量（输入+输出） */
    @Column(nullable = false, name = "total_tokens")
    private Integer totalTokens;

    /** 本次调用费用 */
    @Column(nullable = false, precision = 10, scale = 4)
    private BigDecimal cost;

    /** 本次请求计数 */
    @Column(nullable = false, name = "request_count")
    private Integer requestCount;

    /** 请求状态（如 success、failed） */
    @Column(nullable = false, length = 16)
    private String status;

    /** 错误码（请求失败时记录） */
    @Column(length = 64, name = "error_code")
    private String errorCode;

    /** 错误信息详情（请求失败时记录） */
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    /** 响应时间（毫秒） */
    @Column(name = "response_time_ms")
    private Integer responseTimeMs;

    /** 请求发生时间 */
    @Column(nullable = false)
    private LocalDateTime timestamp;

    /** 记录创建时间，不可更新 */
    @Column(nullable = false, name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /**
     * JPA 持久化前回调：初始化创建时间，以及 Token、费用、请求次数的默认值
     */
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.inputTokens == null) this.inputTokens = 0;
        if (this.outputTokens == null) this.outputTokens = 0;
        if (this.totalTokens == null) this.totalTokens = 0;
        if (this.cost == null) this.cost = BigDecimal.ZERO;
        if (this.requestCount == null) this.requestCount = 1;
    }
}
