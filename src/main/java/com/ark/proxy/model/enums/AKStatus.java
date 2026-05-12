package com.ark.proxy.model.enums;

/**
 * AK（Access Key）状态枚举
 * <p>
 * 定义 API Key 在系统中的生命周期状态。
 * QUOTA_EXHAUSTED 表示配额耗尽，REVOKED 表示已被管理员吊销。
 * </p>
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
public enum AKStatus {

    /** 可用：AK 正常工作，可接收请求 */
    ACTIVE("active", "可用"),

    /** 配额耗尽：AK 对应的配额已用完，需等待配额重置或管理员扩容 */
    QUOTA_EXHAUSTED("quota_exhausted", "配额耗尽"),

    /** 已吊销：AK 被管理员主动吊销，不可恢复 */
    REVOKED("revoked", "已吊销");

    /** 状态编码 */
    private final String code;

    /** 状态显示名称 */
    private final String displayName;

    AKStatus(String code, String displayName) {
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
     * @return 对应的 AKStatus 枚举
     * @throws IllegalArgumentException 如果编码不存在
     */
    public static AKStatus fromCode(String code) {
        for (AKStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown AK status: " + code);
    }
}
