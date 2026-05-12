package com.ark.proxy.model.enums;

/**
 * 配额类型枚举
 * <p>
 * 定义配额的重置周期：日配额每天重置，月配额每月重置，年配额每年重置。
 * 当前系统默认使用月配额（MONTHLY）。
 * </p>
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
public enum QuotaType {

    /** 日配额：每天零点重置 */
    DAILY("daily", "日配额"),

    /** 月配额：每月1日零点重置（默认） */
    MONTHLY("monthly", "月配额"),

    /** 年配额：每年1月1日零点重置 */
    YEARLY("yearly", "年配额");

    /** 配额类型编码 */
    private final String code;

    /** 配额类型显示名称 */
    private final String displayName;

    QuotaType(String code, String displayName) {
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
     * @param code 配额类型编码
     * @return 对应的 QuotaType 枚举
     * @throws IllegalArgumentException 如果编码不存在
     */
    public static QuotaType fromCode(String code) {
        for (QuotaType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown quota type: " + code);
    }
}
