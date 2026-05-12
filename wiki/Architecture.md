# Architecture

**Version**: v1.0
**Date**: 2026-05-11
**Author**: WangMiao

---

## 1. System Overview

The Multi-Protocol AI API Gateway provides unified access to multiple LLM providers (Claude, GPT, DeepSeek, Kimi, MiniMax, GLM) through automatic protocol adaptation, secure key management, and comprehensive usage monitoring.

## 2. Architecture Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│                         User Layer                               │
│           Console (Vue 3 + Element Plus + ECharts)              │
└─────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────┐
│                        API Gateway Layer                         │
│          JWT Auth │ Rate Limit │ Protocol Routing │ Logging     │
└─────────────────────────────────────────────────────────────────┘
                              │
          ┌──────────────────┼──────────────────┐
          ▼                  ▼                  ▼
┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐
│  Anthropic      │ │  OpenAI        │ │  Domestic LLMs   │
│  Adapter        │ │  Adapter       │ │  Adapter         │
│  (/v1/messages) │ │  (/v1/chat)    │ │  (Native APIs)  │
└─────────────────┘ └─────────────────┘ └─────────────────┘
          │                  │                  │
          └──────────────────┼──────────────────┘
                             ▼
┌─────────────────────────────────────────────────────────────────┐
│                      Data Layer                                 │
│   MySQL (Relational)  │  InfluxDB (Time-Series)  │  Redis      │
└─────────────────────────────────────────────────────────────────┘
```

## 3. Technology Stack

| Layer | Technology | Version | Purpose |
|-------|------------|---------|---------|
| Backend | Java Spring Boot WebFlux | 3.2+ | Reactive non-blocking I/O |
| Frontend | Vue 3 + TypeScript | 3.4+ | Type-safe UI |
| UI | Element Plus | 2.x | Enterprise components |
| Charts | ECharts + vue-echarts | 5.x | Data visualization |
| State | Pinia | 2.1+ | State management |
| DB | MySQL | 8.0+ | Relational data |
| TSDB | InfluxDB | 2.x | Time-series metrics |
| Cache | Redis | 7+ | Caching & rate limiting |
| Deploy | Docker Compose + Nginx | - | Container orchestration |

## 4. Backend Module Structure

```
src/main/java/com/ark/proxy/
├── ArkProxyApplication.java              # Entry point
├── config/                               # Configuration
│   ├── RedisConfig.java                  # Redis reactive templates
│   ├── InfluxDBConfig.java              # InfluxDB client
│   ├── WebFluxConfig.java               # CORS, codec
│   ├── JwtConfig.java                    # JWT configuration
│   ├── EncryptionConfig.java            # AES-256-GCM
│   └── ProxyConfig.java                  # Proxy settings
├── model/
│   ├── entity/                           # JPA entities
│   ├── dto/                              # Request/Response DTOs
│   └── enums/                            # Enumerations
├── repository/
│   ├── mysql/                            # MySQL repositories
│   ├── influxdb/                         # InfluxDB repositories
│   └── redis/                            # Redis repositories
├── util/                                 # Utilities
│   ├── JwtUtil.java                     # JWT sign/verify
│   ├── EncryptUtil.java                 # AES-256-GCM
│   └── IdUtil.java                       # ID generators
└── exception/                            # Exception handling
```

## 5. Frontend Module Structure

```
frontend/src/
├── api/                                  # API layer
│   ├── auth.ts                           # Authentication
│   ├── dashboard.ts                      # Dashboards
│   ├── ak.ts                            # AK management
│   ├── alert.ts                         # Alerts
│   └── provider.ts                      # Provider config
├── components/
│   ├── layout/                          # Layout components
│   │   ├── AppLayout.vue
│   │   ├── AppSidebar.vue
│   │   └── AppHeader.vue
│   └── common/                          # Shared components
│       ├── StatCard.vue
│       └── ChartCard.vue
├── views/
│   ├── dashboard/                       # Dashboard pages
│   ├── ak/                              # AK management pages
│   ├── admin/                           # Admin pages
│   ├── alert/                           # Alert pages
│   └── login/                           # Login page
├── router/                              # Vue Router
├── store/                               # Pinia stores
├── types/                               # TypeScript types
└── utils/                               # Utility functions
```

## 6. Database Design

### Core Tables

| Table | Purpose |
|-------|---------|
| `users` | User account management |
| `user_quotas` | Per-user quota allocation |
| `usage_logs` | Token-level API call logs (partitioned) |
| `daily_usage_summary` | Daily aggregated usage |
| `provider_config` | LLM provider configuration |
| `model_mapping` | Model name standardization |
| `api_keys` | API key pool management |
| `alert_rules` | Alert rule definitions |
| `alert_history` | Alert event history |

## 7. Security

- **Key Encryption**: AES-256-GCM for API key storage
- **Password Hashing**: bcrypt
- **Authentication**: JWT with HS256
- **Transport**: HTTPS enforced in production
