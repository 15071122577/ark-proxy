package com.ark.proxy.repository.mysql;

import com.ark.proxy.model.entity.APIKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * API Key 池数据访问层，提供 API Key 实体的 CRUD 及自定义查询操作
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
@Repository
public interface APIKeyRepository extends JpaRepository<APIKey, String> {

    /**
     * 查询指定厂商下特定状态的 API Key 列表
     *
     * @param providerId 厂商ID
     * @param status     Key 状态（如 active、disabled）
     * @return 符合条件的 API Key 列表
     */
    List<APIKey> findByProviderIdAndStatus(String providerId, String status);

    /**
     * 查询指定状态的所有 API Key 列表
     *
     * @param status Key 状态（如 active、disabled）
     * @return 符合条件的 API Key 列表
     */
    List<APIKey> findByStatus(String status);
}
