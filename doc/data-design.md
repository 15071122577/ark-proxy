# 方舟 Code Plan 中转站系统 - 数据设计文档

**版本**: v1.0  
**日期**: 2026-05-11  
**作者**: 数析（Metric）- 数据分析师  
**状态**: 待评审  
**对应 PRD**: prd-ark-code-plan-proxy-2026-05-11.md (v1.2)

---

## 目录

1. [数据库选型](#1-数据库选型)
2. [核心数据表设计](#2-核心数据表设计)
3. [数据关系图](#3-数据关系图)
4. [索引设计](#4-索引设计)
5. [分区策略](#5-分区策略)
6. [数据归档策略](#6-数据归档策略)
7. [性能优化建议](#7-性能优化建议)

---

## 1. 数据库选型

### 1.1 数据存储架构

| 数据类型 | 存储方案 | 用途 | 保留期限 |
|---------|---------|------|---------|
| 实时指标（1秒精度） | InfluxDB 2.x | 实时监控、告警触发 | 24 小时 |
| 短期数据（1小时精度） | InfluxDB 2.x | 日常监控、用量查询 | 90 天 |
| 日汇总数据 | MySQL 8.0 | 趋势分析、月报生成 | 3 年 |
| 用量日志明细 | MySQL 8.0 | 详细查询、故障排查 | 90 天 |
| 用户/配置数据 | MySQL 8.0 | 业务数据 | 永久 |
| 预警历史 | MySQL 8.0 | 预警查询、统计分析 | 1 年 |
| 审计日志 | 对象存储（本地文件） | 合规审计 | 永久 |

### 1.2 MySQL 配置

```ini
# my.cnf 关键配置
[mysql]
innodb_buffer_pool_size = 4G
innodb_log_file_size = 512M
innodb_flush_log_at_trx_commit = 2
max_connections = 500
query_cache_size = 0  # 禁用查询缓存（使用 Redis）
```

---

## 2. 核心数据表设计

### 2.1 用户相关数据

#### users（用户基础信息表）

| 字段名 | 数据类型 | 约束 | 说明 | 示例值 |
|--------|---------|------|------|---------|
| user_id | VARCHAR(64) | PK | 用户唯一标识（UUID） | "user_001" |
| username | VARCHAR(128) | NOT NULL, UNIQUE | 用户名 | "张三" |
| email | VARCHAR(256) | NOT NULL, UNIQUE | 邮箱（加密存储） | "encrypted:xxx" |
| password_hash | VARCHAR(256) | NOT NULL | 密码哈希（bcrypt） | "$2a$12$..." |
| department_id | VARCHAR(64) | FK | 所属部门ID | "dept_001" |
| role | VARCHAR(32) | NOT NULL | 角色（admin/user/guest） | "user" |
| status | VARCHAR(16) | NOT NULL | 状态（active/inactive） | "active" |
| last_login_at | DATETIME | NULL | 最后登录时间 | "2026-05-11 10:00:00" |
| created_at | DATETIME | NOT NULL | 创建时间 | "2026-05-11 10:00:00" |
| updated_at | DATETIME | NOT NULL | 更新时间 | "2026-05-11 10:00:00" |

**索引**:

```sql
CREATE INDEX idx_users_department_id ON users(department_id);
CREATE INDEX idx_users_status ON users(status);
```

#### user_quotas（用户配额表）

| 字段名 | 数据类型 | 约束 | 说明 | 示例值 |
|--------|---------|------|------|---------|
| quota_id | VARCHAR(64) | PK | 配额记录ID（UUID） | "quota_001" |
| user_id | VARCHAR(64) | FK, UNIQUE | 用户ID | "user_001" |
| quota_type | VARCHAR(32) | NOT NULL | 配额类型（daily/monthly/yearly） | "monthly" |
| total_tokens | BIGINT | NOT NULL | 总 Token 配额 | 1000000 |
| used_tokens | BIGINT | NOT NULL DEFAULT 0 | 已用 Token | 750000 |
| total_requests | INT | NOT NULL | 总请求次数配额 | 10000 |
| used_requests | INT | NOT NULL DEFAULT 0 | 已用请求次数 | 7500 |
| total_cost | DECIMAL(10,2) | NOT NULL | 总费用配额（元） | 1000.00 |
| used_cost | DECIMAL(10,2) | NOT NULL DEFAULT 0.00 | 已用费用（元） | 750.00 |
| reset_at | DATETIME | NOT NULL | 配额重置时间 | "2026-06-01 00:00:00" |
| created_at | DATETIME | NOT NULL | 创建时间 | "2026-05-11 10:00:00" |
| updated_at | DATETIME | NOT NULL | 更新时间 | "2026-05-11 10:00:00" |

**索引**:

```sql
CREATE INDEX idx_user_quotas_user_id ON user_quotas(user_id);
CREATE INDEX idx_user_quotas_reset_at ON user_quotas(reset_at);
```

### 2.2 用量相关数据

#### usage_logs（用量日志表 - 分区表）

| 字段名 | 数据类型 | 约束 | 说明 | 示例值 |
|--------|---------|------|------|---------|
| log_id | VARCHAR(64) | PK | 日志ID（UUID） | "log_20240511_001" |
| user_id | VARCHAR(64) | FK | 用户ID | "user_001" |
| team_id | VARCHAR(64) | FK | 团队ID | "team_001" |
| provider | VARCHAR(32) | NOT NULL | 厂商名称 | "anthropic" |
| model | VARCHAR(64) | NOT NULL | 使用的模型 | "claude-3-opus" |
| feature | VARCHAR(32) | NULL | 功能类型 | "code_completion" |
| input_tokens | INT | NOT NULL DEFAULT 0 | 输入 Token 数 | 500 |
| output_tokens | INT | NOT NULL DEFAULT 0 | 输出 Token 数 | 200 |
| total_tokens | INT | NOT NULL DEFAULT 0 | 总 Token 数 | 700 |
| cost | DECIMAL(10,4) | NOT NULL DEFAULT 0.0000 | 费用（元） | 0.0210 |
| request_count | INT | NOT NULL DEFAULT 1 | 请求次数（批量时>1） | 1 |
| status | VARCHAR(16) | NOT NULL | 状态（success/failed） | "success" |
| error_code | VARCHAR(64) | NULL | 错误码 | null |
| error_message | TEXT | NULL | 错误信息 | null |
| response_time_ms | INT | NULL | 响应时间（毫秒） | 1500 |
| timestamp | DATETIME | NOT NULL | 时间戳 | "2026-05-11 10:00:00" |
| created_at | DATETIME | NOT NULL | 创建时间 | "2026-05-11 10:00:00" |

**索引**:

```sql
CREATE INDEX idx_usage_logs_timestamp ON usage_logs(timestamp);
CREATE INDEX idx_usage_logs_user_id ON usage_logs(user_id);
CREATE INDEX idx_usage_logs_team_id ON usage_logs(team_id);
CREATE INDEX idx_usage_logs_provider ON usage_logs(provider);
CREATE INDEX idx_usage_logs_timestamp_user ON usage_logs(timestamp, user_id);
```

**分区策略**（见第5节）:

```sql
ALTER TABLE usage_logs 
PARTITION BY RANGE (YEAR(timestamp) * 100 + MONTH(timestamp)) (
    PARTITION p202401 VALUES LESS THAN (202402),
    PARTITION p202402 VALUES LESS THAN (202403),
    ...
    PARTITION p_future VALUES LESS THAN MAXVALUE
);
```

#### daily_usage_summary（日用量汇总表）

| 字段名 | 数据类型 | 约束 | 说明 | 示例值 |
|--------|---------|------|------|---------|
| summary_id | VARCHAR(64) | PK | 汇总ID（UUID） | "daily_20240511_user001" |
| date | DATE | NOT NULL | 日期 | "2026-05-11" |
| user_id | VARCHAR(64) | FK | 用户ID | "user_001" |
| team_id | VARCHAR(64) | FK | 团队ID（可选） | "team_001" |
| total_requests | INT | NOT NULL DEFAULT 0 | 总请求次数 | 150 |
| total_input_tokens | BIGINT | NOT NULL DEFAULT 0 | 总输入 Token 数 | 75000 |
| total_output_tokens | BIGINT | NOT NULL DEFAULT 0 | 总输出 Token 数 | 30000 |
| total_tokens | BIGINT | NOT NULL DEFAULT 0 | 总 Token 数 | 105000 |
| total_cost | DECIMAL(10,2) | NOT NULL DEFAULT 0.00 | 总费用（元） | 3.15 |
| avg_response_time | DECIMAL(10,2) | NULL | 平均响应时间（毫秒） | 1250.50 |
| success_count | INT | NOT NULL DEFAULT 0 | 成功请求次数 | 148 |
| failed_count | INT | NOT NULL DEFAULT 0 | 失败请求次数 | 2 |
| created_at | DATETIME | NOT NULL | 创建时间 | "2026-05-11 23:59:59" |
| updated_at | DATETIME | NOT NULL | 更新时间 | "2026-05-11 23:59:59" |

**索引**:

```sql
CREATE INDEX idx_daily_summary_date ON daily_usage_summary(date);
CREATE INDEX idx_daily_summary_user_id ON daily_usage_summary(user_id);
CREATE INDEX idx_daily_summary_team_id ON daily_usage_summary(team_id);
```

### 2.3 厂商管理相关数据

#### provider_config（厂商配置表）

| 字段名 | 数据类型 | 约束 | 说明 | 示例值 |
|--------|---------|------|------|---------|
| provider_id | VARCHAR(64) | PK | 厂商唯一标识 | "anthropic" |
| provider_name | VARCHAR(128) | NOT NULL | 厂商显示名称 | "Anthropic (Claude)" |
| api_base_url | VARCHAR(256) | NOT NULL | API 基础 URL | "https://api.anthropic.com/v1" |
| auth_type | VARCHAR(32) | NOT NULL | 认证方式 | "bearer_token" |
| auth_config | JSON | NOT NULL | 认证配置 | `{"header": "x-api-key"}` |
| protocol_type | VARCHAR(32) | NOT NULL | 协议类型 | "anthropic_messages" |
| priority | INT | NOT NULL DEFAULT 1 | 优先级（数字越小优先级越高） | 1 |
| enabled | BOOLEAN | NOT NULL DEFAULT TRUE | 是否启用 | true |
| created_at | DATETIME | NOT NULL | 创建时间 | "2026-05-11 10:00:00" |
| updated_at | DATETIME | NOT NULL | 更新时间 | "2026-05-11 10:00:00" |

#### model_mapping（模型映射表）

| 字段名 | 数据类型 | 约束 | 说明 | 示例值 |
|--------|---------|------|------|---------|
| mapping_id | VARCHAR(64) | PK | 映射记录ID（UUID） | "map_001" |
| provider_id | VARCHAR(64) | FK | 厂商ID | "anthropic" |
| standard_model_name | VARCHAR(128) | NOT NULL | 标准模型名称 | "claude-3-opus" |
| vendor_model_name | VARCHAR(128) | NOT NULL | 厂商模型名称 | "claude-3-opus-20240229" |
| context_window | INT | NOT NULL | 上下文窗口大小（Token 数） | 200000 |
| max_output_tokens | INT | NOT NULL | 最大输出 Token 数 | 4096 |
| input_price_per_1k | DECIMAL(10,6) | NOT NULL | 输入 Token 单价（每 1K Token） | 0.0150 |
| output_price_per_1k | DECIMAL(10,6) | NOT NULL | 输出 Token 单价（每 1K Token） | 0.0750 |
| enabled | BOOLEAN | NOT NULL DEFAULT TRUE | 是否启用 | true |
| created_at | DATETIME | NOT NULL | 创建时间 | "2026-05-11 10:00:00" |
| updated_at | DATETIME | NOT NULL | 更新时间 | "2026-05-11 10:00:00" |

**唯一约束**:

```sql
ALTER TABLE model_mapping ADD CONSTRAINT uk_provider_standard_model UNIQUE (provider_id, standard_model_name);
```

#### api_keys（API Key 池表）

| 字段名 | 数据类型 | 约束 | 说明 | 示例值 |
|--------|---------|------|------|---------|
| key_id | VARCHAR(64) | PK | Key 记录ID（UUID） | "key_001" |
| provider_id | VARCHAR(64) | FK | 厂商ID | "anthropic" |
| api_key_encrypted | TEXT | NOT NULL | API Key（加密存储） | "enc:xxxxxxxxxxxx" |
| key_alias | VARCHAR(128) | NULL | Key 别名（便于管理） | "团队主 Key" |
| rate_limit_rpm | INT | NOT NULL DEFAULT 1000 | 每分钟请求数限制 | 1000 |
| rate_limit_tpm | INT | NOT NULL DEFAULT 100000 | 每分钟 Token 数限制 | 100000 |
| used_rpm | INT | NOT NULL DEFAULT 0 | 当前分钟已用请求数 | 150 |
| used_tpm | INT | NOT NULL DEFAULT 0 | 当前分钟已用 Token 数 | 15000 |
| status | VARCHAR(16) | NOT NULL | 状态（active/quota_exhausted/revoked） | "active" |
| last_used_at | DATETIME | NULL | 最后使用时间 | "2026-05-11 10:00:00" |
| created_at | DATETIME | NOT NULL | 创建时间 | "2026-05-01 00:00:00" |
| updated_at | DATETIME | NOT NULL | 更新时间 | "2026-05-11 10:00:00" |

**索引**:

```sql
CREATE INDEX idx_api_keys_provider_id ON api_keys(provider_id);
CREATE INDEX idx_api_keys_status ON api_keys(status);
```

### 2.4 预警相关数据

#### alert_rules（预警规则表）

| 字段名 | 数据类型 | 约束 | 说明 | 示例值 |
|--------|---------|------|------|---------|
| rule_id | VARCHAR(64) | PK | 规则ID（UUID） | "rule_001" |
| rule_name | VARCHAR(128) | NOT NULL | 规则名称 | "个人配额预警" |
| rule_type | VARCHAR(32) | NOT NULL | 规则类型（quota/cost） | "quota" |
| target_type | VARCHAR(32) | NOT NULL | 目标类型（user/team） | "user" |
| target_id | VARCHAR(64) | NULL | 目标ID（为 NULL 时表示全局规则） | "user_001" |
| reminder_threshold | DECIMAL(5,2) | NOT NULL | 提醒阈值（%） | 70.00 |
| warning_threshold | DECIMAL(5,2) | NOT NULL | 警告阈值（%） | 90.00 |
| block_threshold | DECIMAL(5,2) | NOT NULL | 禁止阈值（%） | 100.00 |
| notification_channels | JSON | NOT NULL | 通知渠道配置 | `["email", "wechat"]` |
| enabled | BOOLEAN | NOT NULL DEFAULT TRUE | 是否启用 | true |
| created_at | DATETIME | NOT NULL | 创建时间 | "2026-05-11 10:00:00" |
| updated_at | DATETIME | NOT NULL | 更新时间 | "2026-05-11 10:00:00" |

#### alert_history（预警历史表）

| 字段名 | 数据类型 | 约束 | 说明 | 示例值 |
|--------|---------|------|------|---------|
| alert_id | VARCHAR(64) | PK | 预警ID（UUID） | "alert_20240511_001" |
| rule_id | VARCHAR(64) | FK | 规则ID | "rule_001" |
| user_id | VARCHAR(64) | FK | 用户ID | "user_001" |
| alert_type | VARCHAR(32) | NOT NULL | 预警类型 | "quota_warning" |
| severity | VARCHAR(16) | NOT NULL | 严重程度（info/warning/critical/emergency） | "warning" |
| threshold_value | DECIMAL(10,2) | NOT NULL | 阈值 | 90.00 |
| actual_value | DECIMAL(10,2) | NOT NULL | 实际值 | 92.50 |
| message | TEXT | NOT NULL | 预警消息 | "您的配额已使用 92.5%" |
| notification_status | VARCHAR(32) | NOT NULL | 通知状态（pending/sent/failed） | "sent" |
| status | VARCHAR(16) | NOT NULL | 状态（active/resolved） | "active" |
| resolved_at | DATETIME | NULL | 解决时间 | null |
| created_at | DATETIME | NOT NULL | 创建时间 | "2026-05-11 10:00:00" |

**索引**:

```sql
CREATE INDEX idx_alert_history_user_id ON alert_history(user_id);
CREATE INDEX idx_alert_history_status ON alert_history(status);
CREATE INDEX idx_alert_history_created_at ON alert_history(created_at);
```

---

## 3. 数据关系图

```mermaid
erDiagram
    users ||--o{ user_quotas : "has"
    users ||--o{ usage_logs : "generates"
    users ||--o{ daily_usage_summary : "has"
    users ||--o{ alert_history : "receives"
    
    provider_config ||--o{ model_mapping : "has"
    provider_config ||--o{ api_keys : "has"
    
    usage_logs ||--o{ daily_usage_summary : "aggregated into"
    
    alert_rules ||--o{ alert_history : "triggers"
    
    users {
        VARCHAR(64) user_id PK
        VARCHAR(128) username
        VARCHAR(256) email
        VARCHAR(256) password_hash
        VARCHAR(64) department_id
        VARCHAR(32) role
        VARCHAR(16) status
        DATETIME last_login_at
        DATETIME created_at
        DATETIME updated_at
    }
    
    user_quotas {
        VARCHAR(64) quota_id PK
        VARCHAR(64) user_id FK
        VARCHAR(32) quota_type
        BIGINT total_tokens
        BIGINT used_tokens
        DECIMAL(10,2) total_cost
        DECIMAL(10,2) used_cost
        DATETIME reset_at
        DATETIME created_at
        DATETIME updated_at
    }
    
    usage_logs {
        VARCHAR(64) log_id PK
        VARCHAR(64) user_id FK
        VARCHAR(64) team_id FK
        VARCHAR(32) provider
        VARCHAR(64) model
        INT input_tokens
        INT output_tokens
        INT total_tokens
        DECIMAL(10,4) cost
        VARCHAR(16) status
        DATETIME timestamp
        DATETIME created_at
    }
    
    daily_usage_summary {
        VARCHAR(64) summary_id PK
        DATE date
        VARCHAR(64) user_id FK
        VARCHAR(64) team_id FK
        INT total_requests
        BIGINT total_tokens
        DECIMAL(10,2) total_cost
        DECIMAL(10,2) avg_response_time
        DATETIME created_at
        DATETIME updated_at
    }
    
    provider_config {
        VARCHAR(64) provider_id PK
        VARCHAR(128) provider_name
        VARCHAR(256) api_base_url
        VARCHAR(32) auth_type
        JSON auth_config
        VARCHAR(32) protocol_type
        INT priority
        BOOLEAN enabled
        DATETIME created_at
        DATETIME updated_at
    }
    
    model_mapping {
        VARCHAR(64) mapping_id PK
        VARCHAR(64) provider_id FK
        VARCHAR(128) standard_model_name
        VARCHAR(128) vendor_model_name
        INT context_window
        DECIMAL(10,6) input_price_per_1k
        DECIMAL(10,6) output_price_per_1k
        BOOLEAN enabled
        DATETIME created_at
        DATETIME updated_at
    }
    
    api_keys {
        VARCHAR(64) key_id PK
        VARCHAR(64) provider_id FK
        TEXT api_key_encrypted
        VARCHAR(128) key_alias
        INT rate_limit_rpm
        INT used_rpm
        VARCHAR(16) status
        DATETIME last_used_at
        DATETIME created_at
        DATETIME updated_at
    }
    
    alert_rules {
        VARCHAR(64) rule_id PK
        VARCHAR(128) rule_name
        VARCHAR(32) rule_type
        VARCHAR(32) target_type
        VARCHAR(64) target_id
        DECIMAL(5,2) reminder_threshold
        DECIMAL(5,2) warning_threshold
        DECIMAL(5,2) block_threshold
        JSON notification_channels
        BOOLEAN enabled
        DATETIME created_at
        DATETIME updated_at
    }
    
    alert_history {
        VARCHAR(64) alert_id PK
        VARCHAR(64) rule_id FK
        VARCHAR(64) user_id FK
        VARCHAR(32) alert_type
        VARCHAR(16) severity
        DECIMAL(10,2) threshold_value
        DECIMAL(10,2) actual_value
        TEXT message
        VARCHAR(32) notification_status
        VARCHAR(16) status
        DATETIME created_at
    }
```

---

## 4. 索引设计

### 4.1 索引设计原则

1. **主键自动创建聚簇索引**
2. **外键必须创建索引**（加速 JOIN 查询）
3. **经常查询的字段必须创建索引**
4. **避免创建过多索引**（影响写入性能，建议单表索引数 ≤ 5）
5. **使用复合索引覆盖查询**（避免回表）

### 4.2 推荐索引

```sql
-- users 表
CREATE INDEX idx_users_department_id ON users(department_id);
CREATE INDEX idx_users_status ON users(status);

-- user_quotas 表
CREATE INDEX idx_user_quotas_user_id ON user_quotas(user_id);
CREATE INDEX idx_user_quotas_reset_at ON user_quotas(reset_at);

-- usage_logs 表（分区表）
CREATE INDEX idx_usage_logs_timestamp ON usage_logs(timestamp);
CREATE INDEX idx_usage_logs_user_id ON usage_logs(user_id);
CREATE INDEX idx_usage_logs_team_id ON usage_logs(team_id);
CREATE INDEX idx_usage_logs_provider ON usage_logs(provider);
CREATE INDEX idx_usage_logs_timestamp_user ON usage_logs(timestamp, user_id);

-- daily_usage_summary 表
CREATE INDEX idx_daily_summary_date ON daily_usage_summary(date);
CREATE INDEX idx_daily_summary_user_id ON daily_usage_summary(user_id);
CREATE INDEX idx_daily_summary_team_id ON daily_usage_summary(team_id);

-- api_keys 表
CREATE INDEX idx_api_keys_provider_id ON api_keys(provider_id);
CREATE INDEX idx_api_keys_status ON api_keys(status);

-- alert_history 表
CREATE INDEX idx_alert_history_user_id ON alert_history(user_id);
CREATE INDEX idx_alert_history_status ON alert_history(status);
CREATE INDEX idx_alert_history_created_at ON alert_history(created_at);
```

### 4.3 索引使用分析

使用 `EXPLAIN` 分析查询计划：

```sql
-- 分析查询计划
EXPLAIN SELECT * FROM usage_logs WHERE user_id = 'user_001' AND timestamp >= '2026-05-01';

-- 查看索引使用情况
SHOW INDEX FROM usage_logs;

-- 查看表状态
SHOW TABLE STATUS LIKE 'usage_logs';
```

---

## 5. 分区策略

### 5.1 分区表选择

**需要进行分区的大表**:

| 表名 | 数据量预估 | 分区策略 | 分区键 |
|------|-----------|---------|--------|
| usage_logs | > 1000 万行/月 | RANGE 分区（按月） | `timestamp` |
| alert_history | > 100 万行/月 | RANGE 分区（按月） | `created_at` |

### 5.2 分区示例

```sql
-- 对 usage_logs 表按月份分区
ALTER TABLE usage_logs 
PARTITION BY RANGE (YEAR(timestamp) * 100 + MONTH(timestamp)) (
    PARTITION p202401 VALUES LESS THAN (202402),
    PARTITION p202402 VALUES LESS THAN (202403),
    PARTITION p202403 VALUES LESS THAN (202404),
    PARTITION p202404 VALUES LESS THAN (202405),
    PARTITION p202405 VALUES LESS THAN (202406),
    PARTITION p202406 VALUES LESS THAN (202407),
    PARTITION p_future VALUES LESS THAN MAXVALUE
);

-- 查询指定分区
SELECT * FROM usage_logs PARTITION (p202405) WHERE user_id = 'user_001';
```

### 5.3 分区维护

```sql
-- 添加下个月的分区
ALTER TABLE usage_logs ADD PARTITION (
    PARTITION p202407 VALUES LESS THAN (202408)
);

-- 删除超过 90 天的分区（归档后删除）
ALTER TABLE usage_logs DROP PARTITION p202401;
```

---

## 6. 数据归档策略

### 6.1 归档规则

| 数据类型 | 归档期限 | 归档目标 | 删除期限 |
|---------|---------|---------|---------|
| usage_logs | 90 天 | 对象存储（CSV/Parquet） | 180 天 |
| alert_history | 1 年 | 对象存储（CSV） | 3 年 |
| daily_usage_summary | 3 年 | 对象存储（CSV） | 5 年 |
| 审计日志 | 永久 | 对象存储（CSV） | 不删除 |

### 6.2 归档流程

```sql
-- 1. 导出数据到 CSV
SELECT * FROM usage_logs 
WHERE timestamp < DATE_SUB(NOW(), INTERVAL 90 DAY)
INTO OUTFILE '/tmp/usage_logs_20240511.csv'
FIELDS TERMINATED BY ','
ENCLOSED BY '"'
LINES TERMINATED BY '\n';

-- 2. 确认导出成功后，删除已归档数据
DELETE FROM usage_logs 
WHERE timestamp < DATE_SUB(NOW(), INTERVAL 90 DAY);
```

---

## 7. 性能优化建议

### 7.1 数据库查询优化

1. **使用索引**: 所有 WHERE 条件、JOIN 条件必须走索引
2. **避免全表扫描**: 使用 `EXPLAIN` 分析查询计划
3. **使用批量操作**: 多次 INSERT/UPDATE 合并为批量操作
4. **使用预编译语句**: 避免 SQL 注入，提高性能

**示例**:

```sql
-- 不好：全表扫描
SELECT * FROM usage_logs WHERE DATE(timestamp) = '2026-05-11';

-- 好：使用索引
SELECT * FROM usage_logs 
WHERE timestamp >= '2026-05-11 00:00:00' 
  AND timestamp < '2026-05-12 00:00:00';
```

### 7.2 缓存策略

| 缓存对象 | 缓存方案 | TTL | 说明 |
|---------|---------|-----|------|
| 用户信息 | Redis | 10 分钟 | 用户基础信息 |
| 厂商配置 | Redis | 1 小时 | 厂商配置信息 |
| 模型映射 | Redis | 1 小时 | 模型名称映射 |
| 限流计数器 | Redis | 1 秒 | 滑动窗口限流 |
| 用量汇总 | Redis | 1 分钟 | 个人/团队用量汇总 |

### 7.3 批量写入

- **用量日志**: 每 100 条或每 1 秒批量写入 MySQL
- **实时指标**: 每 10 条或每 1 秒批量写入 InfluxDB

**示例**:

```java
// 批量写入 usage_logs
@Scheduled(fixedDelay = 1000)  // 每 1 秒执行一次
public void batchInsertUsageLogs() {
    List<UsageLog> batch = usageLogBuffer.drainAll();  // 取出所有待写入日志
    if (!batch.isEmpty()) {
        usageLogRepository.saveAll(batch);
    }
}
```

---

## 附录：InfluxDB 数据设计

### InfluxDB Bucket 设计

| Bucket 名称 | 保留策略 | 用途 |
|-------------|---------|------|
| ark_proxy_realtime | 24 小时 | 实时指标（1 秒精度） |
| ark_proxy_short_term | 90 天 | 短期数据（1 小时精度） |
| ark_proxy_long_term | 3 年 | 长期趋势（1 天精度） |

### Measurement 设计

#### proxy_metrics（代理指标）

| 字段 | 类型 | 说明 |
|------|------|------|
| requests | integer | 请求次数 |
| tokens | integer | Token 消耗 |
| cost | float | 费用 |
| latency | integer | 响应时间（毫秒） |

| Tag | 说明 |
|------|------|
| user_id | 用户ID |
| provider | 厂商名称 |
| model | 模型名称 |
| status | 状态（success/failed） |

**示例数据**:

```
proxy_metrics,user_id=user_001,provider=anthropic,model=claude-3-opus,status=success requests=1i,tokens=700i,cost=0.021,latency=1500i 1621152000000000000
```

---

**文档版本历史**

| 版本 | 日期 | 修改人 | 修改内容 |
|------|------|--------|----------|
| v1.0 | 2026-05-11 | 数析（Metric） | 初始版本 |

---

**审批记录**

- [ ] DBA 审批
- [ ] 架构师审批
- [ ] 研发 Leader 审批
