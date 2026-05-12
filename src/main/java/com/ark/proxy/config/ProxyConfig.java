package com.ark.proxy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 代理配置属性
 *
 * <p>绑定配置前缀 {@code proxy}，用于 API 代理请求的连接超时、读写超时和内存缓冲区大小等配置。</p>
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "proxy")
public class ProxyConfig {

    /** 连接超时时间（单位：毫秒，默认 10000 即 10 秒） */
    private int connectTimeout = 10000;

    /** 读取超时时间（单位：毫秒，默认 120000 即 120 秒，适配 AI 模型长耗时响应） */
    private int readTimeout = 120000;

    /** 写入超时时间（单位：毫秒，默认 10000 即 10 秒） */
    private int writeTimeout = 10000;

    /** 最大内存缓冲区大小（默认 16MB，用于缓存代理请求/响应体） */
    private String maxInMemorySize = "16MB";
}
