package com.ark.proxy.exception;

/**
 * 限流异常。
 * <p>
 * 当请求频率超过系统配置的限流阈值时抛出此异常，携带错误码 {@code code} 用于标识具体的限流类型。
 * 典型场景：单用户 QPS 超限、全局限流触发、厂商 API 调用频率受限等。
 * </p>
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
public class RateLimitException extends RuntimeException {

    /** 错误码，用于标识限流的具体类型，默认为 RATE_LIMIT_EXCEEDED */
    private final String code;

    /**
     * 构造限流异常，使用默认错误码 {@code RATE_LIMIT_EXCEEDED}。
     *
     * @param message 异常描述信息，说明限流的具体原因
     */
    public RateLimitException(String message) {
        super(message);
        this.code = "RATE_LIMIT_EXCEEDED";
    }

    /**
     * 构造限流异常，指定自定义错误码。
     *
     * @param code    错误码，如 {@code USER_RATE_LIMIT_EXCEEDED}、{@code GLOBAL_RATE_LIMIT_EXCEEDED} 等
     * @param message 异常描述信息，说明限流的具体原因
     */
    public RateLimitException(String code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 获取错误码。
     *
     * @return 限流错误码
     */
    public String getCode() {
        return code;
    }
}
