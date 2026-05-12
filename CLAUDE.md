# CLAUDE.md - 方舟 Code Plan 中转站系统

> **项目类型**: AI API 代理/中转系统  
> **技术栈**: Java Spring Boot (WebFlux) + Vue 3 + MySQL 8 + InfluxDB + Redis  
> **创建日期**: 2026-05-11  
> **维护人**: WangMiao (Java Leader)

---

## 项目概述

**方舟 Code Plan 中转站系统** 是一个 AI 大模型 API 代理系统，主要功能：

1. **多协议兼容**: 支持 Anthropic Messages API (`/v1/messages`) 和 OpenAI Chat Completions API (`/v1/chat/completions`)
2. **AK/SK 管理**: 团队级 API Key 分配、配额管理、用量追踪
3. **用量监控**: 多维度监控（个人/团队/系统），支持 Token 级精细化计量
4. **流量预警**: 多级预警（70%/90%/100%），自动通知
5. **厂商管理**: 多厂商支持（Anthropic/OpenAI/DeepSeek/Kimi 等），健康检查和故障转移

---

## 技术栈详解

### 后端技术栈

| 组件 | 技术 | 版本 | 用途 |
|------|------|------|------|
| **框架** | Spring Boot (WebFlux) | 3.2+ | 响应式编程，非阻塞 I/O，适合 AI API 代理 |
| **数据库** | MySQL | 8.0+ | 关系型数据存储（用户、配额、日志） |
| **时序数据库** | InfluxDB | 2.x | 时序数据存储（实时监控指标） |
| **缓存** | Redis | 7+ | 缓存、限流、会话管理 |
| **认证** | JWT | 0.11.5 | 用户认证和授权 |
| **熔断限流** | Resilience4j + Bucket4j | - | 服务高可用保障 |

### 前端技术栈

| 组件 | 技术 | 版本 | 用途 |
|------|------|------|------|
| **框架** | Vue 3 + TypeScript | 3.4+ | 前端框架 |
| **UI 库** | Element Plus | 2.x | 企业级 UI 组件 |
| **图表** | ECharts + vue-echarts | 5.x | 数据可视化 |
| **状态管理** | Pinia | 2.1+ | 状态管理 |
| **HTTP 客户端** | Axios | 1.6+ | API 请求 |
| **构建工具** | Vite | 5.1+ | 快速构建 |

---

## 项目结构

```
ark-proxy/
├── src/main/java/com/ark/proxy/           # 后端代码
│   ├── config/                           # 配置类
│   ├── controller/                       # 控制器层
│   │   ├── api/                         # API 代理控制器
│   │   ├── dashboard/                   # 看板控制器
│   │   ├── auth/                        # 认证控制器
│   │   └── admin/                       # 管理控制器
│   ├── service/                         # 服务层
│   │   ├── proxy/                       # 代理服务
│   │   ├── adapter/                     # 协议适配器
│   │   ├── vendor/                      # 厂商管理服务
│   │   ├── auth/                        # 认证服务
│   │   ├── monitoring/                  # 监控服务
│   │   └── notification/                # 通知服务
│   ├── repository/                      # 数据访问层
│   │   ├── mysql/                       # MySQL 数据访问
│   │   ├── influxdb/                    # InfluxDB 数据访问
│   │   └── redis/                       # Redis 数据访问
│   ├── model/                           # 数据模型
│   │   ├── entity/                      # 实体类
│   │   ├── dto/                         # 数据传输对象
│   │   └── enums/                      # 枚举类
│   ├── exception/                       # 异常处理
│   ├── filter/                          # 过滤器
│   ├── handler/                         # 处理器
│   └── util/                            # 工具类
├── src/main/resources/                   # 后端资源
│   ├── application.yml                  # 主配置文件
│   ├── application-dev.yml              # 开发环境配置
│   ├── application-prod.yml            # 生产环境配置
│   ├── schema.sql                       # 数据库初始化脚本
│   └── data.sql                        # 初始数据脚本
├── frontend/                            # 前端代码
│   ├── src/
│   │   ├── api/                         # API 接口层
│   │   ├── components/                  # 组件
│   │   ├── views/                       # 页面
│   │   ├── router/                      # 路由
│   │   ├── store/                       # 状态管理
│   │   ├── types/                       # TypeScript 类型定义
│   │   ├── utils/                       # 工具函数
│   │   └── assets/                      # 静态资源
│   ├── package.json                     # 依赖配置
│   ├── tsconfig.json                    # TypeScript 配置
│   └── vite.config.ts                  # Vite 配置
├── doc/                                 # 项目文档（持久化）
│   ├── PRD.md                          # 产品需求文档
│   ├── architecture.md                  # 架构设计文档
│   ├── data-design.md                  # 数据设计文档
│   ├── api-design.md                   # API 设计文档
│   └── deployment.md                   # 部署文档
├── docs/                                # 开发文档
│   ├── development-guide.md            # 开发指南
│   ├── coding-standards.md             # 编码规范
│   ├── testing-guide.md               # 测试指南
│   └── troubleshooting.md             # 问题排查
├── docker/                               # Docker 相关
│   ├── docker-compose.yml              # Docker Compose 配置
│   ├── Dockerfile.backend              # 后端 Dockerfile
│   └── Dockerfile.frontend            # 前端 Dockerfile
├── scripts/                              # 脚本
│   ├── init-db.sql                     # 数据库初始化脚本
│   ├── init-influxdb.sh               # InfluxDB 初始化脚本
│   └── deploy.sh                      # 部署脚本
└── CLAUDE.md                           # 本文件（Claude 项目说明）
```

