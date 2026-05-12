package com.ark.proxy.model.enums;

/**
 * 用户状态枚举
 * <p>
 * 控制用户账号是否可用。INACTIVE 状态的用户无法登录和使用系统。
 * </p>
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
public enum UserStatus {

    /** 活跃：用户可正常登录和使用系统 */
    ACTIVE("active", "活跃"),

    /** 停用：用户被管理员停用，无法登录 */
    INACTIVE("inactive", "停用");

    /** 状态编码 */
    private final String code;

    /** 状态显示名称 */
    private final String displayName;

    UserStatus(String code, String displayName) {
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
     * @return 对应的 UserStatus 枚举
     * @throws IllegalArgumentException 如果编码不存在
     */
    public static UserStatus fromCode(String code) {
        for (UserStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown user status: " + code);
    }
}
