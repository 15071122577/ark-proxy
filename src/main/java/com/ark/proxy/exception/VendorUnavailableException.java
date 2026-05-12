package com.ark.proxy.exception;

/**
 * 厂商不可用异常。
 * <p>
 * 当 AI 厂商服务不可达或响应异常时抛出此异常，携带 {@code providerId} 标识具体不可用的厂商。
 * 典型场景：厂商 API 超时、健康检查失败、熔断器打开后触发快速失败等。
 * </p>
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
public class VendorUnavailableException extends RuntimeException {

    /** 不可用的厂商标识，如 {@code anthropic}、{@code openai}、{@code deepseek} 等 */
    private final String providerId;

    /**
     * 构造厂商不可用异常。
     *
     * @param providerId 厂商标识，用于定位不可用的厂商
     * @param message    异常描述信息，说明不可用的具体原因
     */
    public VendorUnavailableException(String providerId, String message) {
        super(message);
        this.providerId = providerId;
    }

    /**
     * 获取不可用的厂商标识。
     *
     * @return 厂商标识
     */
    public String getProviderId() {
        return providerId;
    }
}
