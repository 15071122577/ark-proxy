package com.ark.proxy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 响应式配置
 *
 * <p>配置 Redis 响应式模板 Bean，提供纯字符串和 JSON 对象两种序列化策略的
 * {@link ReactiveRedisTemplate}，用于缓存、限流和会话管理等场景。</p>
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
@Configuration
public class RedisConfig {

    /**
     * 创建纯字符串类型的响应式 Redis 模板
     *
     * <p>所有 Key 和 Value 均使用 {@link StringRedisSerializer} 序列化，
     * 适用于纯字符串缓存场景（如限流计数器、Token 黑名单等）。</p>
     *
     * @param connectionFactory Redis 响应式连接工厂
     * @return 纯字符串类型的响应式 Redis 模板
     */
    @Bean
    public ReactiveRedisTemplate<String, String> reactiveRedisTemplate(
            ReactiveRedisConnectionFactory connectionFactory) {
        StringRedisSerializer serializer = new StringRedisSerializer();
        RedisSerializationContext<String, String> context = RedisSerializationContext
                .<String, String>newSerializationContext(serializer)
                .key(serializer)
                .value(serializer)
                .hashKey(serializer)
                .hashValue(serializer)
                .build();
        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }

    /**
     * 创建 JSON 对象类型的响应式 Redis 模板
     *
     * <p>Key 使用 {@link StringRedisSerializer}，Value 使用 {@link Jackson2JsonRedisSerializer}，
     * 适用于需要缓存 Java 对象的场景（如用户会话、配置信息等）。</p>
     *
     * @param connectionFactory Redis 响应式连接工厂
     * @return JSON 对象类型的响应式 Redis 模板
     */
    @Bean
    public ReactiveRedisTemplate<String, Object> reactiveRedisObjectTemplate(
            ReactiveRedisConnectionFactory connectionFactory) {
        StringRedisSerializer keySerializer = new StringRedisSerializer();
        Jackson2JsonRedisSerializer<Object> valueSerializer = new Jackson2JsonRedisSerializer<>(Object.class);
        RedisSerializationContext<String, Object> context = RedisSerializationContext
                .<String, Object>newSerializationContext(keySerializer)
                .key(keySerializer)
                .value(valueSerializer)
                .hashKey(keySerializer)
                .hashValue(valueSerializer)
                .build();
        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }
}
