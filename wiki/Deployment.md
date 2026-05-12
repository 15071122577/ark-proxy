# Deployment Guide

**Version**: v1.0
**Date**: 2026-05-11
**Author**: WangMiao

---

## 1. Deployment Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                      User Layer                             │
│           Frontend (Vue 3) on Nginx                        │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                    Nginx Reverse Proxy                        │
│          Port 80/443 → Backend :8080 / Frontend :3000       │
└─────────────────────────────────────────────────────────────┘
                            │
                            ▼
┌─────────────────────────────────────────────────────────────┐
│                      Application Layer                      │
│         Backend (Spring Boot) + Frontend (Vite)             │
└─────────────────────────────────────────────────────────────┘
                            │
        ┌───────────────────┼───────────────────┐
        ▼                   ▼                   ▼
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   MySQL     │    │  InfluxDB   │    │    Redis    │
│   8.0+     │    │    2.x      │    │     7+     │
└─────────────┘    └─────────────┘    └─────────────┘
```

---

## 2. Environment Requirements

| Component | Version | Minimum Resources |
|-----------|---------|------------------|
| JDK | 17+ | 2 CPU, 4GB RAM |
| Node.js | 18+ | 1 CPU, 2GB RAM |
| MySQL | 8.0+ | 2 CPU, 4GB RAM |
| InfluxDB | 2.x | 2 CPU, 4GB RAM |
| Redis | 7+ | 1 CPU, 2GB RAM |
| Docker | 20.10+ | - |
| Docker Compose | 2.0+ | - |

---

## 3. Docker Compose Deployment

### 3.1 Quick Start

```bash
# Clone the repository
git clone https://github.com/15071122577/ark-proxy.git
cd ark-proxy

# Start all services
docker-compose up -d

# Check service status
docker-compose ps

# View logs
docker-compose logs -f backend
```

### 3.2 Docker Compose Configuration

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: ark_proxy
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
      - ./scripts/schema.sql:/docker-entrypoint-initdb.d/1-schema.sql
      - ./scripts/data.sql:/docker-entrypoint-initdb.d/2-data.sql
    healthcheck:
      test: ["CMD", "mysqladmin", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

  influxdb:
    image: influxdb:2.7
    ports:
      - "8086:8086"
    volumes:
      - influxdb_data:/var/lib/influxdb2
    environment:
      DOCKER_INFLUXDB_INIT_MODE: setup
      DOCKER_INFLUXDB_INIT_USERNAME: admin
      DOCKER_INFLUXDB_INIT_PASSWORD: ${INFLUXDB_PASSWORD}
      DOCKER_INFLUXDB_INIT_ORG: ark-proxy
      DOCKER_INFLUXDB_INIT_BUCKET: metrics
    healthcheck:
      test: ["CMD", "influx", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

  redis:
    image: redis:7
    ports:
      - "6379:6379"
    volumes:
      - redis_data:/data
    healthcheck:
      test: ["CMD", "redis-cli", "ping"]
      interval: 10s
      timeout: 5s
      retries: 5

  backend:
    build:
      context: .
      dockerfile: docker/Dockerfile.backend
    ports:
      - "8080:8080"
    environment:
      SPRING_PROFILES_ACTIVE: docker
      MYSQL_HOST: mysql
      REDIS_HOST: redis
      INFLUXDB_URL: http://influxdb:8086
    depends_on:
      mysql:
        condition: service_healthy
      redis:
        condition: service_healthy
      influxdb:
        condition: service_healthy

  frontend:
    build:
      context: .
      dockerfile: docker/Dockerfile.frontend
    ports:
      - "3000:80"
    depends_on:
      - backend

  nginx:
    image: nginx:alpine
    ports:
      - "80:80"
      - "443:443"
    volumes:
      - ./docker/nginx.conf:/etc/nginx/nginx.conf:ro
    depends_on:
      - backend
      - frontend

volumes:
  mysql_data:
  influxdb_data:
  redis_data:
```

---

## 4. Manual Deployment

### 4.1 Backend

```bash
# Build
mvn clean package -DskipTests

# Database setup
mysql -u root -p < scripts/schema.sql
mysql -u root -p < scripts/data.sql

# Run
java -jar target/ark-proxy-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### 4.2 Frontend

```bash
cd frontend

# Install dependencies
npm install

# Development
npm run dev

# Production build
npm run build
# Deploy dist/ to Nginx
```

---

## 5. Configuration

### 5.1 Environment Variables

| Variable | Description | Default |
|---------|-------------|---------|
| `MYSQL_HOST` | MySQL host | localhost |
| `MYSQL_PORT` | MySQL port | 3306 |
| `MYSQL_DATABASE` | Database name | ark_proxy |
| `REDIS_HOST` | Redis host | localhost |
| `REDIS_PORT` | Redis port | 6379 |
| `INFLUXDB_URL` | InfluxDB URL | http://localhost:8086 |
| `JWT_SECRET` | JWT signing key | - |
| `ENCRYPTION_KEY` | AES encryption key | - |

### 5.2 Profile Selection

| Profile | Use Case |
|---------|---------|
| `dev` | Local development |
| `docker` | Docker Compose |
| `prod` | Production |

---

## 6. Health Check

```bash
# Backend health
curl http://localhost:8080/actuator/health

# All services
curl http://localhost:8080/api/v1/health
```

---

## 7. Backup and Restore

### MySQL

```bash
# Backup
mysqldump -u root -p ark_proxy > backup.sql

# Restore
mysql -u root -p ark_proxy < backup.sql
```

### InfluxDB

```bash
# Backup
influx backup /backup/influxdb-$(date +%Y%m%d)

# Restore
influx restore /backup/influxdb-20260511
```
