package com.ark.proxy.util;

import java.util.UUID;

/**
 * ID 生成工具类。
 * <p>
 * 提供带业务前缀的唯一标识符生成方法，基于 UUID 去横线格式，确保全局唯一性。
 * 前缀设计便于在日志和数据库中快速识别实体类型，提高可读性和可追溯性。
 * </p>
 *
 * <p>生成规则：</p>
 * <ul>
 *   <li>通用 ID 格式：{@code prefix_16位随机十六进制字符}（如 {@code user_a1b2c3d4e5f67890}）</li>
 *   <li>AK 格式：{@code ak-24位随机十六进制字符}（如 {@code ak-a1b2c3d4e5f6789012345678}）</li>
 *   <li>SK 格式：{@code sk-32位随机十六进制字符}（完整 UUID 去横线）</li>
 * </ul>
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
public final class IdUtil {

    /**
     * 私有构造函数，防止实例化。
     * <p>
     * 工具类所有方法均为静态方法，不应被实例化。
     * </p>
     */
    private IdUtil() {}

    /**
     * 生成无横线的 UUID 字符串（32 位十六进制字符）。
     *
     * @return 去除横线的 UUID 字符串
     */
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 生成带前缀的 ID。
     * <p>
     * 格式为 {@code prefix_16位随机字符}，取 UUID 前 16 位以保证简洁性，
     * 碰撞概率极低（16 个十六进制字符 = 64 位熵）。
     * </p>
     *
     * @param prefix 业务前缀，如 {@code user}、{@code log}、{@code key} 等
     * @return 带前缀的唯一标识符
     */
    public static String prefixedId(String prefix) {
        return prefix + "_" + uuid().substring(0, 16);
    }

    /**
     * 生成用户 ID，前缀为 {@code user}。
     *
     * @return 用户唯一标识符，格式如 {@code user_a1b2c3d4e5f67890}
     */
    public static String userId() {
        return prefixedId("user");
    }

    /**
     * 生成日志 ID，前缀为 {@code log}。
     *
     * @return 日志唯一标识符，格式如 {@code log_a1b2c3d4e5f67890}
     */
    public static String logId() {
        return prefixedId("log");
    }

    /**
     * 生成配额 ID，前缀为 {@code quota}。
     *
     * @return 配额唯一标识符，格式如 {@code quota_a1b2c3d4e5f67890}
     */
    public static String quotaId() {
        return prefixedId("quota");
    }

    /**
     * 生成 API Key ID，前缀为 {@code key}。
     *
     * @return Key 唯一标识符，格式如 {@code key_a1b2c3d4e5f67890}
     */
    public static String keyId() {
        return prefixedId("key");
    }

    /**
     * 生成预警 ID，前缀为 {@code alert}。
     *
     * @return 预警唯一标识符，格式如 {@code alert_a1b2c3d4e5f67890}
     */
    public static String alertId() {
        return prefixedId("alert");
    }

    /**
     * 生成规则 ID，前缀为 {@code rule}。
     *
     * @return 规则唯一标识符，格式如 {@code rule_a1b2c3d4e5f67890}
     */
    public static String ruleId() {
        return prefixedId("rule");
    }

    /**
     * 生成请求 ID，前缀为 {@code req}。
     *
     * @return 请求唯一标识符，格式如 {@code req_a1b2c3d4e5f67890}
     */
    public static String requestId() {
        return prefixedId("req");
    }

    /**
     * 生成 Access Key（AK），前缀为 {@code ak-}。
     * <p>
     * AK 用于标识调用方身份，长度 24 位随机字符，兼顾唯一性和可读性。
     * </p>
     *
     * @return Access Key，格式如 {@code ak-a1b2c3d4e5f6789012345678}
     */
    public static String accessKey() {
        return "ak-" + uuid().substring(0, 24);
    }

    /**
     * 生成 Secret Key（SK），前缀为 {@code sk-}。
     * <p>
     * SK 用于请求签名验证，采用完整 UUID（32 位）以提供更高的安全强度。
     * </p>
     *
     * @return Secret Key，格式如 {@code sk-a1b2c3d4e5f6789012345678abcdef01}
     */
    public static String secretKey() {
        return "sk-" + uuid();
    }
}
