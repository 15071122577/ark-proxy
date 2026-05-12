package com.ark.proxy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * JWT 配置属性
 *
 * <p>绑定配置前缀 {@code jwt}，用于 JWT 令牌的签名密钥和有效期配置。</p>
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    /** JWT 签名密钥（建议生产环境通过环境变量注入，不要明文写在配置文件中） */
    private String secret;

    /** JWT 令牌有效时长（单位：秒，默认 86400 即 24 小时） */
    private long expiration = 86400;
}
