# 方舟 Code Plan 中转站系统 - 部署文档

**版本**: v1.0  
**日期**: 2026-05-11  
**作者**: 运维团队  
**状态**: 待评审  

---

## 目录

1. [部署架构](#1-部署架构)
2. [环境要求](#2-环境要求)
3. [Docker Compose 部署](#3-docker-compose-部署)
4. [手动部署](#4-手动部署)
5. [配置管理](#5-配置管理)
6. [监控和日志](#6-监控和日志)
7. [备份和恢复](#7-备份和恢复)
8. [故障排查](#8-故障排查)

---

## 1. 部署架构

### 1.1 系统架构图

```
┌─────────────────────────────────────────────────────────────┐
│                      用户层                                 │
│  前端 (Vue 3)  │  后端 API (Spring Boot)  │  代理服务   │
└─────────────────────────────────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    负载均衡层                               │
│                  Nginx (可选)                              │
└─────────────────────────────────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    应用层                                   │
│  Spring Boot (Backend)  │  Vue 3 (Frontend)              │
└─────────────────────────────────────────────────────────────┘
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    数据层                                   │
│  MySQL 8.0  │  InfluxDB 2.x  │  Redis 7+             │
└─────────────────────────────────────────────────────────────┘
```

### 1.2 部署拓扑

| 环境 | 部署方式 | 说明 |
|------|---------|------|
| 开发环境 | Docker Compose | 快速启动所有依赖服务 |
| 测试环境 | Docker Compose + 手动部署 | 模拟生产环境 |
| 生产环境 | Kubernetes (推荐) 或 Docker Swarm | 高可用部署 |

---

## 2. 环境要求

### 2.1 硬件要求

| 环境 | CPU | 内存 | 磁盘 | 网络 |
|------|-----|------|------|------|
| 开发环境 | 4 核 | 8 GB | 50 GB | 1 Mbps |
| 测试环境 | 8 核 | 16 GB | 100 GB | 10 Mbps |
| 生产环境 | 16 核 | 32 GB | 500 GB | 100 Mbps |

### 2.2 软件要求

| 组件 | 版本 | 说明 |
|------|------|------|
| JDK | 17+ | OpenJDK 17 推荐 |
| Node.js | 18+ | 20 LTS 推荐 |
| MySQL | 8.0+ | 关系型数据库 |
| InfluxDB | 2.x | 时序数据库 |
| Redis | 7+ | 缓存和会话存储 |
| Nginx | 1.20+ | 反向代理（可选） |
| Docker | 20.10+ | 容器化部署 |

---

## 3. Docker Compose 部署

### 3.1 Docker Compose 配置文件

`docker/docker-compose.yml`:

```yaml
version: '3.8'

services:
  # MySQL 数据库
  mysql:
    image: mysql:8.0
    container_name: ark-proxy-mysql
    restart: unless-stopped
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: ark_proxy
      MYSQL_USER: ark_proxy
      MYSQL_PASSWORD: ark_proxy_password
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./scripts/init-db.sql:/docker-entrypoint-initdb.d/init-db.sql
    networks:
      - ark-proxy-network
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      timeout: 20s
      retries: 10

  # InfluxDB 时序数据库
  influxdb:
    image: influxdb:2.7
    container_name: ark-proxy-influxdb
    restart: unless-stopped
    environment:
      INFLUXDB_DB: ark_proxy
      INFLUXDB_ADMIN_USER: admin
      INFLUXDB_ADMIN_PASSWORD: adminpassword
      INFLUXDB_USER: ark_proxy
      INFLUXDB_USER_PASSWORD: ark_proxy_password
    ports:
      - "8086:8086"
    volumes:
      - influxdb_data:/var/lib/influxdb2
    networks:
      - ark-proxy-network
    healthcheck:
      test: ["CMD", "influx", "ping"]
      timeout: 20s
      retries: 10

  # Redis 缓存
  redis:
    image: redis:7-alpine
    container_name: ark-proxy-redis
    restart: unless-stopped
    command: redis-server --appendonly yes --requirepass redis_password
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    networks:
      - ark-proxy-network
    healthcheck:
      test: ["CMD", "redis-cli", "-a", "redis_password", "ping"]
      timeout: 20s
      retries: 10

  # 后端服务
  backend:
    build:
      context: ..
      dockerfile: docker/Dockerfile.backend
    container_name: ark-proxy-backend
    restart: unless-stopped
    environment:
      SPRING_PROFILES_ACTIVE: docker
      MYSQL_HOST: mysql
      MYSQL_PORT: 3306
      MYSQL_DATABASE: ark_proxy
      MYSQL_USERNAME: ark_proxy
      MYSQL_PASSWORD: ark_proxy_password
      INFLUXDB_URL: http://influxdb:8086
      INFLUXDB_TOKEN: influxdb-token
      INFLUXDB_ORG: ark-proxy-org
      INFLUXDB_BUCKET: ark_proxy_realtime
      REDIS_HOST: redis
      REDIS_PORT: 6379
      REDIS_PASSWORD: redis_password
    ports:
      - "8080:8080"
    depends_on:
      mysql:
        condition: service_healthy
      influxdb:
        condition: service_healthy
      redis:
        condition: service_healthy
    networks:
      - ark-proxy-network

  # 前端服务
  frontend:
    build:
      context: ../frontend
      dockerfile: ../docker/Dockerfile.frontend
    container_name: ark-proxy-frontend
    restart: unless-stopped
    ports:
      - "3000:3000"
    depends_on:
      - backend
    networks:
      - ark-proxy-network

  # Nginx 反向代理（可选）
  nginx:
    image: nginx:alpine
    container_name: ark-proxy-nginx
    restart: unless-stopped
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./docker/nginx.conf:/etc/nginx/nginx.conf
    depends_on:
      - backend
      - frontend
    networks:
      - ark-proxy-network
    profiles:
      - with-nginx

networks:
  ark-proxy-network:
    driver: bridge

volumes:
  mysql_data:
  influxdb_data:
  redis_data:
```

### 3.2 启动服务

```bash
# 1. 克隆项目
git clone <repository-url>
cd ark-proxy

# 2. 启动服务（后台运行）
docker-compose -f docker/docker-compose.yml up -d

# 3. 查看日志
docker-compose -f docker/docker-compose.yml logs -f

# 4. 停止服务
docker-compose -f docker/docker-compose.yml down

# 5. 停止服务并删除数据卷（慎用！）
docker-compose -f docker/docker-compose.yml down -v
```

### 3.3 验证部署

```bash
# 1. 检查服务状态
docker-compose -f docker/docker-compose.yml ps

# 2. 测试后端 API
curl http://localhost:8080/api/v1/health

# 3. 测试前端
curl http://localhost:3000

# 4. 检查 MySQL
docker exec -it ark-proxy-mysql mysql -uark_proxy -park_proxy_password ark_proxy -e "SHOW TABLES;"

# 5. 检查 Redis
docker exec -it ark-proxy-redis redis-cli -a redis_password ping
```

---

## 4. 手动部署

### 4.1 后端手动部署

#### 4.1.1 编译打包

```bash
# 1. 进入后端目录
cd ark-proxy

# 2. 编译打包（跳过测试）
mvn clean package -DskipTests

# 3. 生成的 JAR 文件
ls -lh target/ark-proxy-0.0.1-SNAPSHOT.jar
```

#### 4.1.2 启动后端服务

```bash
# 1. 使用默认配置启动
java -jar target/ark-proxy-0.0.1-SNAPSHOT.jar

# 2. 使用指定配置文件启动
java -jar target/ark-proxy-0.0.1-SNAPSHOT.jar --spring.config.location=file:/path/to/application-prod.yml

# 3. 使用环境变量启动
export SPRING_PROFILES_ACTIVE=prod
export MYSQL_HOST=localhost
export MYSQL_PORT=3306
export MYSQL_DATABASE=ark_proxy
export MYSQL_USERNAME=root
export MYSQL_PASSWORD=password
java -jar target/ark-proxy-0.0.1-SNAPSHOT.jar

# 4. 后台运行
nohup java -jar target/ark-proxy-0.0.1-SNAPSHOT.jar > logs/app.log 2>&1 &
```

#### 4.1.3 使用 systemd 管理后端服务

`/etc/systemd/system/ark-proxy-backend.service`:

```ini
[Unit]
Description=Ark Proxy Backend Service
After=network.target mysql.service redis.service

[Service]
Type=simple
User=ark-proxy
WorkingDirectory=/opt/ark-proxy
ExecStart=/usr/bin/java -jar /opt/ark-proxy/target/ark-proxy-0.0.1-SNAPSHOT.jar
SuccessExitStatus=143
Restart=always
RestartSec=10
Environment="SPRING_PROFILES_ACTIVE=prod"
Environment="MYSQL_HOST=localhost"
Environment="MYSQL_PORT=3306"
Environment="MYSQL_DATABASE=ark_proxy"
Environment="MYSQL_USERNAME=ark_proxy"
Environment="MYSQL_PASSWORD=password"
Environment="INFLUXDB_URL=http://localhost:8086"
Environment="REDIS_HOST=localhost"
Environment="REDIS_PORT=6379"

[Install]
WantedBy=multi-user.target
```

**管理命令**:

```bash
# 1. 重新加载 systemd 配置
sudo systemctl daemon-reload

# 2. 启动服务
sudo systemctl start ark-proxy-backend

# 3. 停止服务
sudo systemctl stop ark-proxy-backend

# 4. 重启服务
sudo systemctl restart ark-proxy-backend

# 5. 查看服务状态
sudo systemctl status ark-proxy-backend

# 6. 设置开机自启
sudo systemctl enable ark-proxy-backend

# 7. 查看日志
sudo journalctl -u ark-proxy-backend -f
```

### 4.2 前端手动部署

#### 4.2.1 构建前端

```bash
# 1. 进入前端目录
cd ark-proxy/frontend

# 2. 安装依赖
npm install

# 3. 构建生产版本
npm run build

# 4. 构建产物在 dist/ 目录
ls -lh dist/
```

#### 4.2.2 部署到 Nginx

`/etc/nginx/conf.d/ark-proxy.conf`:

```nginx
server {
    listen 80;
    server_name ark-proxy.example.com;

    # 前端静态文件
    location / {
        root /opt/ark-proxy/frontend/dist;
        try_files $uri $uri/ /index.html;
        index index.html;
    }

    # 后端 API 反向代理
    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }

    # WebSocket 反向代理
    location /ws/ {
        proxy_pass http://localhost:8080/ws/;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

**管理命令**:

```bash
# 1. 测试 Nginx 配置
sudo nginx -t

# 2. 重新加载 Nginx 配置
sudo nginx -s reload

# 3. 停止 Nginx
sudo nginx -s stop

# 4. 启动 Nginx
sudo nginx
```

---

## 5. 配置管理

### 5.1 配置文件结构

```
ark-proxy/
├── src/main/resources/
│   ├── application.yml                  # 主配置文件
│   ├── application-dev.yml              # 开发环境配置
│   └── application-prod.yml           # 生产环境配置
```

### 5.2 主配置文件示例

`application.yml`:

```yaml
# 应用配置
spring:
  application:
    name: ark-proxy
  profiles:
    active: ${SPRING_PROFILES_ACTIVE:dev}

# 服务器配置
server:
  port: 8080
  servlet:
    context-path: /

# 数据库配置
spring:
  datasource:
    url: jdbc:mysql://${MYSQL_HOST:localhost}:${MYSQL_PORT:3306}/${MYSQL_DATABASE:ark_proxy}?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
    username: ${MYSQL_USERNAME:root}
    password: ${MYSQL_PASSWORD:password}
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: validate  # 生产环境使用 validate，开发环境可以使用 update
    show-sql: false
    properties:
      hibernate:
        format_sql: true

# InfluxDB 配置
influx:
  url: ${INFLUXDB_URL:http://localhost:8086}
  token: ${INFLUXDB_TOKEN:influxdb-token}
  org: ${INFLUXDB_ORG:ark-proxy-org}
  bucket: ${INFLUXDB_BUCKET:ark_proxy_realtime}

# Redis 配置
spring:
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}
      timeout: 5000ms

# JWT 配置
jwt:
  secret: ${JWT_SECRET:my-secret-key}
  expiration: 86400  # 24 小时

# 日志配置
logging:
  level:
    com.ark.proxy: INFO
    org.springframework: WARN
  file:
    name: logs/ark-proxy.log
    max-size: 10MB
    max-history: 30
```

### 5.3 使用环境变量覆盖配置

**推荐**: 生产环境使用环境变量或配置中心管理敏感信息。

```bash
# 示例：使用环境变量启动
export SPRING_PROFILES_ACTIVE=prod
export MYSQL_HOST=db.example.com
export MYSQL_PORT=3306
export MYSQL_DATABASE=ark_proxy
export MYSQL_USERNAME=ark_proxy
export MYSQL_PASSWORD=secret_password
export INFLUXDB_URL=http://influxdb.example.com:8086
export INFLUXDB_TOKEN=secret-token
export REDIS_HOST=redis.example.com
export REDIS_PORT=6379
export REDIS_PASSWORD=secret_password
export JWT_SECRET=super-secret-key

java -jar target/ark-proxy-0.0.1-SNAPSHOT.jar
```

---

## 6. 监控和日志

### 6.1 健康检查端点

```
GET /actuator/health
```

**响应示例**:

```json
{
  "status": "UP",
  "components": {
    "db": {
      "status": "UP",
      "details": {
        "database": "MySQL",
        "validationQuery": "isValid()"
      }
    },
    "redis": {
      "status": "UP",
      "details": {
        "version": "7.2.0"
      }
    },
    "influxdb": {
      "status": "UP",
      "details": {
        "version": "2.7.0"
      }
    }
  }
}
```

### 6.2 日志管理

#### 日志配置

`application.yml`:

```yaml
logging:
  level:
    com.ark.proxy: INFO
    org.springframework: WARN
    io.r2dbc: WARN
  file:
    name: logs/ark-proxy.log
    max-size: 10MB
    max-history: 30
  pattern:
    console: "%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n"
    file: "%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n"
```

#### 使用 Logback 配置

`logback-spring.xml`:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <!-- 控制台输出 -->
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- 文件输出 -->
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/ark-proxy.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <fileNamePattern>logs/ark-proxy.%d{yyyy-MM-dd}.log</fileNamePattern>
            <maxHistory>30</maxHistory>
            <totalSizeCap>10GB</totalSizeCap>
        </rollingPolicy>
        <encoder>
            <pattern>%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>

    <!-- 异步输出 -->
    <appender name="ASYNC_FILE" class="ch.qos.logback.classic.AsyncAppender">
        <queueSize>512</queueSize>
        <discardingThreshold>0</discardingThreshold>
        <appender-ref ref="FILE"/>
    </appender>

    <!-- 日志级别 -->
    <root level="INFO">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="ASYNC_FILE"/>
    </root>

    <!-- 指定包日志级别 -->
    <logger name="com.ark.proxy" level="DEBUG"/>
    <logger name="org.springframework" level="WARN"/>
</configuration>
```

### 6.3 监控指标

#### Spring Boot Actuator

`application.yml`:

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  endpoint:
    health:
      show-details: always
  metrics:
    export:
      prometheus:
        enabled: true
```

#### Prometheus + Grafana

`prometheus.yml`:

```yaml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'ark-proxy'
    metrics_path: '/actuator/prometheus'
    static_configs:
      - targets: ['localhost:8080']
```

---

## 7. 备份和恢复

### 7.1 数据库备份

#### MySQL 备份

```bash
# 1. 全量备份
mysqldump -u root -p --single-transaction --routines --triggers --all-databases > backup_$(date +%Y%m%d_%H%M%S).sql

# 2. 备份指定数据库
mysqldump -u root -p --single-transaction ark_proxy > ark_proxy_backup_$(date +%Y%m%d_%H%M%S).sql

# 3. 压缩备份
mysqldump -u root -p --single-transaction ark_proxy | gzip > ark_proxy_backup_$(date +%Y%m%d_%H%M%S).sql.gz

# 4. 定时备份（crontab）
# 每天凌晨 2 点备份
0 2 * * * mysqldump -u root -pPASSWORD --single-transaction ark_proxy | gzip > /backup/ark_proxy_$(date +\%Y\%m\%d).sql.gz
```

#### MySQL 恢复

```bash
# 1. 恢复全量备份
mysql -u root -p < backup_20260511_020000.sql

# 2. 恢复指定数据库
mysql -u root -p ark_proxy < ark_proxy_backup_20260511_020000.sql

# 3. 恢复压缩备份
gunzip < ark_proxy_backup_20260511_020000.sql.gz | mysql -u root -p ark_proxy
```

### 7.2 InfluxDB 备份

```bash
# 1. 备份 InfluxDB
influx backup /backup/influxdb_$(date +%Y%m%d_%H%M%S)

# 2. 恢复 InfluxDB
influx restore /backup/influxdb_20260511_020000
```

---

## 8. 故障排查

### 8.1 常见问题

#### 后端服务无法启动

**现象**: 后端服务启动失败

**排查步骤**:

```bash
# 1. 查看日志
tail -f logs/ark-proxy.log

# 2. 检查端口占用
lsof -i :8080

# 3. 检查数据库连接
mysql -h $MYSQL_HOST -P $MYSQL_PORT -u $MYSQL_USERNAME -p$MYSQL_PASSWORD $MYSQL_DATABASE

# 4. 检查 Redis 连接
redis-cli -h $REDIS_HOST -p $REDIS_PORT -a $REDIS_PASSWORD ping
```

#### 前端无法访问后端 API

**现象**: 前端页面正常，但 API 请求失败

**排查步骤**:

```bash
# 1. 检查后端服务是否启动
curl http://localhost:8080/api/v1/health

# 2. 检查 CORS 配置
# 查看后端日志是否有 CORS 错误

# 3. 检查 Nginx 反向代理配置
sudo nginx -t
```

### 8.2 性能问题

#### 数据库查询慢

**排查步骤**:

```sql
-- 1. 查看慢查询日志
SHOW VARIABLES LIKE 'slow_query_log';
SHOW VARIABLES LIKE 'long_query_time';

-- 2. 分析查询计划
EXPLAIN SELECT * FROM usage_logs WHERE user_id = 'user_001';

-- 3. 查看索引使用情况
SHOW INDEX FROM usage_logs;
```

#### Redis 内存耗尽

**排查步骤**:

```bash
# 1. 查看 Redis 内存使用情况
redis-cli -a $REDIS_PASSWORD INFO memory

# 2. 查看键空间
redis-cli -a $REDIS_PASSWORD Info keyspace

# 3. 清理过期键
redis-cli -a $REDIS_PASSWORD FLUSHDB
```

---

## 附录：部署检查清单

### 部署前检查

- [ ] 硬件资源满足要求（CPU/内存/磁盘）
- [ ] 软件环境已安装（JDK/Node.js/MySQL/InfluxDB/Redis）
- [ ] 数据库已初始化（执行 init-db.sql）
- [ ] 配置文件已正确设置（数据库/Redis/InfluxDB 连接信息）
- [ ] 敏感信息已使用环境变量或配置中心管理
- [ ] 日志目录已创建并赋予权限
- [ ] 端口未被占用

### 部署后检查

- [ ] 后端服务健康检查通过（`/actuator/health`）
- [ ] 前端页面正常访问
- [ ] API 代理功能正常
- [ ] 数据库读写正常
- [ ] Redis 缓存正常
- [ ] 日志输出正常
- [ ] 监控指标正常（Prometheus + Grafana）

---

**文档版本历史**

| 版本 | 日期 | 修改人 | 修改内容 |
|------|------|--------|----------|
| v1.0 | 2026-05-11 | 运维团队 | 初始版本 |

---

**审批记录**

- [ ] 运维 Leader 审批
- [ ] DBA 审批
- [ ] 安全团队审批