---

## 开发规约

### 代码规范

#### Java 代码规范

1. **命名规范**:
   - 类名: `UpperCamelCase` (e.g., `ProxyService`)
   - 方法名: `lowerCamelCase` (e.g., `handleAnthropicRequest`)
   - 常量: `UPPER_SNAKE_CASE` (e.g., `MAX_RETRY_ATTEMPTS`)
   - 包名: `lowercase` (e.g., `com.ark.proxy.service`)

2. **注释规范**:
   - 类注释: Javadoc 格式，包含类功能描述、作者、日期
   - 方法注释: Javadoc 格式，包含功能描述、参数、返回值、异常
   - 代码注释: 复杂逻辑必须添加注释

3. **异常处理**:
   - 使用自定义业务异常 (e.g., `RateLimitException`)
   - 全局异常处理器 (`GlobalExceptionHandler`)
   - 异常日志必须包含 trace_id 和 context

#### TypeScript/Vue 代码规范

1. **命名规范**:
   - 组件名: `PascalCase` (e.g., `PersonalDashboard.vue`)
   - 变量/函数名: `camelCase` (e.g., `getUsageSummary`)
   - 常量: `UPPER_SNAKE_CASE` (e.g., `API_BASE_URL`)
   - TypeScript 类型: `PascalCase` (e.g., `UserInfo`)

2. **组件规范**:
   - 使用 Composition API (`<script setup lang="ts">`)
   - Props 和 Emits 必须定义 TypeScript 类型
   - 组件样式使用 Scoped CSS 或 CSS Modules

3. **代码组织**:
   - API 接口统一放在 `src/api/` 目录
   - 工具函数统一放在 `src/utils/` 目录
   - 类型定义统一放在 `src/types/` 目录

### Git 提交规范

遵循 **Conventional Commits** 规范：

```
<type>(<scope>): <subject>

<body>

<footer>
```

**Type 类型**:

| Type | 说明 |
|------|------|
| `feat` | 新功能 |
| `fix` | Bug 修复 |
| `docs` | 文档更新 |
| `style` | 代码格式调整（不影响功能） |
| `refactor` | 重构（既不是新功能也不是 Bug 修复） |
| `perf` | 性能优化 |
| `test` | 测试相关 |
| `chore` | 构建过程或辅助工具变动 |

**示例**:

```bash
feat(proxy): 添加 Anthropic 协议适配器

- 实现 AnthropicAdapter 类
- 支持 /v1/messages 接口代理
- 添加流式响应支持

Closes #123
```

### 分支管理策略

- `main`: 生产环境分支，只允许从 `release/*` 分支合并
- `develop`: 开发环境分支，功能分支合并到此分支
- `feature/*`: 功能分支（e.g., `feature/anthropic-adapter`）
- `bugfix/*`: Bug 修复分支（e.g., `bugfix/rate-limit`）
- `release/*`: 发布分支（e.g., `release/v1.0.0`）
- `hotfix/*`: 热修复分支（e.g., `hotfix/critical-bug`）

---

## 开发环境搭建

### 后端环境要求

1. **JDK**: 17+ (推荐 OpenJDK 17)
2. **Maven**: 3.8+
3. **MySQL**: 8.0+
4. **Redis**: 7+
5. **InfluxDB**: 2.x

### 前端环境要求

1. **Node.js**: 18+ (推荐 20 LTS)
2. **npm**: 9+ 或 **yarn**: 1.22+

### 快速启动

#### 后端启动

```bash
# 1. 克隆项目
git clone <repository-url>
cd ark-proxy

# 2. 配置数据库
mysql -u root -p < scripts/init-db.sql

# 3. 配置 InfluxDB
bash scripts/init-influxdb.sh

# 4. 启动 Redis (如果使用 Docker)
docker run -d -p 6379:6379 redis:7

# 5. 编译并启动
mvn clean install
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

#### 前端启动

```bash
# 1. 进入前端目录
cd frontend

# 2. 安装依赖
npm install

