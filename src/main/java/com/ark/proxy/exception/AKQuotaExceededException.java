package com.ark.proxy.exception;

/**
 * AK 配额超限异常。
 * <p>
 * 当用户（AK 持有者）的 API 调用量超过分配的配额阈值时抛出此异常，
 * 携带 {@code userId} 和 {@code usageRate} 用于标识超限用户及当前使用率。
 * 典型场景：日配额耗尽、月度 Token 用量超限、预警规则触发后拒绝请求等。
 * </p>
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
public class AKQuotaExceededException extends RuntimeException {

    /** 超限的用户标识 */
    private final String userId;

    /** 当前配额使用率（百分比），如 95.5 表示已使用 95.5% */
    private final double usageRate;

    /**
     * 构造 AK 配额超限异常。
     *
     * @param userId    超限的用户标识
     * @param usageRate 当前配额使用率（百分比）
     * @param message   异常描述信息，说明超限的具体情况
     */
    public AKQuotaExceededException(String userId, double usageRate, String message) {
        super(message);
        this.userId = userId;
        this.usageRate = usageRate;
    }

    /**
     * 获取超限的用户标识。
     *
     * @return 用户标识
     */
    public String getUserId() {
        return userId;
    }

    /**
     * 获取当前配额使用率。
     *
     * @return 配额使用率（百分比）
     */
    public double getUsageRate() {
        return usageRate;
    }
}
