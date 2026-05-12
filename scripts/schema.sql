-- =============================================
-- 方舟 Code Plan 中转站系统 - 数据库初始化脚本
-- 版本: v1.1
-- 日期: 2026-05-11
-- 作者: WangMiao
-- 说明: 适用于 MySQL 8.0+
-- =============================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS ark_proxy
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE ark_proxy;

-- 删除外键约束（用于重新初始化）
SET FOREIGN_KEY_CHECKS = 0;

-- =============================================
-- 用户相关数据表
-- =============================================

-- users（用户基础信息表）
DROP TABLE IF EXISTS users;
CREATE TABLE users (
    user_id VARCHAR(64) PRIMARY KEY COMMENT '用户唯一标识（UUID）',
    username VARCHAR(128) NOT NULL COMMENT '用户名',
    email VARCHAR(256) NOT NULL COMMENT '邮箱（加密存储）',
    password_hash VARCHAR(256) NOT NULL COMMENT '密码哈希（bcrypt）',
    department_id VARCHAR(64) COMMENT '所属部门ID',
    role VARCHAR(32) NOT NULL DEFAULT 'user' COMMENT '角色（admin/user/guest）',
    status VARCHAR(16) NOT NULL DEFAULT 'active' COMMENT '状态（active/inactive）',
    last_login_at DATETIME COMMENT '最后登录时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_username (username),
    UNIQUE KEY uk_email (email),
    KEY idx_department_id (department_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户基础信息表';

-- user_quotas（用户配额表）
DROP TABLE IF EXISTS user_quotas;
CREATE TABLE user_quotas (
    quota_id VARCHAR(64) PRIMARY KEY COMMENT '配额记录ID（UUID）',
    user_id VARCHAR(64) NOT NULL COMMENT '用户ID',
    quota_type VARCHAR(32) NOT NULL COMMENT '配额类型（daily/monthly/yearly）',
    total_tokens BIGINT NOT NULL COMMENT '总 Token 配额',
    used_tokens BIGINT NOT NULL DEFAULT 0 COMMENT '已用 Token',
    total_requests INT NOT NULL DEFAULT 0 COMMENT '总请求次数配额',
    used_requests INT NOT NULL DEFAULT 0 COMMENT '已用请求次数',
    total_cost DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '总费用配额（元）',
    used_cost DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '已用费用（元）',
    reset_at DATETIME NOT NULL COMMENT '配额重置时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_user_id (user_id),
    KEY idx_reset_at (reset_at),
    FOREIGN KEY fk_quota_user_id (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户配额表';

-- =============================================
-- 用量相关数据表
-- =============================================

-- usage_logs（用量日志表 - 按月分区）
DROP TABLE IF EXISTS usage_logs;
CREATE TABLE usage_logs (
    log_id VARCHAR(64) PRIMARY KEY COMMENT '日志ID（UUID）',
    user_id VARCHAR(64) NOT NULL COMMENT '用户ID',
    team_id VARCHAR(64) COMMENT '团队ID',
    provider VARCHAR(32) NOT NULL COMMENT '厂商名称',
    model VARCHAR(64) NOT NULL COMMENT '使用的模型',
    feature VARCHAR(32) COMMENT '功能类型',
    input_tokens INT NOT NULL DEFAULT 0 COMMENT '输入 Token 数',
    output_tokens INT NOT NULL DEFAULT 0 COMMENT '输出 Token 数',
    total_tokens INT NOT NULL DEFAULT 0 COMMENT '总 Token 数',
    cost DECIMAL(10,4) NOT NULL DEFAULT 0.0000 COMMENT '费用（元）',
    request_count INT NOT NULL DEFAULT 1 COMMENT '请求次数（批量时>1）',
    status VARCHAR(16) NOT NULL COMMENT '状态（success/failed）',
    error_code VARCHAR(64) COMMENT '错误码',
    error_message TEXT COMMENT '错误信息',
    response_time_ms INT COMMENT '响应时间（毫秒）',
    timestamp DATETIME NOT NULL COMMENT '时间戳',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_timestamp (timestamp),
    KEY idx_user_id (user_id),
    KEY idx_team_id (team_id),
    KEY idx_provider (provider),
    KEY idx_timestamp_user (timestamp, user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用量日志表'
PARTITION BY RANGE (YEAR(timestamp) * 100 + MONTH(timestamp)) (
    PARTITION p202601 VALUES LESS THAN (202602),
    PARTITION p202602 VALUES LESS THAN (202603),
    PARTITION p202603 VALUES LESS THAN (202604),
    PARTITION p202604 VALUES LESS THAN (202605),
    PARTITION p202605 VALUES LESS THAN (202606),
    PARTITION p202606 VALUES LESS THAN (202607),
    PARTITION p202607 VALUES LESS THAN (202608),
    PARTITION p202608 VALUES LESS THAN (202609),
    PARTITION p202609 VALUES LESS THAN (202610),
    PARTITION p202610 VALUES LESS THAN (202611),
    PARTITION p202611 VALUES LESS THAN (202612),
    PARTITION p202612 VALUES LESS THAN (202701),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);

-- daily_usage_summary（日用量汇总表）
DROP TABLE IF EXISTS daily_usage_summary;
CREATE TABLE daily_usage_summary (
    summary_id VARCHAR(64) PRIMARY KEY COMMENT '汇总ID（UUID）',
    date DATE NOT NULL COMMENT '日期',
    user_id VARCHAR(64) NOT NULL COMMENT '用户ID',
    team_id VARCHAR(64) COMMENT '团队ID（可选）',
    total_requests INT NOT NULL DEFAULT 0 COMMENT '总请求次数',
    total_input_tokens BIGINT NOT NULL DEFAULT 0 COMMENT '总输入 Token 数',
    total_output_tokens BIGINT NOT NULL DEFAULT 0 COMMENT '总输出 Token 数',
    total_tokens BIGINT NOT NULL DEFAULT 0 COMMENT '总 Token 数',
    total_cost DECIMAL(10,2) NOT NULL DEFAULT 0.00 COMMENT '总费用（元）',
    avg_response_time DECIMAL(10,2) COMMENT '平均响应时间（毫秒）',
    success_count INT NOT NULL DEFAULT 0 COMMENT '成功请求次数',
    failed_count INT NOT NULL DEFAULT 0 COMMENT '失败请求次数',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_date_user (date, user_id),
    KEY idx_date (date),
    KEY idx_user_id (user_id),
    KEY idx_team_id (team_id),
    FOREIGN KEY fk_summary_user_id (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='日用量汇总表';

-- =============================================
-- 厂商管理相关数据表
-- =============================================

-- provider_config（厂商配置表）
DROP TABLE IF EXISTS provider_config;
CREATE TABLE provider_config (
    provider_id VARCHAR(64) PRIMARY KEY COMMENT '厂商唯一标识',
    provider_name VARCHAR(128) NOT NULL COMMENT '厂商显示名称',
    api_base_url VARCHAR(256) NOT NULL COMMENT 'API 基础 URL',
    auth_type VARCHAR(32) NOT NULL COMMENT '认证方式',
    auth_config JSON NOT NULL COMMENT '认证配置',
    protocol_type VARCHAR(32) NOT NULL COMMENT '协议类型',
    priority INT NOT NULL DEFAULT 1 COMMENT '优先级（数字越小优先级越高）',
    enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='厂商配置表';

-- model_mapping（模型映射表）
DROP TABLE IF EXISTS model_mapping;
CREATE TABLE model_mapping (
    mapping_id VARCHAR(64) PRIMARY KEY COMMENT '映射记录ID（UUID）',
    provider_id VARCHAR(64) NOT NULL COMMENT '厂商ID',
    standard_model_name VARCHAR(128) NOT NULL COMMENT '标准模型名称',
    vendor_model_name VARCHAR(128) NOT NULL COMMENT '厂商模型名称',
    context_window INT NOT NULL COMMENT '上下文窗口大小（Token 数）',
    max_output_tokens INT NOT NULL COMMENT '最大输出 Token 数',
    input_price_per_1k DECIMAL(10,6) NOT NULL COMMENT '输入 Token 单价（每 1K Token）',
    output_price_per_1k DECIMAL(10,6) NOT NULL COMMENT '输出 Token 单价（每 1K Token）',
    enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_provider_standard_model (provider_id, standard_model_name),
    FOREIGN KEY fk_mapping_provider_id (provider_id) REFERENCES provider_config(provider_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='模型映射表';

-- api_keys（API Key 池表）
DROP TABLE IF EXISTS api_keys;
CREATE TABLE api_keys (
    key_id VARCHAR(64) PRIMARY KEY COMMENT 'Key 记录ID（UUID）',
    provider_id VARCHAR(64) NOT NULL COMMENT '厂商ID',
    api_key_encrypted TEXT NOT NULL COMMENT 'API Key（加密存储）',
    key_alias VARCHAR(128) COMMENT 'Key 别名（便于管理）',
    rate_limit_rpm INT NOT NULL DEFAULT 1000 COMMENT '每分钟请求数限制',
    rate_limit_tpm INT NOT NULL DEFAULT 100000 COMMENT '每分钟 Token 数限制',
    used_rpm INT NOT NULL DEFAULT 0 COMMENT '当前分钟已用请求数',
    used_tpm INT NOT NULL DEFAULT 0 COMMENT '当前分钟已用 Token 数',
    status VARCHAR(16) NOT NULL DEFAULT 'active' COMMENT '状态（active/quota_exhausted/revoked）',
    last_used_at DATETIME COMMENT '最后使用时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_provider_id (provider_id),
    KEY idx_status (status),
    FOREIGN KEY fk_key_provider_id (provider_id) REFERENCES provider_config(provider_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='API Key 池表';

-- =============================================
-- 预警相关数据表
-- =============================================

-- alert_rules（预警规则表）
DROP TABLE IF EXISTS alert_rules;
CREATE TABLE alert_rules (
    rule_id VARCHAR(64) PRIMARY KEY COMMENT '规则ID（UUID）',
    rule_name VARCHAR(128) NOT NULL COMMENT '规则名称',
    rule_type VARCHAR(32) NOT NULL COMMENT '规则类型（quota/cost）',
    target_type VARCHAR(32) NOT NULL COMMENT '目标类型（user/team）',
    target_id VARCHAR(64) COMMENT '目标ID（为 NULL 时表示全局规则）',
    reminder_threshold DECIMAL(5,2) NOT NULL COMMENT '提醒阈值（%）',
    warning_threshold DECIMAL(5,2) NOT NULL COMMENT '警告阈值（%）',
    block_threshold DECIMAL(5,2) COMMENT '禁止阈值（%）',
    notification_channels JSON NOT NULL COMMENT '通知渠道配置',
    enabled BOOLEAN NOT NULL DEFAULT TRUE COMMENT '是否启用',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预警规则表';

-- alert_history（预警历史表）
DROP TABLE IF EXISTS alert_history;
CREATE TABLE alert_history (
    alert_id VARCHAR(64) PRIMARY KEY COMMENT '预警ID（UUID）',
    rule_id VARCHAR(64) NOT NULL COMMENT '规则ID',
    user_id VARCHAR(64) NOT NULL COMMENT '用户ID',
    alert_type VARCHAR(32) NOT NULL COMMENT '预警类型',
    severity VARCHAR(16) NOT NULL COMMENT '严重程度（info/warning/critical/emergency）',
    threshold_value DECIMAL(10,2) NOT NULL COMMENT '阈值',
    actual_value DECIMAL(10,2) NOT NULL COMMENT '实际值',
    message TEXT NOT NULL COMMENT '预警消息',
    notification_status VARCHAR(32) NOT NULL DEFAULT 'pending' COMMENT '通知状态（pending/sent/failed）',
    status VARCHAR(16) NOT NULL DEFAULT 'active' COMMENT '状态（active/resolved）',
    resolved_at DATETIME COMMENT '解决时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    KEY idx_user_id (user_id),
    KEY idx_status (status),
    KEY idx_created_at (created_at),
    FOREIGN KEY fk_alert_rule_id (rule_id) REFERENCES alert_rules(rule_id) ON DELETE CASCADE,
    FOREIGN KEY fk_alert_user_id (user_id) REFERENCES users(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='预警历史表';

-- 恢复外键约束
SET FOREIGN_KEY_CHECKS = 1;

-- 显示所有表
SHOW TABLES;
