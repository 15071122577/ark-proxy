package com.ark.proxy.model.enums;

/**
 * 预警类型枚举
 * <p>
 * 区分配额类预警和费用类预警，以及对应的三个级别（提醒/警告/禁止）。
 * </p>
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
public enum AlertType {

    /** 配额提醒：用量达到配额的 70% */
    QUOTA_REMINDER("quota_reminder", "配额提醒"),

    /** 配额警告：用量达到配额的 90% */
    QUOTA_WARNING("quota_warning", "配额警告"),

    /** 配额禁止：用量达到配额的 100%，暂停服务 */
    QUOTA_BLOCK("quota_block", "配额禁止"),

    /** 费用提醒：日均费用超日常水平 2 倍 */
    COST_REMINDER("cost_reminder", "费用提醒"),

    /** 费用警告：日均费用超日常水平 5 倍 */
    COST_WARNING("cost_warning", "费用警告"),

    /** 系统错误：服务异常、超时等 */
    SYSTEM_ERROR("system_error", "系统错误");

    /** 预警类型编码 */
    private final String code;

    /** 预警类型显示名称 */
    private final String displayName;

    AlertType(String code, String displayName) {
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
     * @param code 预警类型编码
     * @return 对应的 AlertType 枚举
     * @throws IllegalArgumentException 如果编码不存在
     */
    public static AlertType fromCode(String code) {
        for (AlertType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown alert type: " + code);
    }
}
