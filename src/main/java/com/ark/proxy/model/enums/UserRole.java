package com.ark.proxy.model.enums;

/**
 * 用户角色枚举
 * <p>
 * 定义系统中的用户角色层级，admin 拥有全部权限，user 为普通用户，guest 为只读访客。
 * </p>
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
public enum UserRole {

    /** 管理员：拥有系统全部管理权限 */
    ADMIN("admin", "管理员"),

    /** 普通用户：可申请 AK、查看个人看板 */
    USER("user", "普通用户"),

    /** 访客：仅可查看公开信息，不可申请 AK */
    GUEST("guest", "访客");

    /** 角色编码 */
    private final String code;

    /** 角色显示名称 */
    private final String displayName;

    UserRole(String code, String displayName) {
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
     * @param code 角色编码
     * @return 对应的 UserRole 枚举
     * @throws IllegalArgumentException 如果编码不存在
     */
    public static UserRole fromCode(String code) {
        for (UserRole role : values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unknown user role: " + code);
    }
}
