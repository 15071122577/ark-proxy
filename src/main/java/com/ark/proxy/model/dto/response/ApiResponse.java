package com.ark.proxy.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 统一 API 响应 DTO
 *
 * <p>封装所有 API 接口的返回结果，包含状态码、消息、数据和请求元信息。
 * 提供 success/error 静态工厂方法，便于快速构建成功或失败响应。
 * 序列化时自动忽略 null 字段。</p>
 *
 * @param <T> 响应数据的泛型类型
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    /** 响应状态码（200 表示成功，其他为错误码） */
    private int code;

    /** 响应消息 */
    private String message;

    /** 响应数据 */
    private T data;

    /** 响应时间戳 */
    private LocalDateTime timestamp;

    /** 请求唯一标识，用于链路追踪 */
    private String requestId;

    /**
     * 构建成功响应（带数据）
     *
     * @param data 响应数据
     * @param <T>  响应数据类型
     * @return 包含数据的成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .code(200)
                .message("success")
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    /**
     * 构建成功响应（无数据）
     *
     * @param <T> 响应数据类型
     * @return 不包含数据的成功响应
     */
    public static <T> ApiResponse<T> success() {
        return success(null);
    }

    /**
     * 构建错误响应
     *
     * @param code    错误状态码
     * @param message 错误消息
     * @param <T>     响应数据类型
     * @return 错误响应
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return ApiResponse.<T>builder()
                .code(code)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
