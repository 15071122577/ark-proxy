package com.ark.proxy.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 厂商配置实体，对应 provider_config 表
 * <p>管理 AI 厂商的连接配置，包括 API 地址、认证方式、协议类型和优先级等</p>
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
@Table(name = "provider_config")
public class ProviderConfig {

    /** 厂商唯一标识 */
    @Id
    @Column(length = 64)
    private String providerId;

    /** 厂商名称 */
    @Column(nullable = false, length = 128, name = "provider_name")
    private String providerName;

    /** API 基础地址 */
    @Column(nullable = false, length = 256, name = "api_base_url")
    private String apiBaseUrl;

    /** 认证类型（如 api_key、oauth） */
    @Column(nullable = false, length = 32, name = "auth_type")
    private String authType;

    /** 认证配置（JSON 格式，存储认证参数） */
    @Column(nullable = false, name = "auth_config", columnDefinition = "JSON")
    private String authConfig;

    /** 协议类型（如 anthropic、openai） */
    @Column(nullable = false, length = 32, name = "protocol_type")
    private String protocolType;

    /** 优先级（数值越小优先级越高） */
    @Column(nullable = false)
    private Integer priority;

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
     * JPA 持久化前回调：初始化创建时间、更新时间，以及优先级和启用状态的默认值
     */
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.priority == null) this.priority = 1;
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
