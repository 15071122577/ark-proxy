package com.ark.proxy.model.enums;

/**
 * 协议类型枚举
 * <p>
 * 定义系统支持的 AI API 协议类型。
 * Anthropic Messages API 和 OpenAI Chat Completions API 是两种主流协议。
 * </p>
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
public enum ProtocolType {

    /** Anthropic Messages API 协议（/v1/messages） */
    ANTHROPIC_MESSAGES("anthropic_messages", "Anthropic Messages API"),

    /** OpenAI Chat Completions API 协议（/v1/chat/completions） */
    OPENAI_CHAT("openai_chat", "OpenAI Chat Completions API");

    /** 协议编码，对应数据库 provider_config.protocol_type */
    private final String code;

    /** 协议显示名称 */
    private final String displayName;

    ProtocolType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    /**
     * 获取协议编码
     * @return 协议编码字符串
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取协议显示名称
     * @return 协议显示名称
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 根据编码获取枚举实例
     * @param code 协议编码
     * @return 对应的 ProtocolType 枚举
     * @throws IllegalArgumentException 如果编码不存在
     */
    public static ProtocolType fromCode(String code) {
        for (ProtocolType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown protocol type: " + code);
    }
}
