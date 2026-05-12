package com.ark.proxy.model.enums;

/**
 * 通知状态枚举
 * <p>
 * 跟踪预警通知的发送状态：PENDING 待发送、SENT 已发送、FAILED 发送失败需重试。
 * </p>
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
public enum NotificationStatus {

    /** 待发送：通知已创建，等待发送 */
    PENDING("pending", "待发送"),

    /** 已发送：通知已成功送达 */
    SENT("sent", "已发送"),

    /** 发送失败：通知发送失败，需重试或人工处理 */
    FAILED("failed", "发送失败");

    /** 状态编码 */
    private final String code;

    /** 状态显示名称 */
    private final String displayName;

    NotificationStatus(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * 根据编码获取枚举实例
     * @param code 通知状态编码
     * @return 对应的 NotificationStatus 枚举
     * @throws IllegalArgumentException 如果编码不存在
     */
    public static NotificationStatus fromCode(String code) {
        for (NotificationStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown notification status: " + code);
    }
}
