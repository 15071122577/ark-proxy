# API Documentation

**Version**: v1.0
**Date**: 2026-05-11
**Author**: WangMiao

---

## 1. Proxy API Endpoints

### Anthropic Messages API Proxy

```
POST /v1/messages
Authorization: Bearer {ak}
Content-Type: application/json

Request Body:
{
  "model": "claude-3-opus-20240229",
  "messages": [...],
  "max_tokens": 4096
}

Response: SSE stream or JSON
```

### OpenAI Chat Completions API Proxy

```
POST /v1/chat/completions
Authorization: Bearer {ak}
Content-Type: application/json

Request Body:
{
  "model": "gpt-4-turbo",
  "messages": [...],
  "max_tokens": 4096
}

Response: SSE stream or JSON
```

---

## 2. Authentication APIs

### Login

```
POST /api/v1/auth/login
Content-Type: application/json

Request:
{
  "username": "string",
  "password": "string"
}

Response:
{
  "code": 200,
  "data": {
    "accessToken": "jwt_token",
    "expiresIn": 86400,
    "userId": "string",
    "username": "string",
    "role": "admin|user|guest"
  }
}
```

### Refresh Token

```
POST /api/v1/auth/refresh
Content-Type: application/json

{
  "token": "current_jwt_token"
}
```

---

## 3. Dashboard APIs

### Personal Dashboard Summary

```
GET /api/v1/dashboard/personal/summary
Authorization: Bearer {jwt}

Response:
{
  "code": 200,
  "data": {
    "dailyRequests": 150,
    "dailyTokens": 105000,
    "dailyCost": 3.15,
    "quotaUsageRate": 75.5,
    "requestSuccessRate": 99.2,
    "avgResponseTime": 120
  }
}
```

### Personal Usage Trend

```
GET /api/v1/dashboard/personal/trend?days=7
Authorization: Bearer {jwt}

Response:
{
  "code": 200,
  "data": [
    { "timestamp": "2026-05-01", "requests": 120, "tokens": 84000, "cost": 2.52 },
    ...
  ]
}
```

### Team Summary

```
GET /api/v1/dashboard/team/summary
Authorization: Bearer {jwt}

Response:
{
  "code": 200,
  "data": {
    "dailyRequests": 1500,
    "dailyTokens": 1050000,
    "dailyCost": 31.5,
    "budgetUsageRate": 45.2,
    "memberCount": 10,
    "avgResponseTime": 150
  }
}
```

### System Realtime

```
GET /api/v1/dashboard/system/realtime
Authorization: Bearer {jwt}

Response:
{
  "code": 200,
  "data": {
    "currentQps": 42,
    "systemAvailability": 99.9,
    "errorRate": 0.1,
    "p95ResponseTime": 320,
    "p99ResponseTime": 480,
    "activeAlerts": 2,
    "totalKeys": 15,
    "healthyKeys": 14
  }
}
```

---

## 4. AK Management APIs

### Apply for AK

```
POST /api/v1/ak/apply
Authorization: Bearer {jwt}
Content-Type: application/json

{
  "purpose": "development|testing|production|research",
  "providerId": "string",
  "model": "string",
  "estimatedUsage": "string",
  "reason": "string"
}

Response:
{
  "code": 200,
  "data": {
    "applicationId": "string",
    "status": "pending"
  }
}
```

### Get My Applications

```
GET /api/v1/ak/applications
Authorization: Bearer {jwt}

Response:
{
  "code": 200,
  "data": [
    {
      "applicationId": "string",
      "status": "pending|approved|rejected",
      "createdAt": "2026-05-11T10:00:00Z",
      "comment": "string"
    }
  ]
}
```

### Get My AK

```
GET /api/v1/ak/my
Authorization: Bearer {jwt}

Response:
{
  "code": 200,
  "data": {
    "ak": "ak_xxxx",
    "sk": "sk_xxxx",
    "quota": {
      "totalTokens": 1000000,
      "usedTokens": 250000,
      "resetAt": "2026-06-01"
    },
    "configGuide": "# API Configuration\n...",
    "status": "active|quota_exhausted|revoked"
  }
}
```

### Approve AK (Admin)

```
POST /api/v1/ak/approve
Authorization: Bearer {jwt}
Content-Type: application/json

{
  "applicationId": "string",
  "action": "approve|reject",
  "comment": "string"
}
```

---

## 5. Alert APIs

### Get Alert Rules

```
GET /api/v1/alert/rules
Authorization: Bearer {jwt}

Response:
{
  "code": 200,
  "data": [
    {
      "ruleId": "string",
      "ruleName": "string",
      "ruleType": "quota|cost|error_rate",
      "targetType": "global|user|team",
      "reminderThreshold": 70,
      "warningThreshold": 90,
      "blockThreshold": 100,
      "enabled": true
    }
  ]
}
```

### Create Alert Rule

```
POST /api/v1/alert/rules
Authorization: Bearer {jwt}
Content-Type: application/json

{
  "ruleName": "string",
  "ruleType": "quota|cost|error_rate",
  "targetType": "global|user|team",
  "targetId": "string",
  "reminderThreshold": 70,
  "warningThreshold": 90,
  "blockThreshold": 100,
  "notificationChannels": "email|wechat|dingtalk|feishu"
}
```

### Get Alert History

```
GET /api/v1/alert/history?page=0&size=10&severity=critical&status=active
Authorization: Bearer {jwt}

Response:
{
  "code": 200,
  "data": {
    "items": [
      {
        "alertId": "string",
        "severity": "info|warning|critical|emergency",
        "thresholdValue": 90,
        "actualValue": 92.5,
        "message": "Quota usage exceeded 90%",
        "status": "active|resolved",
        "createdAt": "2026-05-11T10:00:00Z"
      }
    ],
    "pagination": {
      "page": 0,
      "pageSize": 10,
      "total": 100,
      "totalPages": 10
    }
  }
}
```

---

## 6. Provider APIs

### Get Providers

```
GET /api/v1/provider/list
Authorization: Bearer {jwt}

Response:
{
  "code": 200,
  "data": [
    {
      "providerId": "string",
      "providerName": "Anthropic",
      "apiBaseUrl": "https://api.anthropic.com",
      "protocolType": "anthropic",
      "priority": 1,
      "enabled": true
    }
  ]
}
```

### Create Provider

```
POST /api/v1/provider
Authorization: Bearer {jwt}
Content-Type: application/json

{
  "providerId": "string",
  "providerName": "string",
  "apiBaseUrl": "string",
  "authType": "api_key|bearer|oauth2",
  "authConfig": "encrypted_config_string",
  "protocolType": "anthropic|openai|deepseek|kimi|minimax|glm",
  "priority": 1
}
```

### Get Model Mappings

```
GET /api/v1/provider/models?providerId=xxx
Authorization: Bearer {jwt}

Response:
{
  "code": 200,
  "data": [
    {
      "mappingId": "string",
      "providerId": "string",
      "standardModelName": "claude-3-opus",
      "vendorModelName": "claude-3-opus-20240229",
      "contextWindow": 200000,
      "maxOutputTokens": 4096,
      "enabled": true
    }
  ]
}
```

### Get Provider Health

```
GET /api/v1/provider/health
Authorization: Bearer {jwt}

Response:
{
  "code": 200,
  "data": [
    {
      "providerId": "string",
      "providerName": "Anthropic",
      "status": "healthy|degraded|down",
      "latency": 120,
      "errorRate": 0.01
    }
  ]
}
```
