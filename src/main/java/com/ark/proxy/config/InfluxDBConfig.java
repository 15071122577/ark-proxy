package com.ark.proxy.config;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * InfluxDB 配置
 *
 * <p>配置 InfluxDB 客户端连接属性和 Bean，用于时序数据存储（实时监控指标、请求延迟、Token 用量等）。
 * 绑定配置前缀 {@code influx}。</p>
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "influx")
public class InfluxDBConfig {

    /** InfluxDB 服务地址（如 http://localhost:8086） */
    private String url;

    /** InfluxDB 认证 Token */
    private String token;

    /** InfluxDB 组织名称 */
    private String org;

    /** InfluxDB Bucket 名称（类似数据库概念） */
    private String bucket;

    /**
     * 创建 InfluxDB 客户端 Bean
     *
     * @return 已初始化的 {@link InfluxDBClient} 实例
     */
    @Bean
    public InfluxDBClient influxDBClient() {
        return InfluxDBClientFactory.create(url, token.toCharArray(), org, bucket);
    }
}
