package com.ark.proxy.model.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 错误响应 DTO（兼容 Anthropic/OpenAI 错误格式）
 *
 * <p>统一错误响应封装，外层包含错误类型和错误详情。
 * 内部 {@link ErrorDetail} 兼容 Anthropic 和 OpenAI 两种错误格式，
 * Anthropic 格式使用 type + message，OpenAI 格式使用 code + message + param。</p>
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    /** 错误类型（如 "error"，兼容 Anthropic 格式） */
    private String type;

    /** 错误详情 */
    private ErrorDetail error;

    /**
     * 错误详情内部类
     *
     * <p>兼容 Anthropic 和 OpenAI 两种错误格式。
     * Anthropic 格式：type + message；OpenAI 格式：code + message + param。</p>
     *
     * @author WangMiao
     * @date 2026-05-11
     * @version 1.0
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorDetail {

        /** 错误类型（Anthropic 格式，如 "invalid_request_error"） */
        private String type;

        /** 错误码（OpenAI 格式，如 "rate_limit_exceeded"） */
        private String code;

        /** 错误消息 */
        private String message;

        /** 错误参数（OpenAI 格式，标识触发错误的请求参数名） */
        private String param;

        /** 错误详细信息（附加调试信息） */
        private String detail;
    }
}
