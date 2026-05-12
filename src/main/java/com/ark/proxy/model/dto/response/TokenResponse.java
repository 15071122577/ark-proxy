package com.ark.proxy.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Token 响应 DTO
 *
 * <p>登录成功后返回的 Token 信息，包含访问令牌、有效期和用户基本信息。</p>
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenResponse {

    /** 访问令牌（JWT） */
    private String accessToken;

    /** 令牌类型（默认 Bearer） */
    private String tokenType;

    /** 令牌有效时长（单位：秒） */
    private long expiresIn;

    /** 用户ID */
    private String userId;

    /** 用户名 */
    private String username;

    /** 用户角色 */
    private String role;
}
