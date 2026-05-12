package com.ark.proxy.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 登录请求 DTO
 *
 * <p>用于用户登录接口的请求参数封装，包含用户名和密码。</p>
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /** 用户名 */
    private String username;

    /** 密码 */
    private String password;
}
