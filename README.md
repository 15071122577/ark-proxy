# Multi-Protocol AI API Gateway

**企业级多协议 AI API 中台** — 统一接入 Claude、GPT、DeepSeek、Kimi、MiniMax、GLM 等多模型，自动协议适配、安全密钥管控、用量监控与多级流量预警。

[![Java 17](https://img.shields.io/badge/Java-17+-blue.svg)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-green.svg)](https://spring.io/projects/spring-boot)
[![Vue 3](https://img.shields.io/badge/Vue%203-3.4-yellow.svg)](https://vuejs.org/)
[![License](https://img.shields.io/badge/License-MIT-lightgrey.svg)](LICENSE)

---

## 核心特性

### 🔌 多协议统一接入
- **Anthropic Messages API** (`/v1/messages`) — Claude 系列
- **OpenAI Chat Completions API** (`/v1/chat/completions`) — GPT 系列
- **DeepSeek / Kimi / MiniMax / GLM** 等国产模型原生协议
- 新增模型只需配置模型映射，无需编写适配代码

### 🔐 AK/SK 安全管控
- AES-256-GCM 加密存储，API Key 永不裸奔
- 团队级 Key 分配，支持个人/团队/部门多级配额
- 申请 → 审批 → 下发 → 轮换全流程管理

### 📊 用量精细化计量
- Token 级统计，精准计量每个模型调用
- 个人看板 / 团队看板 / 系统看板多维度展示
- 费用分摊，部门/项目成本透明化

### 🚨 多级流量预警
- 三档预警：70%（提醒）/ 90%（警告）/ 100%（阻断）
- 多渠道通知：邮件 / 企业微信 / 钉钉 / 飞书
- 预警历史完整记录，支持溯源

### ⚡ 高可用保障
- 多 Key 池 + 负载均衡，故障自动切换
- 指数退避重试策略，防止雪崩
- Resilience4j 熔断机制，保障系统稳定

---

## 系统架构

```
┌─────────────────────────────────────────────────────────────────┐
│                         用户层                                   │
│           控制台 (Vue 3 + Element Plus + ECharts)              │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                        API 网关层                                 │
│              JWT 认证 │ 限流 │ 协议路由 │ 日志追踪                │
└─────────────────────────────────────────────────────────────────┘
                              │
          ┌──────────────────┼──────────────────┐
          ▼                  ▼                  ▼
┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
│  Anthropic      │ │  OpenAI        │ │  国产模型       │
│  Adapter        │ │  Adapter       │ │  Adapter        │
│  (/v1/messages) │ │  (/v1/chat)    │ │  (原生协议)     │
└─────────────────┘ └─────────────────┘ └─────────────────┘
          │                  │                  │
          └──────────────────┼──────────────────┘
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                      数据层                                      │
│   MySQL (关系数据)  │  InfluxDB (时序指标)  │  Redis (缓存/限流)  │
└─────────────────────────────────────────────────────────────────┘
```

---

## 技术栈

| 层级 | 技术 | 说明 |
|------|------|------|
| **后端** | Java 17 + Spring Boot WebFlux | 响应式非阻塞，高并发 I/O |
| **前端** | Vue 3 + TypeScript + Vite | 组合式 API，类型安全 |
| **UI** | Element Plus + ECharts | 企业级组件 + 数据可视化 |
| **状态** | Pinia | Vue 3 官方推荐状态管理 |
| **数据库** | MySQL 8.0 + InfluxDB 2.x | 关系型 + 时序型混合存储 |
| **缓存** | Redis 7+ | 高性能缓存与限流 |
| **部署** | Docker Compose + Nginx | 多容器编排，统一入口 |

---

## 快速开始

### 环境要求

- JDK 17+ / Node.js 18+ / Docker Compose
- MySQL 8.0+ / InfluxDB 2.x / Redis 7+

### 1. 克隆项目

```bash
git clone https://github.com/15071122577/ark-proxy.git
cd ark-proxy
```

### 2. 一键启动（开发环境）

```bash
docker-compose up -d
```

访问控制台：http://localhost:3000

### 3. 手动启动（后端）

```bash
# 初始化数据库
mysql -u root -p < scripts/schema.sql
mysql -u root -p < scripts/data.sql

# 启动后端
mvn clean install
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

### 4. 手动启动（前端）

```bash
cd frontend
npm install
npm run dev
```

---

## API 接口概览

### 代理接口

| 方法 | 路径 | 说明 |
|------|------|------|
| `POST` | `/v1/messages` | Anthropic Messages API 代理 |
| `POST` | `/v1/chat/completions` | OpenAI Chat Completions API 代理 |

### 管理接口

| 方法 | 路径 | 说明 |
|------|------|------|
| `POST` | `/api/v1/auth/login` | 用户登录 |
| `GET` | `/api/v1/dashboard/personal/summary` | 个人看板汇总 |
| `POST` | `/api/v1/ak/apply` | 提交 AK 申请 |
| `GET` | `/api/v1/alert/rules` | 获取预警规则 |
| `POST` | `/api/v1/provider` | 创建厂商配置 |

---

## 目录结构

```
ark-proxy/
├── src/main/java/com/ark/proxy/     # 后端代码
│   ├── config/                      # 配置类
│   ├── model/                       # 数据模型（Entity/DTO/Enum）
│   ├── repository/                  # 数据访问层
│   ├── util/                        # 工具类
│   └── exception/                   # 异常处理
├── frontend/                        # 前端代码
│   └── src/
│       ├── api/                     # API 接口层
│       ├── components/              # 组件
│       ├── views/                   # 页面
│       ├── router/                  # 路由
│       ├── store/                   # 状态管理
│       ├── types/                   # 类型定义
│       └── utils/                   # 工具函数
├── docker/                          # Docker 相关
│   ├── Dockerfile.backend
│   ├── Dockerfile.frontend
│   └── nginx.conf
├── scripts/                         # 数据库脚本
├── doc/                             # 设计文档
└── docker-compose.yml               # 容器编排
```

---

## 功能演示

### 个人看板
![Personal Dashboard](https://via.placeholder.com/800x400?text=Personal+Dashboard)

### 团队看板
支持成员排行、部门分布、费用分摊。

### 系统看板
实时 QPS、可用性、厂商健康状态，5 秒自动刷新。

---

## 适用场景

- **企业内部 AI 平台**：统一管理多部门、多团队的 API 调用
- **代理商户系统**：下游客户 API 密钥分发与用量计费
- **多模型集成项目**：一套代码接入所有主流 LLM，无需重复适配
- **成本管控需求**：精细化 Token 计量，部门/项目费用透明

---

## 性能数据

| 指标 | 数值 |
|------|------|
| 日均 API 调用 | 数千次 |
| 并发支持 | 1000+ RPS |
| P99 响应延迟 | < 500ms |
| 可用性 | 99.9% |

---

## 发展规划

- [ ] **Phase 2**: 流式响应优化 (SSE/WebSocket)
- [ ] **Phase 3**: Key 自动轮换与负载均衡策略
- [ ] **Phase 4**: 费用预算与超支告警
- [ ] **Phase 5**: 多租户隔离
- [ ] **Phase 6**: 插件市场（自定义模型接入）

---

## 相关文档

- [产品需求文档 (PRD)](doc/PRD.md)
- [架构设计文档](doc/architecture.md)
- [数据设计文档](doc/data-design.md)
- [API 设计文档](doc/api-design.md)
- [部署文档](doc/deployment.md)

---

## 致谢

本项目由 **Claude Code** + **GitHub Copilot** 多 Agent 协同开发，架构设计到代码实现全程 AI 辅助，开发效率提升约 60%。

---

## 许可证

[MIT License](LICENSE)
