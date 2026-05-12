package com.ark.proxy.exception;

import com.ark.proxy.model.dto.response.ApiResponse;
import com.ark.proxy.model.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 全局异常处理器。
 * <p>
 * 基于 Spring WebFlux 的 {@code @RestControllerAdvice} 实现，统一拦截各类业务异常并返回标准化的
 * {@link ApiResponse} 响应体。确保所有异常场景下客户端都能收到一致的错误格式，避免暴露内部堆栈信息。
 * </p>
 *
 * <p>处理顺序（按异常类型）：</p>
 * <ol>
 *   <li>{@link AuthenticationException} → 401 Unauthorized</li>
 *   <li>{@link RateLimitException} → 429 Too Many Requests</li>
 *   <li>{@link AKQuotaExceededException} → 403 Forbidden</li>
 *   <li>{@link VendorUnavailableException} → 503 Service Unavailable</li>
 *   <li>{@link Exception}（兜底）→ 500 Internal Server Error</li>
 * </ol>
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理认证异常，返回 401 状态码。
     * <p>
     * 认证失败时记录 WARN 级别日志（不记录堆栈，避免日志膨胀），返回统一错误响应。
     * </p>
     *
     * @param ex      认证异常实例
     * @param exchange WebFlux 交换上下文
     * @return 401 Unauthorized 响应，包含标准化错误信息
     */
    @ExceptionHandler(AuthenticationException.class)
    public Mono<ResponseEntity<ApiResponse<ErrorResponse>>> handleAuthenticationException(
            AuthenticationException ex, ServerWebExchange exchange) {
        log.warn("Authentication failed: {}", ex.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .type("error")
                .error(ErrorResponse.ErrorDetail.builder()
                        .type("authentication_error")
                        .code(ex.getCode())
                        .message(ex.getMessage())
                        .build())
                .build();
        return Mono.just(ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(401, ex.getMessage())));
    }

    /**
     * 处理限流异常，返回 429 状态码。
     * <p>
     * 限流触发时记录 WARN 日志，客户端应按 Retry-After 头进行退避重试。
     * </p>
     *
     * @param ex      限流异常实例
     * @param exchange WebFlux 交换上下文
     * @return 429 Too Many Requests 响应，包含标准化错误信息
     */
    @ExceptionHandler(RateLimitException.class)
    public Mono<ResponseEntity<ApiResponse<ErrorResponse>>> handleRateLimitException(
            RateLimitException ex, ServerWebExchange exchange) {
        log.warn("Rate limit exceeded: {}", ex.getMessage());
        return Mono.just(ResponseEntity
                .status(HttpStatus.TOO_MANY_REQUESTS)
                .body(ApiResponse.error(429, ex.getMessage())));
    }

    /**
     * 处理 AK 配额超限异常，返回 403 状态码。
     * <p>
     * 配额超限时记录 WARN 日志（含用户ID和当前使用率），便于后续用量审计。
     * </p>
     *
     * @param ex      配额超限异常实例
     * @param exchange WebFlux 交换上下文
     * @return 403 Forbidden 响应，包含标准化错误信息
     */
    @ExceptionHandler(AKQuotaExceededException.class)
    public Mono<ResponseEntity<ApiResponse<ErrorResponse>>> handleAKQuotaExceededException(
            AKQuotaExceededException ex, ServerWebExchange exchange) {
        log.warn("Quota exceeded for user {}: usage rate {}%", ex.getUserId(), ex.getUsageRate());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .type("error")
                .error(ErrorResponse.ErrorDetail.builder()
                        .type("quota_error")
                        .code("QUOTA_EXCEEDED")
                        .message(ex.getMessage())
                        .detail("当前配额使用率: " + String.format("%.1f", ex.getUsageRate()) + "%")
                        .build())
                .build();
        return Mono.just(ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.error(403, ex.getMessage())));
    }

    /**
     * 处理厂商不可用异常，返回 503 状态码。
     * <p>
     * 厂商不可用时记录 ERROR 日志（含厂商标识），触发告警以便运维介入。
     * </p>
     *
     * @param ex      厂商不可用异常实例
     * @param exchange WebFlux 交换上下文
     * @return 503 Service Unavailable 响应，包含标准化错误信息
     */
    @ExceptionHandler(VendorUnavailableException.class)
    public Mono<ResponseEntity<ApiResponse<ErrorResponse>>> handleVendorUnavailableException(
            VendorUnavailableException ex, ServerWebExchange exchange) {
        log.error("Vendor unavailable [{}]: {}", ex.getProviderId(), ex.getMessage());
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ApiResponse.error(503, ex.getMessage())));
    }

    /**
     * 兜底异常处理，捕获所有未被上述处理器拦截的异常，返回 500 状态码。
     * <p>
     * 记录 ERROR 日志并打印完整堆栈，便于排查未知问题。
     * 返回给客户端的消息固定为 "Internal Server Error"，避免暴露内部实现细节。
     * </p>
     *
     * @param ex      未预期的异常实例
     * @param exchange WebFlux 交换上下文
     * @return 500 Internal Server Error 响应，包含标准化错误信息
     */
    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ApiResponse<ErrorResponse>>> handleGenericException(
            Exception ex, ServerWebExchange exchange) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return Mono.just(ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(500, "Internal Server Error")));
    }
}
