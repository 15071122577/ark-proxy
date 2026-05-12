package com.ark.proxy.model.enums;

/**
 * 预警状态枚举
 * <p>
 * 跟踪预警记录的生命周期：ACTIVE 表示预警生效中，RESOLVED 表示已处理/恢复。
 * </p>
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
public enum AlertStatus {

    /** 活跃：预警生效中，尚未处理 */
    ACTIVE("active", "活跃"),

    /** 已解决：预警已处理，配额/费用已恢复 */
    RESOLVED("resolved", "已解决");

    /** 状态编码 */
    private final String code;

    /** 状态显示名称 */
    private final String displayName;

    AlertStatus(String code, String displayName) {
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
     * @return 对应的 AlertStatus 枚举
     * @throws IllegalArgumentException 如果编码不存在
     */
    public static AlertStatus fromCode(String code) {
        for (AlertStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown alert status: " + code);
    }
}
