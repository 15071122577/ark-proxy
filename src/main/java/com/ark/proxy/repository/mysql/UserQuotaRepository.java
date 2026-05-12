package com.ark.proxy.repository.mysql;

import com.ark.proxy.model.entity.UserQuota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户配额数据访问层，提供用户配额实体的 CRUD 及自定义查询操作
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
@Repository
public interface UserQuotaRepository extends JpaRepository<UserQuota, String> {

    /**
     * 根据用户ID查询配额信息
     *
     * @param userId 用户ID
     * @return 匹配的用户配额实体，若不存在则返回空的 Optional
     */
    Optional<UserQuota> findByUserId(String userId);
}
