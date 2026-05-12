package com.ark.proxy.model.enums;

/**
 * 预警严重程度枚举
 * <p>
 * 定义四级预警体系，对应不同通知方式和处理逻辑：
 * <ul>
 *   <li>INFO — 配额达 70%，站内信+邮件通知</li>
 *   <li>WARNING — 配额达 90%，站内信+邮件+企业微信</li>
 *   <li>CRITICAL — 配额达 100%，暂停服务，全渠道通知</li>
 *   <li>EMERGENCY — 系统故障/安全漏洞，紧急响应</li>
 * </ul>
 * </p>
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
public enum AlertSeverity {

    /** 提醒：用量达到阈值，需关注 */
    INFO("info", "提醒"),

    /** 警告：用量接近上限，需采取措施 */
    WARNING("warning", "警告"),

    /** 严重：配额耗尽，服务暂停，需人工介入 */
    CRITICAL("critical", "严重"),

    /** 紧急：系统故障或安全漏洞，立即响应 */
    EMERGENCY("emergency", "紧急");

    /** 严重程度编码 */
    private final String code;

    /** 严重程度显示名称 */
    private final String displayName;

    AlertSeverity(String code, String displayName) {
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
     * @param code 严重程度编码
     * @return 对应的 AlertSeverity 枚举
     * @throws IllegalArgumentException 如果编码不存在
     */
    public static AlertSeverity fromCode(String code) {
        for (AlertSeverity severity : values()) {
            if (severity.code.equals(code)) {
                return severity;
            }
        }
        throw new IllegalArgumentException("Unknown alert severity: " + code);
    }
}