# 3. 启动开发服务器
npm run dev
```

---

## 核心功能模块

### 1. API 代理模块

**功能**: 代理 AI 厂商 API 请求，支持多协议适配

**关键类**:
- `ProxyService.java`: 代理核心服务
- `AnthropicAdapter.java`: Anthropic 协议适配器
- `OpenAIAdapter.java`: OpenAI 协议适配器
- `LoadBalancer.java`: 负载均衡器

**API 端点**:
- `POST /v1/messages`: Anthropic Messages API 代理
- `POST /v1/chat/completions`: OpenAI Chat Completions API 代理
- `ALL /v1/*`: 通用代理端点（自动检测协议）

### 2. AK 分配管理模块

**功能**: AK 申请、审批、分配、轮换

**关键类**:
- `AKService.java`: AK 分配服务
- `AuthService.java`: 认证服务
- `APIKeyPoolService.java`: API Key 池管理

**API 端点**:
- `POST /api/v1/ak/apply`: 提交 AK 申请
- `GET /api/v1/ak/status/{application_id}`: 查询审批状态
- `GET /api/v1/ak/my`: 查看我的 AK

### 3. 用量监控模块

**功能**: 多维度用量监控、看板展示

**关键类**:
- `UsageMonitoringService.java`: 用量监控服务
- `MetricsCollector.java`: 指标采集器
- `DashboardService.java`: 看板数据服务

**API 端点**:
- `GET /api/v1/dashboard/personal/summary`: 个人看板数据
- `GET /api/v1/dashboard/team/{team_id}/summary`: 团队看板数据
- `GET /api/v1/dashboard/system/realtime`: 系统看板数据

### 4. 流量预警模块

**功能**: 多级预警、自动通知

**关键类**:
- `AlertService.java`: 预警服务
- `NotificationService.java`: 通知服务

**API 端点**:
- `GET /api/v1/alert/rules`: 获取预警规则
- `POST /api/v1/alert/rules`: 创建预警规则
- `GET /api/v1/alert/history`: 查询预警历史

---

## 数据库设计

### 核心数据表

1. **users**: 用户基础信息表
2. **user_quotas**: 用户配额表
3. **usage_logs**: 用量日志表（分区表）
4. **daily_usage_summary**: 日用量汇总表
5. **provider_config**: 厂商配置表
6. **model_mapping**: 模型映射表
7. **api_keys**: API Key 池表
8. **alert_rules**: 预警规则表
9. **alert_history**: 预警历史表

**详细说明**: 参见 `doc/data-design.md`

---

## API 接口设计

### 代理 API

```
POST /v1/messages
Authorization: Bearer {ak}
Content-Type: application/json

{
  "model": "claude-3-opus-20240229",
  "messages": [...],
  "max_tokens": 4096
}
```

### 看板 API

```
GET /api/v1/dashboard/personal/summary
Authorization: Bearer {jwt_token}

Response:
{
  "code": 200,
  "data": {
    "daily_requests": 150,
    "daily_tokens": 105000,
    "daily_cost": 3.15,
    "quota_usage_rate": 75.5
  }
}
```

**详细说明**: 参见 `doc/api-design.md`

---

## 测试策略

### 单元测试

- **后端**: JUnit 5 + Mockito + Reactor Test
- **前端**: Vitest + @vue/test-utils

### 集成测试

- **后端**: Spring Boot Test + TestContainers
- **前端**: Cypress (E2E 测试)

### 性能测试

- **工具**: JMeter 或 Gatling
- **指标**: QPS、响应时间、错误率

---

## 部署指南

### Docker Compose 部署

```bash
# 1. 构建镜像
docker build -f docker/Dockerfile.backend -t ark-proxy-backend .
docker build -f docker/Dockerfile.frontend -t ark-proxy-frontend .

# 2. 启动服务
docker-compose -f docker/docker-compose.yml up -d

# 3. 查看日志
docker-compose -f docker/docker-compose.yml logs -f
```

### 手动部署

```bash
# 1. 后端部署
mvn clean package -DskipTests
java -jar target/ark-proxy-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod

# 2. 前端部署
cd frontend
npm run build
# 将 dist/ 目录部署到 Nginx 或 Apache
```

**详细说明**: 参见 `doc/deployment.md`

---

## 常见问题 (FAQ)

### Q1: 如何添加新的厂商支持？

A: 实现 `ProtocolAdapter` 接口，然后在 `application.yml` 中配置厂商信息。

### Q2: 如何优化性能？

A: 
- 使用 Redis 缓存热门数据
- 使用 InfluxDB 存储时序数据
- 使用 WebFlux 的响应式编程模型
- 配置合理的连接池大小

### Q3: 如何处理厂商 API 限流？

A: 
- 使用多 API Key 池 + 负载均衡
- 配置指数退避重试策略
- 使用 Resilience4j 实现熔断机制

---

## 联系方式和贡献指南

### 维护人

- **姓名**: WangMiao
- **角色**: Java Leader
- **邮箱**: wangmiao@example.com

### 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/xxx`)
3. 提交代码 (`git commit -m 'feat(xxx): description'`)
4. 推送到分支 (`git push origin feature/xxx`)
5. 创建 Pull Request

---

## 附录

### 相关文档

- [PRD 文档](./doc/PRD.md)
- [架构设计文档](./doc/architecture.md)
- [数据设计文档](./doc/data-design.md)
- [API 设计文档](./doc/api-design.md)
- [部署文档](./doc/deployment.md)

### 外部资源

- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
- [Vue 3 官方文档](https://vuejs.org/)
- [Element Plus 官方文档](https://element-plus.org/)
- [ECharts 官方文档](https://echarts.apache.org/)

---

**最后更新**: 2026-05-11  
**文档版本**: v1.0
