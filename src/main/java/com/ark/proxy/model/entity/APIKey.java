package com.ark.proxy.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * API Key 池实体，对应 api_keys 表
 * <p>管理厂商 API Key 的加密存储和限流参数，支持多 Key 轮换和负载均衡</p>
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
@Table(name = "api_keys")
public class APIKey {

    /** API Key 唯一标识 */
    @Id
    @Column(length = 64)
    private String keyId;

    /** 所属厂商ID */
    @Column(nullable = false, length = 64, name = "provider_id")
    private String providerId;

    /** 加密后的 API Key 值 */
    @Column(nullable = false, name = "api_key_encrypted", columnDefinition = "TEXT")
    private String apiKeyEncrypted;

    /** Key 别名（便于辨识） */
    @Column(length = 128, name = "key_alias")
    private String keyAlias;

    /** 限流：每分钟最大请求数（RPM） */
    @Column(nullable = false, name = "rate_limit_rpm")
    private Integer rateLimitRpm;

    /** 限流：每分钟最大 Token 数（TPM） */
    @Column(nullable = false, name = "rate_limit_tpm")
    private Integer rateLimitTpm;

    /** 已用 RPM（当前分钟） */
    @Column(nullable = false, name = "used_rpm")
    private Integer usedRpm;

    /** 已用 TPM（当前分钟） */
    @Column(nullable = false, name = "used_tpm")
    private Integer usedTpm;

    /** Key 状态（如 active、disabled、expired） */
    @Column(nullable = false, length = 16)
    private String status;

    /** 最后使用时间 */
    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    /** 创建时间，不可更新 */
    @Column(nullable = false, name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @Column(nullable = false, name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * JPA 持久化前回调：初始化创建时间、更新时间，以及限流参数和状态的默认值
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.rateLimitRpm == null) this.rateLimitRpm = 1000;
        if (this.rateLimitTpm == null) this.rateLimitTpm = 100000;
        if (this.usedRpm == null) this.usedRpm = 0;
        if (this.usedTpm == null) this.usedTpm = 0;
        if (this.status == null) this.status = "active";
    }

    /**
     * JPA 更新前回调：自动刷新更新时间
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
