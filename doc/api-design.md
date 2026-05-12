# 方舟 Code Plan 中转站系统 - API 设计文档

**版本**: v1.0  
**日期**: 2026-05-11  
**作者**: API 设计团队  
**状态**: 待评审  
**对应 PRD**: prd-ark-code-plan-proxy-2026-05-11.md (v1.2)

---

## 目录

1. [API 设计规范](#1-api-设计规范)
2. [认证和授权](#2-认证和授权)
3. [代理 API](#3-代理-api)
4. [看板 API](#4-看板-api)
5. [AK 管理 API](#5-ak-管理-api)
6. [厂商管理 API](#6-厂商管理-api)
7. [预警管理 API](#7-预警管理-api)
8. [错误码定义](#8-错误码定义)
9. [限流策略](#9-限流策略)

---

## 1. API 设计规范

### 1.1 API 响应格式统一

#### 成功响应格式

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "userId": "user_001",
    "totalTokens": 1000000,
    "usedTokens": 750000
  },
  "timestamp": "2026-05-11T10:30:00Z",
  "request_id": "req_abc123"
}
```

#### 错误响应格式

```json
{
  "code": 429,
  "message": "Too Many Requests",
  "error": {
    "type": "rate_limit_exceeded",
    "code": "RATE_LIMIT_EXCEEDED",
    "message": "Rate limit exceeded. Please retry after 5 seconds.",
    "param": null,
    "detail": "当前 QPS 超限，请降低请求频率或升级配额"
  },
  "timestamp": "2026-05-11T10:30:00Z",
  "request_id": "req_abc123"
}
```

### 1.2 分页格式

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "items": [...],
    "pagination": {
      "page": 1,
      "pageSize": 20,
      "total": 100,
      "totalPages": 5
    }
  },
  "timestamp": "2026-05-11T10:30:00Z",
  "request_id": "req_abc123"
}
```

### 1.3 日期时间格式

- **JSON 序列化**: ISO 8601 格式 (`2026-05-11T10:30:00Z`)
- **数据库存储**: UTC 时间
- **前端展示**: 根据用户时区自动转换

---

## 2. 认证和授权

### 2.1 认证方式

系统支持两种认证方式：

1. **JWT Token**（用于前端 API 调用）
   - Header: `Authorization: Bearer {jwt_token}`
   - 有效期: 24 小时（可配置）

2. **API Key**（用于代理 API 调用）
   - Header: `Authorization: Bearer {ak}`
   - 有效期: 根据配额重置时间

### 2.2 登录 API

#### 用户登录

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "zhangsan",
  "password": "password123"
}
```

**响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "expiresIn": 86400,
    "user": {
      "userId": "user_001",
      "username": "zhangsan",
      "role": "user"
    }
  },
  "timestamp": "2026-05-11T10:30:00Z",
  "request_id": "req_abc123"
}
```

#### 刷新 Token

```http
POST /api/v1/auth/refresh
Content-Type: application/json

{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

---

## 3. 代理 API

### 3.1 Anthropic Messages API 代理

```http
POST /v1/messages
Authorization: Bearer {ak}
Content-Type: application/json

{
  "model": "claude-3-opus-20240229",
  "messages": [
    {"role": "user", "content": "Hello!"}
  ],
  "max_tokens": 4096
}
```

**响应**: 遵循 Anthropic API 响应格式

### 3.2 OpenAI Chat Completions API 代理

```http
POST /v1/chat/completions
Authorization: Bearer {ak}
Content-Type: application/json

{
  "model": "gpt-4o",
  "messages": [
    {"role": "user", "content": "Hello!"}
  ],
  "max_tokens": 4096
}
```

**响应**: 遵循 OpenAI API 响应格式

### 3.3 通用代理端点

```http
ALL /v1/*
Authorization: Bearer {ak}
```

**功能**: 自动检测请求协议，路由到对应的协议适配器

---

## 4. 看板 API

### 4.1 个人看板 API

#### 获取个人看板汇总数据

```http
GET /api/v1/dashboard/personal/summary
Authorization: Bearer {jwt_token}
```

**响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "dailyRequests": 150,
    "dailyTokens": 105000,
    "dailyCost": 3.15,
    "quotaUsageRate": 75.5,
    "successRate": 98.5,
    "avgResponseTime": 1250
  },
  "timestamp": "2026-05-11T10:30:00Z",
  "request_id": "req_abc123"
}
```

#### 获取个人用量趋势

```http
GET /api/v1/dashboard/personal/trend?period=7d&interval=hour
Authorization: Bearer {jwt_token}
```

**响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "trend": [
      {"timestamp": "2026-05-11T00:00:00Z", "requests": 10, "tokens": 7000, "cost": 0.21},
      {"timestamp": "2026-05-11T01:00:00Z", "requests": 15, "tokens": 10500, "cost": 0.315},
      ...
    ]
  },
  "timestamp": "2026-05-11T10:30:00Z",
  "request_id": "req_abc123"
}
```

#### 获取功能使用分布

```http
GET /api/v1/dashboard/personal/feature-distribution
Authorization: Bearer {jwt_token}
```

#### 获取模型使用分布

```http
GET /api/v1/dashboard/personal/model-distribution
Authorization: Bearer {jwt_token}
```

### 4.2 团队看板 API

#### 获取团队看板汇总数据

```http
GET /api/v1/dashboard/team/{team_id}/summary
Authorization: Bearer {jwt_token}
```

#### 获取团队成员用量排名

```http
GET /api/v1/dashboard/team/{team_id}/member-ranking?limit=10
Authorization: Bearer {jwt_token}
```

#### 获取部门费用分布

```http
GET /api/v1/dashboard/team/{team_id}/department-distribution
Authorization: Bearer {jwt_token}
```

### 4.3 系统看板 API

#### 获取系统实时指标

```http
GET /api/v1/dashboard/system/realtime
Authorization: Bearer {jwt_token}
```

**响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "currentQps": 150,
    "systemAvailability": 99.95,
    "errorRate": 0.5,
    "p95ResponseTime": 2500,
    "p99ResponseTime": 5000
  },
  "timestamp": "2026-05-11T10:30:00Z",
  "request_id": "req_abc123"
}
```

#### 获取活跃告警列表

```http
GET /api/v1/dashboard/system/alerts?status=active
Authorization: Bearer {jwt_token}
```

---

## 5. AK 管理 API

### 5.1 申请 AK

```http
POST /api/v1/ak/apply
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "reason": "用于代码补全",
  "provider": "anthropic",
  "model": "claude-3-opus",
  "expectedUsage": "每天约 10000 tokens"
}
```

**响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "applicationId": "app_001",
    "status": "pending",
    "approvalProgressUrl": "/ak/status/app_001"
  },
  "timestamp": "2026-05-11T10:30:00Z",
  "request_id": "req_abc123"
}
```

### 5.2 查询 AK 申请状态

```http
GET /api/v1/ak/status/{application_id}
Authorization: Bearer {jwt_token}
```

### 5.3 查看我的 AK

```http
GET /api/v1/ak/my
Authorization: Bearer {jwt_token}
```

**响应**:

```json
{
  "code": 200,
  "message": "success",
  "data": {
    "ak": "sk-ant-xxxxx",
    "sk": "xxxxx",
    "quota": {
      "totalTokens": 1000000,
      "usedTokens": 750000,
      "resetAt": "2026-06-01T00:00:00Z"
    },
    "configGuide": "https://docs.example.com/config-guide"
  },
  "timestamp": "2026-05-11T10:30:00Z",
  "request_id": "req_abc123"
}
```

### 5.4 审批 AK 申请（管理员）

```http
POST /api/v1/admin/ak/approve
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "applicationId": "app_001",
  "action": "approve",  // approve | reject
  "comment": "通过"
}
```

---

## 6. 厂商管理 API

### 6.1 获取厂商列表

```http
GET /api/v1/admin/providers
Authorization: Bearer {jwt_token}
```

### 6.2 新增厂商

```http
POST /api/v1/admin/providers
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "providerId": "anthropic",
  "providerName": "Anthropic (Claude)",
  "apiBaseUrl": "https://api.anthropic.com/v1",
  "authType": "bearer_token",
  "authConfig": "{\"header\": \"x-api-key\"}",
  "protocolType": "anthropic_messages",
  "priority": 1,
  "enabled": true
}
```

### 6.3 获取模型映射列表

```http
GET /api/v1/admin/providers/{provider_id}/models
Authorization: Bearer {jwt_token}
```

### 6.4 新增 API Key

```http
POST /api/v1/admin/providers/{provider_id}/keys
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "apiKey": "sk-ant-xxxxx",
  "keyAlias": "团队主 Key",
  "rateLimitRpm": 1000,
  "rateLimitTpm": 100000
}
```

### 6.5 获取厂商健康状态

```http
GET /api/v1/admin/providers/health
Authorization: Bearer {jwt_token}
```

---

## 7. 预警管理 API

### 7.1 获取预警规则列表

```http
GET /api/v1/alert/rules
Authorization: Bearer {jwt_token}
```

### 7.2 创建预警规则

```http
POST /api/v1/alert/rules
Authorization: Bearer {jwt_token}
Content-Type: application/json

{
  "ruleName": "个人配额预警",
  "ruleType": "quota",
  "targetType": "user",
  "targetId": "user_001",
  "reminderThreshold": 70.00,
  "warningThreshold": 90.00,
  "blockThreshold": 100.00,
  "notificationChannels": ["email", "wechat"]
}
```

### 7.3 获取预警历史

```http
GET /api/v1/alert/history?status=active&page=1&pageSize=20
Authorization: Bearer {jwt_token}
```

---

## 8. 错误码定义

### 8.1 HTTP 状态码

| HTTP 状态码 | 说明 |
|-------------|------|
| 200 | 成功 |
| 400 | 请求参数错误 |
| 401 | 认证失败（AK 无效/过期） |
| 403 | 权限不足 |
| 404 | 资源不存在 |
| 429 | 限流触发 |
| 500 | 服务器内部错误 |
| 503 | 厂商 API 不可用 |

### 8.2 业务错误码

| 错误码 | 说明 |
|--------|------|
| `AK_NOT_FOUND` | AK 不存在或已失效 |
| `AK_QUOTA_EXCEEDED` | AK 配额超限 |
| `RATE_LIMIT_EXCEEDED` | 限流触发 |
| `VENDOR_UNAVAILABLE` | 厂商 API 不可用 |
| `INVALID_REQUEST` | 请求参数错误 |
| `PERMISSION_DENIED` | 权限不足 |

---

## 9. 限流策略

### 9.1 限流维度

| 限流维度 | 限流规则 | 默认值 |
|---------|---------|--------|
| 用户级 | 单用户 QPS 上限 | 10 req/s |
| 团队级 | 单团队 QPS 上限 | 100 req/s |
| 全局 | 系统总 QPS 上限 | 1000 req/s |
| 厂商级 | 单厂商 QPS 上限 | 按厂商 API 限制 |

### 9.2 限流响应

```json
{
  "code": 429,
  "message": "Too Many Requests",
  "error": {
    "type": "rate_limit_exceeded",
    "code": "RATE_LIMIT_EXCEEDED",
    "message": "Rate limit exceeded. Please retry after 5 seconds.",
    "param": null,
    "detail": "当前 QPS 超限，请降低请求频率或升级配额"
  },
  "timestamp": "2026-05-11T10:30:00Z",
  "request_id": "req_abc123"
}
```

**Response Headers**:

```
Retry-After: 5
X-RateLimit-Limit: 10
X-RateLimit-Remaining: 0
X-RateLimit-Reset: 1621152300
```

---

## 附录：API 文档生成

使用 **Swagger/OpenAPI 3.0** 生成 API 文档：

```java
@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("方舟 Code Plan 中转站系统 API")
                .version("v1.0")
                .description("AI API 代理系统 API 文档"))
            .addServersItem(new Server().url("http://localhost:8080").description("开发环境"))
            .addServersItem(new Server().url("https://api.example.com").description("生产环境"));
    }
}
```

**访问 API 文档**:

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI Spec: `http://localhost:8080/v3/api-docs`

---

**文档版本历史**

| 版本 | 日期 | 修改人 | 修改内容 |
|------|------|--------|----------|
| v1.0 | 2026-05-11 | API 设计团队 | 初始版本 |

---

**审批记录**

- [ ] 后端 Leader 审批
- [ ] 前端 Leader 审批
- [ ] 测试 Leader 审批
