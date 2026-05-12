# 方舟 Code Plan 中转站系统 - 开发规约

> **文档版本**: v1.0  
> **生效日期**: 2026-05-11  
> **适用范围**: 方舟 Code Plan 中转站系统开发团队  
> **维护人**: WangMiao (Java Leader)

---

## 目录

1. [代码规范](#1-代码规范)
2. [Git 工作流规范](#2-git-工作流规范)
3. [API 开发规范](#3-api-开发规范)
4. [数据库开发规范](#4-数据库开发规范)
5. [测试规范](#5-测试规范)
6. [文档规范](#6-文档规范)
7. [安全规范](#7-安全规范)
8. [性能优化规范](#8-性能优化规范)
9. [Code Review 规范](#9-code-review-规范)
10. [发布部署规范](#10-发布部署规范)

---

## 1. 代码规范

### 1.1 Java 代码规范

#### 1.1.1 命名规范

| 元素 | 规范 | 示例 |
|------|------|------|
| 类名 | `UpperCamelCase`，名词或名词短语 | `ProxyService`, `UserController` |
| 方法名 | `lowerCamelCase`，动词或动词短语 | `handleRequest()`, `getUsageSummary()` |
| 变量名 | `lowerCamelCase` | `apiKey`, `requestCount` |
| 常量 | `UPPER_SNAKE_CASE` | `MAX_RETRY_ATTEMPTS`, `DEFAULT_TIMEOUT` |
| 包名 | `lowercase`，连续单词直接连接 | `com.ark.proxy.service` |
| 类型参数 | 单个大写字母 | `T`, `E`, `K`, `V` |

#### 1.1.2 代码格式

- **缩进**: 使用 4 个空格，禁止使用 Tab
- **行长度**: 单行不超过 120 字符
- **大括号**: K&R 风格（开括号在行尾，闭括号独占一行）
- **空行使用**:
  - 类成员之间空一行
  - 方法之间空一行
  - 逻辑段落之间空一行

**示例**:

```java
@Service
public class ProxyService {
    
    private static final int MAX_RETRY_ATTEMPTS = 3;
    
    private final WebClient webClient;
    
    public ProxyService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://api.anthropic.com").build();
    }
    
    public Mono<String> handleAnthropicRequest(String requestBody, String apiKey) {
        // 1. 参数校验
        if (requestBody == null || apiKey == null) {
            return Mono.error(new IllegalArgumentException("Request body and API key are required"));
        }
        
        // 2. 转发请求
        return webClient.post()
            .uri("/v1/messages")
            .header("x-api-key", apiKey)
            .header("Content-Type", "application/json")
            .bodyValue(requestBody)
            .retrieve()
            .bodyToMono(String.class)
            .retryWhen(Retry.backoff(MAX_RETRY_ATTEMPTS, Duration.ofMillis(100)))
            .doOnError(e -> log.error("Failed to proxy request", e));
    }
}
```

#### 1.1.3 注释规范

- **类注释**: 使用 Javadoc，包含类功能描述、作者、日期

```java
/**
 * 代理核心服务
 * 
 * 负责处理 AI API 请求代理，包括：
 * - 协议路由
 * - 负载均衡
 * - 重试机制
 * 
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
@Service
public class ProxyService {
    // ...
}
```

- **方法注释**: 使用 Javadoc，包含功能描述、参数、返回值、异常

```java
/**
 * 处理 Anthropic Messages API 请求
 * 
 * @param requestBody 请求体（JSON 格式）
 * @param apiKey 客户端 AK
 * @return 响应体（Mono<String>）
 * @throws RateLimitException 当触发限流时
 * @throws VendorUnavailableException 当厂商 API 不可用时
 */
public Mono<String> handleAnthropicRequest(String requestBody, String apiKey) {
    // ...
}
```

- **代码注释**: 复杂逻辑必须添加注释

```java
// 1. 查询用户配额（带缓存）
// 2. 检查是否超限
// 3. 记录用量日志
```

#### 1.1.4 异常处理

- 使用自定义业务异常（继承自 `RuntimeException`）
- 所有异常必须由 `GlobalExceptionHandler` 统一处理
- 异常日志必须包含 `trace_id` 和 `context`

**自定义异常示例**:

```java
public class RateLimitException extends RuntimeException {
    private final String errorCode;
    private final int retryAfterSeconds;
    
    public RateLimitException(String message, String errorCode, int retryAfterSeconds) {
        super(message);
        this.errorCode = errorCode;
        this.retryAfterSeconds = retryAfterSeconds;
    }
    
    // getters
}
```

**全局异常处理器**:

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<ErrorResponse> handleRateLimitException(RateLimitException e) {
        log.warn("Rate limit exceeded: {}", e.getMessage());
        
        ErrorResponse error = new ErrorResponse(
            "rate_limit_exceeded",
            e.getMessage(),
            e.getErrorCode(),
            Instant.now().toString()
        );
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("Retry-After", String.valueOf(e.getRetryAfterSeconds()));
        
        return ResponseEntity.status(429).headers(headers).body(error);
    }
}
```

### 1.2 TypeScript/Vue 代码规范

#### 1.2.1 命名规范

| 元素 | 规范 | 示例 |
|------|------|------|
| 组件名 | `PascalCase` | `PersonalDashboard.vue` |
| 变量/函数名 | `camelCase` | `getUsageSummary()`, `apiKey` |
| 常量 | `UPPER_SNAKE_CASE` | `API_BASE_URL`, `MAX_RETRY_COUNT` |
| TypeScript 类型 | `PascalCase` | `UserInfo`, `ApiResponse` |
| CSS 类名 | `kebab-case` | `.dashboard-card`, `.usage-chart` |

#### 1.2.2 组件规范

- 使用 Composition API (`<script setup lang="ts">`)
- Props 和 Emits 必须定义 TypeScript 类型
- 组件样式使用 Scoped CSS 或 CSS Modules

**示例**:

```vue
<script setup lang="ts">
import { ref, computed, onMounted } from 'vue';
import { UserInfo } from '@/types/user';
import { getUsageSummary } from '@/api/dashboard';

// Props 定义
interface Props {
  userId: string;
  showDetails?: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  showDetails: false
});

// Emits 定义
const emit = defineEmits<{
  (e: 'load-success', data: any): void;
  (e: 'load-error', error: Error): void;
}>();

// 响应式数据
const userInfo = ref<UserInfo | null>(null);
const loading = ref(false);

// 计算属性
const usageRate = computed(() => {
  if (!userInfo.value) return 0;
  return (userInfo.value.usedTokens / userInfo.value.totalTokens) * 100;
});

// 生命周期
onMounted(async () => {
  await loadData();
});

// 方法
const loadData = async () => {
  loading.value = true;
  try {
    const data = await getUsageSummary(props.userId);
    userInfo.value = data;
    emit('load-success', data);
  } catch (error) {
    emit('load-error', error as Error);
  } finally {
    loading.value = false;
  }
};
</script>

<template>
  <div class="dashboard-card">
    <h2>个人看板</h2>
    <div v-if="loading">加载中...</div>
    <div v-else-if="userInfo">
      <p>用量使用率: {{ usageRate.toFixed(2) }}%</p>
      <slot name="details" v-if="showDetails"></slot>
    </div>
  </div>
</template>

<style scoped>
.dashboard-card {
  padding: 16px;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}
</style>
```

#### 1.2.3 代码组织

- API 接口统一放在 `src/api/` 目录
- 工具函数统一放在 `src/utils/` 目录
- 类型定义统一放在 `src/types/` 目录
- 组件按需导出，避免循环依赖

**API 接口示例**:

```typescript
// src/api/dashboard.ts
import request from '@/utils/request';
import { DashboardSummary, UsageTrend } from '@/types/dashboard';

export const dashboardApi = {
  // 获取个人看板汇总数据
  getPersonalSummary(): Promise<ApiResponse<DashboardSummary>> {
    return request.get('/api/v1/dashboard/personal/summary');
  },

  // 获取用量趋势
  getUsageTrend(params: {
    startDate: string;
    endDate: string;
    interval: 'hour' | 'day';
  }): Promise<ApiResponse<UsageTrend>> {
    return request.get('/api/v1/dashboard/personal/trend', { params });
  }
};
```

---

## 2. Git 工作流规范

### 2.1 分支管理策略

```
main (生产环境)
  ↑
  | (合并请求)
release/v1.0.0 (预发布环境)
  ↑
  | (合并请求)
develop (开发环境)
  ↑
  | (合并请求)
feature/anthropic-adapter (功能分支)
bugfix/rate-limit (Bug 修复分支)
```

#### 分支命名规范

| 分支类型 | 命名格式 | 示例 |
|----------|----------|------|
| 功能分支 | `feature/<short-description>` | `feature/anthropic-adapter` |
| Bug 修复分支 | `bugfix/<short-description>` | `bugfix/rate-limit` |
| 发布分支 | `release/v<major>.<minor>.<patch>` | `release/v1.0.0` |
| 热修复分支 | `hotfix/<short-description>` | `hotfix/critical-bug` |
| 文档分支 | `docs/<short-description>` | `docs/api-documentation` |

### 2.2 Commit Message 规范

遵循 **Conventional Commits** 规范：

```
<type>(<scope>): <subject>

<body>

<footer>
```

#### Type 类型

| Type | 说明 | 示例 |
|------|------|------|
| `feat` | 新功能 | `feat(proxy): 添加 Anthropic 协议适配器` |
| `fix` | Bug 修复 | `fix(rate-limit): 修复限流计数器不准确的问题` |
| `docs` | 文档更新 | `docs(readme): 更新 API 文档` |
| `style` | 代码格式调整（不影响功能） | `style(service): 格式化代码` |
| `refactor` | 重构（既不是新功能也不是 Bug 修复） | `refactor(proxy): 重构代理服务` |
| `perf` | 性能优化 | `perf(cache): 优化 Redis 缓存策略` |
| `test` | 测试相关 | `test(service): 添加 ProxyService 单元测试` |
| `chore` | 构建过程或辅助工具变动 | `chore(deps): 升级 Spring Boot 到 3.2.0` |

#### 示例

```bash
feat(proxy): 添加 Anthropic 协议适配器

- 实现 AnthropicAdapter 类
- 支持 /v1/messages 接口代理
- 添加流式响应支持

Closes #123
```

```bash
fix(rate-limit): 修复限流计数器不准确的问题

修复了使用 Redis 滑动窗口算法时计数器不准确的 Bug，
原因是时间窗口计算错误。

Fixes #456
```

### 2.3 Pull Request (PR) 规范

#### PR 标题格式

```
<type>(<scope>): <description> (#<issue-number>)
```

#### PR 描述模板

```markdown
## 📝 变更说明

<!-- 简要描述本次 PR 的变更内容 -->

## 🎯 关联 Issue

<!-- 关联的 Issue 编号 -->
Closes #

## 🧪 测试说明

<!-- 描述如何测试本次变更 -->
- [ ] 单元测试通过
- [ ] 集成测试通过
- [ ] 手动测试通过

## 📸 截图（如果涉及 UI 变更）

<!-- 添加截图 -->

## ✅ Checklist

- [ ] 代码符合规范
- [ ] 添加/更新了测试
- [ ] 添加/更新了文档
- [ ] 没有产生新的警告
```

---

## 3. API 开发规范

### 3.1 API 响应格式统一

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

### 3.2 API 错误处理

- **400 Bad Request**: 请求参数错误
- **401 Unauthorized**: 认证失败（AK 无效/过期）
- **403 Forbidden**: 权限不足
- **404 Not Found**: 资源不存在
- **429 Too Many Requests**: 限流触发
- **500 Internal Server Error**: 服务器内部错误
- **503 Service Unavailable**: 厂商 API 不可用

### 3.3 API 文档规范

使用 **Swagger/OpenAPI 3.0** 生成 API 文档：

```java
@Tag(name = "代理管理", description = "AI API 代理相关接口")
@RestController
@RequestMapping("/api/v1/proxy")
public class UniversalProxyController {
    
    @Operation(
        summary = "通用代理接口",
        description = "自动检测请求协议并转发到对应厂商 API"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "成功"),
        @ApiResponse(responseCode = "429", description = "限流触发"),
        @ApiResponse(responseCode = "503", description = "厂商 API 不可用")
    })
    @PostMapping("/**")
    public Mono<ResponseEntity<String>> handleUniversalRequest(
        @Parameter(description = "请求路径", required = true)
        @PathVariable String path,
        
        @Parameter(description = "请求体", required = true)
        @RequestBody String requestBody,
        
        @Parameter(description = "Authorization header", required = true)
        @RequestHeader("Authorization") String authorization
    ) {
        // ...
    }
}
```

---

## 4. 数据库开发规范

### 4.1 表命名规范

- 表名使用小写字母和下划线（snake_case）
- 表名使用复数形式（e.g., `users`, `usage_logs`）
- 关联表使用两个表名组合（e.g., `user_roles`）

### 4.2 字段命名规范

- 字段名使用小写字母和下划线（snake_case）
- 主键统一命名为 `id`（VARCHAR(64)，UUID 格式）
- 外键命名：`<table_name>_id`（e.g., `user_id`, `provider_id`）
- 时间戳字段：`created_at`, `updated_at`

### 4.3 索引规范

- 主键自动创建聚簇索引
- 外键必须创建索引
- 经常查询的字段必须创建索引
- 避免创建过多索引（影响写入性能）

**示例**:

```sql
-- usage_logs 表索引
CREATE INDEX idx_usage_logs_timestamp ON usage_logs(timestamp);
CREATE INDEX idx_usage_logs_user_id ON usage_logs(user_id);
CREATE INDEX idx_usage_logs_team_id ON usage_logs(team_id);
CREATE INDEX idx_usage_logs_timestamp_user ON usage_logs(timestamp, user_id);
```

### 4.4 分区规范

对于大表（数据量 > 1000 万行），必须进行分区：

```sql
-- 对 usage_logs 表按月份分区
ALTER TABLE usage_logs 
PARTITION BY RANGE (YEAR(timestamp) * 100 + MONTH(timestamp)) (
    PARTITION p202401 VALUES LESS THAN (202402),
    PARTITION p202402 VALUES LESS THAN (202403),
    PARTITION p202403 VALUES LESS THAN (202404),
    ...
    PARTITION p_future VALUES LESS THAN MAXVALUE
);
```

---

## 5. 测试规范

### 5.1 单元测试

- 覆盖率要求：> 80%
- 必须测试边界条件和异常情况
- 使用 Mock 替代外部依赖

**示例**:

```java
@ExtendWith(MockitoExtension.class)
class ProxyServiceTest {
    
    @Mock
    private WebClient webClient;
    
    @InjectMocks
    private ProxyService proxyService;
    
    @Test
    @DisplayName("处理 Anthropic 请求 - 成功")
    void handleAnthropicRequest_success() {
        // Arrange
        String requestBody = "{\"model\": \"claude-3-opus\"}";
        String apiKey = "sk-ant-123";
        
        WebClient.RequestBodyUriSpec uriSpec = Mockito.mock(WebClient.RequestBodyUriSpec.class);
        WebClient.RequestBodySpec bodySpec = Mockito.mock(WebClient.RequestBodySpec.class);
        WebClient.ResponseSpec responseSpec = Mockito.mock(WebClient.ResponseSpec.class);
        
        Mockito.when(webClient.post()).thenReturn(uriSpec);
        Mockito.when(uriSpec.uri(Mockito.anyString())).thenReturn(bodySpec);
        Mockito.when(bodySpec.header(Mockito.anyString(), Mockito.anyString())).thenReturn(bodySpec);
        Mockito.when(bodySpec.bodyValue(Mockito.any())).thenReturn(bodySpec);
        Mockito.when(bodySpec.retrieve()).thenReturn(responseSpec);
        Mockito.when(responseSpec.bodyToMono(String.class)).thenReturn(Mono.just("{\"success\": true}"));
        
        // Act
        Mono<String> result = proxyService.handleAnthropicRequest(requestBody, apiKey);
        
        // Assert
        StepVerifier.create(result)
            .expectNext("{\"success\": true}")
            .verifyComplete();
    }
    
    @Test
    @DisplayName("处理 Anthropic 请求 - API 限流")
    void handleAnthropicRequest_rateLimit() {
        // Arrange
        // ...
        
        // Act & Assert
        // ...
    }
}
```

### 5.2 集成测试

- 使用 TestContainers 进行数据库集成测试
- 使用 WireMock 模拟外部 API

**示例**:

```java
@SpringBootTest
@Testcontainers
class ProxyServiceIntegrationTest {
    
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
        .withDatabaseName("ark_proxy_test")
        .withUsername("test")
        .withPassword("test");
    
    @Test
    @DisplayName("完整流程：代理请求 -> 记录用量 -> 触发预警")
    void completeFlow_test() {
        // 1. 发送代理请求
        // 2. 验证用量日志已记录
        // 3. 验证预警已触发（如果超限）
    }
}
```

---

## 6. 文档规范

### 6.1 代码文档

- 所有公共类、方法必须添加 Javadoc/TSDoc 注释
- 复杂算法必须添加注释说明
- 使用示例见 [1.1.3 注释规范](#113-注释规范)

### 6.2 API 文档

- 使用 Swagger/OpenAPI 3.0 生成 API 文档
- 每个 API 必须包含：
  - 功能描述
  - 请求参数说明
  - 响应格式说明
  - 错误码说明
  - 使用示例

### 6.3 架构文档

- 使用 Markdown 格式
- 使用 Mermaid 绘制流程图、时序图、类图
- 包含：架构图、数据流图、部署图

---

## 7. 安全规范

### 7.1 敏感数据保护

- **AK/SK 加密存储**: 使用 AES-256-GCM 加密后存储到 MySQL
- **密码哈希**: 使用 bcrypt (salt round=12) 哈希后存储
- **JWT Secret**: 存储在环境变量，定期轮换

### 7.2 SQL 注入防护

- 使用 JPA 的参数化查询
- 禁止字符串拼接 SQL
- 使用预编译语句（PreparedStatement）

### 7.3 XSS 防护

- 前端使用 Vue 的模板语法（自动转义）
- 后端使用 OWASP ESAPI 转义

---

## 8. 性能优化规范

### 8.1 数据库查询优化

- 必须使用索引，避免全表扫描
- 使用 `EXPLAIN` 分析查询计划
- 避免使用 `SELECT *`，只查询需要的字段

### 8.2 缓存策略

| 缓存对象 | TTL | 说明 |
|---------|-----|------|
| 模型列表 | 1 小时 | 各厂商模型列表 |
| 用户信息 | 10 分钟 | 用户基础信息 |
| 限流计数器 | 1 秒 | Redis 滑动窗口 |

### 8.3 批量处理

- 用量日志每 100 条或每 1 秒批量写入 MySQL
- 通知发送异步执行，失败重试

---

## 9. Code Review 规范

### 9.1 Review 检查清单

- [ ] 代码符合命名规范
- [ ] 代码格式正确（使用格式化工具）
- [ ] 添加了必要的注释
- [ ] 异常处理完善
- [ ] 添加了单元测试
- [ ] 测试覆盖率 > 80%
- [ ] 没有硬编码敏感信息
- [ ] API 响应格式正确
- [ ] 数据库查询使用了索引
- [ ] 添加了必要的日志

### 9.2 Review 流程

1. 开发者提交 PR
2. 至少 1 位团队成员进行 Code Review
3. 所有评论必须解决后才能合并
4. 合并前必须通过 CI/CD 流水线

---

## 10. 发布部署规范

### 10.1 版本号规范

遵循 **语义化版本 2.0.0**：

```
MAJOR.MINOR.PATCH

MAJOR: 不兼容的 API 修改
MINOR: 向下兼容的功能性新增
PATCH: 向下兼容的问题修正
```

**示例**: `v1.2.3`

### 10.2 发布流程

1. 从 `develop` 分支创建 `release/vX.Y.Z` 分支
2. 更新版本号、CHANGELOG.md
3. 进行测试
4. 合并到 `main` 分支，打标签（Tag）
5. 合并回 `develop` 分支
6. 部署到生产环境

### 10.3 回滚策略

- 如果发布后发现问题，立即回滚到上一个稳定版本
- 使用 Docker 镜像版本回滚
- 数据库变更必须可回滚（使用 Flyway 版本化管理）

---

## 附录：工具推荐

| 类型 | 工具 | 用途 |
|------|------|------|
| IDE | IntelliJ IDEA | Java 开发 |
| IDE | VS Code | TypeScript/Vue 开发 |
| 代码格式化 | Spotless (Java), Prettier (TS/Vue) | 统一代码格式 |
| 静态分析 | SonarQube | 代码质量检测 |
| CI/CD | Jenkins/GitHub Actions | 持续集成/持续部署 |
| API 测试 | Postman/Insomnia | API 接口测试 |
| 性能测试 | JMeter/Gatling | 性能测试 |

---

**文档版本历史**

| 版本 | 日期 | 修改人 | 修改内容 |
|------|------|--------|----------|
| v1.0 | 2026-05-11 | WangMiao | 初始版本 |

---

**审批记录**

- [ ] Java Leader 审批
- [ ] 前端 Leader 审批
- [ ] 测试 Leader 审批
- [ ] 运维 Leader 审批
