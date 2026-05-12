# Development Standards

**Version**: v1.0
**Date**: 2026-05-11
**Author**: WangMiao

---

## 1. Java Code Standards

### 1.1 Naming Conventions

| Element | Convention | Example |
|---------|-----------|---------|
| Class | UpperCamelCase | `ProxyService` |
| Method | lowerCamelCase | `handleAnthropicRequest` |
| Constant | UPPER_SNAKE_CASE | `MAX_RETRY_ATTEMPTS` |
| Package | lowercase | `com.ark.proxy.service` |
| Variable | lowerCamelCase | `userId`, `apiKey` |

### 1.2 Class Structure Order

```java
/**
 * Class description
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
// 1. Package declaration
package com.ark.proxy.service;

// 2. Import statements
import org.springframework.stereotype.Service;

// 3. Class annotation
@Service

// 4. Class declaration
public class ProxyService {

    // 5. Constants (static final)
    private static final Logger log = LoggerFactory.getLogger(ProxyService.class);

    // 6. Instance fields
    private final ProviderConfigRepository providerConfigRepository;

    // 7. Constructors
    public ProxyService(ProviderConfigRepository providerConfigRepository) {
        this.providerConfigRepository = providerConfigRepository;
    }

    // 8. Public methods
    public void doSomething() { }

    // 9. Private methods
    private void helperMethod() { }
}
```

### 1.3 Javadoc Requirements

Every class and public method must have Javadoc:

```java
/**
 * Handles incoming proxy requests and routes them to appropriate vendor adapters.
 *
 * @param request the incoming proxy request
 * @param ak the API key for authentication
 * @return the processed response from vendor
 * @throws VendorUnavailableException if all vendors are unavailable
 * @throws RateLimitException if rate limit is exceeded
 */
public ProxyResponse handleRequest(ProxyRequest request, String ak) {
    // implementation
}
```

### 1.4 Exception Handling

- Use custom business exceptions (`RateLimitException`, `VendorUnavailableException`)
- Global exception handler (`GlobalExceptionHandler`) for统一处理
- Log with trace_id and context: `log.error("msg: {}, trace_id: {}", msg, traceId)`

---

## 2. TypeScript / Vue Standards

### 2.1 Naming Conventions

| Element | Convention | Example |
|---------|-----------|---------|
| Component | PascalCase | `PersonalDashboard.vue` |
| File | camelCase | `httpClient.ts` |
| Type/Interface | PascalCase | `UserInfo`, `ApiResponse` |
| Constant | UPPER_SNAKE_CASE | `API_BASE_URL` |

### 2.2 Vue Component Structure

```vue
<!-- 1. Template -->
<template>
  <div class="page-container">
    <!-- content -->
  </div>
</template>

<!-- 2. Script -->
<script setup lang="ts">
// imports
import { ref, computed, onMounted } from 'vue'

// props
defineProps<{
  title: string
}>()

// emits
const emit = defineEmits<{
  'update': [value: string]
}>()

// state
const loading = ref(false)

// computed
const displayValue = computed(() => { })

// methods
async function loadData() { }

// lifecycle
onMounted(loadData)
</script>

<!-- 3. Style -->
<style scoped>
/* scoped CSS */
</style>
```

### 2.3 API Layer Pattern

```typescript
// src/api/xxx.ts
import http from '@/utils/http'
import type { RequestType, ResponseType } from '@/types/xxx'

/** Get xxx list */
export function getXxxList(params?: Record<string, any>): Promise<ResponseType[]> {
  return http.get('/xxx/list', { params })
}

/** Create xxx */
export function createXxx(data: RequestType): Promise<ResponseType> {
  return http.post('/xxx', data)
}
```

---

## 3. Git Commit Convention

Format: `<type>(<scope>): <subject>`

### Types

| Type | Description |
|------|-------------|
| `feat` | New feature |
| `fix` | Bug fix |
| `docs` | Documentation |
| `style` | Code formatting |
| `refactor` | Refactoring |
| `perf` | Performance improvement |
| `test` | Test related |
| `chore` | Build or auxiliary tool changes |

### Example

```bash
feat(proxy): add Anthropic protocol adapter

- Implement AnthropicAdapter for /v1/messages
- Add streaming response support
- Integrate with existing proxy core

Closes #123
```

---

## 4. Branch Management

| Branch | Description |
|--------|-------------|
| `main` | Production branch |
| `develop` | Development branch |
| `feature/*` | Feature branches |
| `bugfix/*` | Bug fix branches |
| `release/*` | Release branches |
| `hotfix/*` | Hotfix branches |

---

## 5. Code Review Checklist

- [ ] Javadoc/comments added for complex logic
- [ ] No hardcoded values (use constants/config)
- [ ] Exception handling in place
- [ ] Unit tests written for new features
- [ ] No security vulnerabilities (SQL injection, XSS, etc.)
- [ ] Naming conventions followed
- [ ] No unused imports/variables
