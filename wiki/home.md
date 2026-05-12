# Home

Welcome to the **Multi-Protocol AI API Gateway** Wiki.

## Quick Navigation

| Page | Description |
|------|-------------|
| [Architecture](Architecture) | System architecture and design |
| [API-Documentation](API-Documentation) | API endpoint reference |
| [Deployment](Deployment) | Deployment guide |
| [Development-Standards](Development-Standards) | Coding standards |

## Getting Started

```bash
# Clone the repository
git clone https://github.com/15071122577/ark-proxy.git

# Start with Docker Compose
cd ark-proxy && docker-compose up -d
```

Access the console at http://localhost:3000

## Core Features

- **Multi-Protocol Adapter**: Anthropic, OpenAI, DeepSeek, Kimi, MiniMax, GLM
- **Secure AK/SK Management**: AES-256-GCM encryption, team-level allocation
- **Usage Monitoring**: Token-level metering, personal/team/system dashboards
- **Multi-Level Alerts**: 70%/90%/100% thresholds, multi-channel notifications
- **High Availability**: Load balancing, circuit breaker, exponential backoff retry
