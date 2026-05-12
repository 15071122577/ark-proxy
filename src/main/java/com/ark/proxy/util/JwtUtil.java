package com.ark.proxy.util;

import com.ark.proxy.config.JwtConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT 工具类。
 * <p>
 * 基于 HMAC-SHA256 算法签发和验证 JSON Web Token，用于系统内部用户认证。
 * Token 中携带 userId、username、role 等声明信息，过期时间通过 {@link JwtConfig} 统一配置。
 * </p>
 *
 * <p>使用方式：</p>
 * <pre>
 *   // 签发 Token
 *   String token = jwtUtil.generateToken("user_xxx", "admin", "ADMIN");
 *
 *   // 验证并解析
 *   if (jwtUtil.validateToken(token)) {
 *       String userId = jwtUtil.getUserIdFromToken(token);
 *   }
 * </pre>
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
@Component
@RequiredArgsConstructor
public class JwtUtil {

    /** JWT 配置，包含密钥和过期时间等参数 */
    private final JwtConfig jwtConfig;

    /**
     * 根据配置的密钥字符串构建 HMAC-SHA 签名密钥。
     * <p>
     * 密钥长度必须满足 HMAC-SHA256 的最低要求（256 位 / 32 字节），
     * 由 {@code jwtConfig.getSecret()} 提供。
     * </p>
     *
     * @return HMAC-SHA 签名密钥
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成 JWT Token。
     * <p>
     * 将用户基本信息（userId、username、role）写入 Payload 声明，设置签发时间和过期时间，
     * 使用 HMAC-SHA256 签名。subject 统一设置为 userId，便于快速提取。
     * </p>
     *
     * @param userId   用户标识
     * @param username 用户名
     * @param role     用户角色，如 {@code ADMIN}、{@code USER} 等
     * @return 签名后的 JWT Token 字符串
     */
    public String generateToken(String userId, String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("role", role);

        Date now = new Date();
        // 过期时间 = 当前时间 + 配置的过期秒数（jwtConfig.getExpiration 单位为秒）
        Date expiryDate = new Date(now.getTime() + jwtConfig.getExpiration() * 1000);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userId)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 解析 JWT Token，提取 Payload 声明。
     * <p>
     * 解析过程中会自动验证签名和过期时间，若签名不匹配或 Token 已过期将抛出异常。
     * </p>
     *
     * @param token JWT Token 字符串
     * @return Token 中的 Payload 声明
     * @throws io.jsonwebtoken.ExpiredJwtException      Token 已过期
     * @throws io.jsonwebtoken.SignatureException        签名验证失败
     * @throws io.jsonwebtoken.MalformedJwtException     Token 格式不合法
     */
    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从 Token 中获取用户ID。
     * <p>
     * 用户ID 存储在 JWT 的 subject 字段中，直接从解析结果获取。
     * </p>
     *
     * @param token JWT Token 字符串
     * @return 用户标识
     * @throws io.jsonwebtoken.JwtException Token 无效或已过期
     */
    public String getUserIdFromToken(String token) {
        return parseToken(token).getSubject();
    }

    /**
     * 验证 Token 是否有效。
     * <p>
     * 通过尝试解析 Token 来验证其有效性：签名正确、未过期则返回 true。
     * 任何解析异常（签名不匹配、过期、格式错误等）均视为无效，返回 false。
     * </p>
     *
     * @param token JWT Token 字符串
     * @return {@code true} 如果 Token 有效；{@code false} 如果 Token 无效或已过期
     */
    public boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
