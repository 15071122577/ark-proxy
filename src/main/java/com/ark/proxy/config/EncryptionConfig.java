package com.ark.proxy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 加密配置属性
 *
 * <p>绑定配置前缀 {@code encryption}，用于数据加密相关的密钥配置，
 * 主要用于 AK/SK 等敏感信息的 AES 加密存储。</p>
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "encryption")
public class EncryptionConfig {

    /** AES 加密密钥（建议生产环境通过环境变量注入，长度须为 16/24/32 字节） */
    private String aesKey;
}
