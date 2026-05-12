package com.ark.proxy.util;

import com.ark.proxy.config.EncryptionConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * AES-256-GCM 加解密工具类。
 * <p>
 * 用于 AK/SK 等敏感信息的加密存储。采用 AES-256-GCM 认证加密模式，兼顾机密性和完整性。
 * 密文格式为 {@code enc:Base64(IV+ciphertext)}，其中：
 * </p>
 * <ul>
 *   <li>{@code enc:} 前缀用于标识已加密数据，与明文数据区分</li>
 *   <li>IV（初始化向量）长度 12 字节，每次加密随机生成，确保相同明文产生不同密文</li>
 *   <li>GCM Tag 长度 128 位，提供认证保护</li>
 *   <li>密钥通过 {@link EncryptionConfig} 外部配置，长度不足 32 字节时自动补零</li>
 * </ul>
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EncryptUtil {

    /** AES/GCM/NoPadding 算法标识，使用 NoPadding 避免 PKCS5Padding 的填充攻击风险 */
    private static final String ALGORITHM = "AES/GCM/NoPadding";

    /** GCM 模式 IV 长度（12 字节），NIST 推荐值 */
    private static final int GCM_IV_LENGTH = 12;

    /** GCM 认证 Tag 长度（128 位），提供最高强度的完整性校验 */
    private static final int GCM_TAG_LENGTH = 128;

    /** 加密配置，提供 AES-256 密钥 */
    private final EncryptionConfig encryptionConfig;

    /**
     * 根据 {@link EncryptionConfig} 中配置的密钥字符串构建 AES-256 密钥。
     * <p>
     * 配置的密钥字符串按 UTF-8 编码转为字节数组，若长度不足 32 字节则补零，
     * 超过 32 字节则截断。保证最终密钥长度始终为 256 位以满足 AES-256 要求。
     * </p>
     *
     * @return AES-256 密钥规格
     */
    private SecretKeySpec getKey() {
        byte[] keyBytes = encryptionConfig.getAesKey().getBytes(StandardCharsets.UTF_8);
        // AES-256 需要 32 字节密钥，不足补零，超出截断
        byte[] validKey = new byte[32];
        System.arraycopy(keyBytes, 0, validKey, 0, Math.min(keyBytes.length, 32));
        return new SecretKeySpec(validKey, "AES");
    }

    /**
     * AES-256-GCM 加密。
     * <p>
     * 加密流程：
     * <ol>
     *   <li>生成 12 字节随机 IV</li>
     *   <li>使用 AES-256-GCM 加密明文，产生密文（含 16 字节 Tag）</li>
     *   <li>将 IV 和密文拼接后 Base64 编码，添加 {@code enc:} 前缀</li>
     * </ol>
     * </p>
     *
     * @param plaintext 待加密的明文字符串
     * @return 格式为 {@code enc:Base64(IV+ciphertext)} 的密文字符串
     * @throws RuntimeException 加密过程中发生任何异常时抛出
     */
    public String encrypt(String plaintext) {
        try {
            // 每次加密生成随机 IV，确保相同明文产生不同密文，防止密文模式分析攻击
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, getKey(), gcmSpec);

            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            // 格式: Base64(IV + ciphertext)，解密时先提取 IV 再解密密文
            byte[] combined = new byte[iv.length + ciphertext.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(ciphertext, 0, combined, iv.length, ciphertext.length);

            return "enc:" + Base64.getEncoder().encodeToString(combined);
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed", e);
        }
    }

    /**
     * AES-256-GCM 解密。
     * <p>
     * 解密流程：
     * <ol>
     *   <li>校验密文格式，必须以 {@code enc:} 开头</li>
     *   <li>Base64 解码后分离 IV（前 12 字节）和密文（剩余字节）</li>
     *   <li>使用 AES-256-GCM 解密，GCM 模式自动验证 Tag 完整性</li>
     * </ol>
     * </p>
     *
     * @param encryptedText 格式为 {@code enc:Base64(IV+ciphertext)} 的密文字符串
     * @return 解密后的明文字符串
     * @throws IllegalArgumentException 密文格式不合法（缺少 enc: 前缀）
     * @throws RuntimeException         解密过程中发生任何异常时抛出
     */
    public String decrypt(String encryptedText) {
        try {
            if (!encryptedText.startsWith("enc:")) {
                throw new IllegalArgumentException("Invalid encrypted text format");
            }

            // 去除 "enc:" 前缀后 Base64 解码
            String base64Data = encryptedText.substring(4);
            byte[] combined = Base64.getDecoder().decode(base64Data);

            // 分离 IV 和密文：前 12 字节为 IV，剩余为密文（含 GCM Tag）
            byte[] iv = new byte[GCM_IV_LENGTH];
            byte[] ciphertext = new byte[combined.length - GCM_IV_LENGTH];
            System.arraycopy(combined, 0, iv, 0, iv.length);
            System.arraycopy(combined, iv.length, ciphertext, 0, ciphertext.length);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec gcmSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, getKey(), gcmSpec);

            byte[] plaintext = cipher.doFinal(ciphertext);
            return new String(plaintext, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed", e);
        }
    }
}
