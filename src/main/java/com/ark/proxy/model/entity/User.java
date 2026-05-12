package com.ark.proxy.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户基础信息实体，对应 users 表
 * <p>存储系统用户的基本信息，包括认证、角色、状态等</p>
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
@Table(name = "users")
public class User {

    /** 用户唯一标识 */
    @Id
    @Column(length = 64)
    private String userId;

    /** 用户名，唯一 */
    @Column(nullable = false, length = 128, unique = true)
    private String username;

    /** 邮箱地址，唯一 */
    @Column(nullable = false, length = 256, unique = true)
    private String email;

    /** 密码哈希值 */
    @Column(nullable = false, length = 256, name = "password_hash")
    private String passwordHash;

    /** 所属部门ID */
    @Column(length = 64, name = "department_id")
    private String departmentId;

    /** 用户角色（如 admin、user、team_leader） */
    @Column(nullable = false, length = 32)
    private String role;

    /** 用户状态（如 active、disabled） */
    @Column(nullable = false, length = 16)
    private String status;

    /** 最后登录时间 */
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

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
