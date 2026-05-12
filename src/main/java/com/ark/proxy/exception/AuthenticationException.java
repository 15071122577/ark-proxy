package com.ark.proxy.exception;

/**
 * 认证异常。
 * <p>
 * 当用户身份认证失败时抛出此异常，携带错误码 {@code code} 用于标识具体的认证失败类型。
 * 典型场景：AK/SK 验证失败、JWT Token 过期或无效、权限不足等。
 * </p>
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
public class AuthenticationException extends RuntimeException {

    /** 错误码，用于标识认证失败的具体类型，默认为 AUTHENTICATION_FAILED */
    private final String code;

    /**
     * 构造认证异常，使用默认错误码 {@code AUTHENTICATION_FAILED}。
     *
     * @param message 异常描述信息，说明认证失败的具体原因
     */
    public AuthenticationException(String message) {
        super(message);
        this.code = "AUTHENTICATION_FAILED";
    }

    /**
     * 构造认证异常，指定自定义错误码。
     *
     * @param code    错误码，如 {@code TOKEN_EXPIRED}、{@code INVALID_AK}、{@code PERMISSION_DENIED} 等
     * @param message 异常描述信息，说明认证失败的具体原因
     */
    public AuthenticationException(String code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * 获取错误码。
     *
     * @return 认证错误码
     */
    public String getCode() {
        return code;
    }
}
