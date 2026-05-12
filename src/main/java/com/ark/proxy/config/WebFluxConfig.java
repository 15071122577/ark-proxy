package com.ark.proxy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.web.reactive.config.CorsRegistry;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.WebFluxConfigurer;

/**
 * WebFlux 配置（CORS 跨域和编解码器）
 *
 * <p>配置 Spring WebFlux 全局设置，包括 CORS 跨域策略和 HTTP 消息编解码器。
 * CORS 允许前端跨域访问 API；编解码器增大内存缓冲区以支持 AI API 的大 JSON 请求体。</p>
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
@Configuration
@EnableWebFlux
public class WebFluxConfig implements WebFluxConfigurer {

    /**
     * 配置 CORS 跨域映射
     *
     * <p>允许所有来源、常用 HTTP 方法和必要请求头，支持携带凭证，
     * 预检请求缓存时间为 3600 秒。</p>
     *
     * @param registry CORS 注册器
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .exposedHeaders("Authorization", "Content-Type", "X-Request-Id")
                .allowCredentials(true)
                .maxAge(3600);
    }

    /**
     * 配置 HTTP 消息编解码器
     *
     * <p>增大默认内存缓冲区至 16MB，以支持 AI API 返回的大体积 JSON 响应体，
     * 避免因超出默认 256KB 限制而抛出 {@code DataBufferLimitException}。</p>
     *
     * @param configurer 服务端编解码器配置器
     */
    @Override
    public void configureHttpMessageCodecs(ServerCodecConfigurer configurer) {
        configurer.defaultCodecs().maxInMemorySize(16 * 1024 * 1024);
    }
}
