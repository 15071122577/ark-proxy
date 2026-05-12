package com.ark.proxy.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 注册请求 DTO
 *
 * <p>用于用户注册接口的请求参数封装，包含用户名、邮箱、密码和所属部门。</p>
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    /** 用户名 */
    private String username;

    /** 邮箱地址 */
    private String email;

    /** 密码 */
    private String password;

    /** 所属部门ID */
    private String departmentId;
}
