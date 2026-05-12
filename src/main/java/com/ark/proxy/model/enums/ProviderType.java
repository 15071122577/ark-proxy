package com.ark.proxy.model.enums;

/**
 * 厂商类型枚举
 * <p>
 * 定义系统支持的 AI 厂商类型，每个厂商对应一个唯一的 code 和显示名称。
 * 新增厂商时需在此枚举中添加对应条目。
 * </p>
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
public enum ProviderType {

    /** Anthropic（Claude 系列） */
    ANTHROPIC("anthropic", "Anthropic (Claude)"),

    /** OpenAI（GPT 系列） */
    OPENAI("openai", "OpenAI (GPT)"),

    /** DeepSeek（DeepSeek-V2/Coder） */
    DEEPSEEK("deepseek", "DeepSeek"),

    /** Kimi / Moonshot（月之暗面） */
    KIMI("kimi", "Kimi (Moonshot)"),

    /** MiniMax（abab 系列） */
    MINIMAX("minimax", "MiniMax"),

    /** GLM / 智谱AI（GLM-4 系列） */
    GLM("glm", "GLM (智谱AI)");

    /** 厂商编码，对应数据库 provider_config.provider_id */
    private final String code;

    /** 厂商显示名称，用于前端展示 */
    private final String displayName;

    ProviderType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    /**
     * 获取厂商编码
     * @return 厂商编码字符串
     */
    public String getCode() {
        return code;
    }

    /**
     * 获取厂商显示名称
     * @return 厂商显示名称
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * 根据编码获取枚举实例
     * @param code 厂商编码
     * @return 对应的 ProviderType 枚举
     * @throws IllegalArgumentException 如果编码不存在
     */
    public static ProviderType fromCode(String code) {
        for (ProviderType type : values()) {
            if (type.code.equals(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown provider type: " + code);
    }
}
