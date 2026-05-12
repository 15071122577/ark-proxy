package com.ark.proxy.model.enums;

/**
 * 请求状态枚举
 * <p>
 * 用于用量日志（usage_logs）中记录每次 API 请求的执行结果。
 * </p>
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
public enum RequestStatus {

    /** 成功：请求正常完成，已记录 Token 用量 */
    SUCCESS("success", "成功"),

    /** 失败：请求出错（厂商错误/超时/限流等） */
    FAILED("failed", "失败");

    /** 状态编码 */
    private final String code;

    /** 状态显示名称 */
    private final String displayName;

    RequestStatus(String code, String displayName) {
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
     * @param code 状态编码
     * @return 对应的 RequestStatus 枚举
     * @throws IllegalArgumentException 如果编码不存在
     */
    public static RequestStatus fromCode(String code) {
        for (RequestStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown request status: " + code);
    }
}
