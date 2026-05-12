package com.ark.proxy.repository.mysql;

import com.ark.proxy.model.entity.ProviderConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 厂商配置数据访问层，提供厂商配置实体的 CRUD 及自定义查询操作
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
@Repository
public interface ProviderConfigRepository extends JpaRepository<ProviderConfig, String> {

    /**
     * 查询所有已启用的厂商配置，按优先级升序排列
     *
     * @return 按优先级排序的已启用厂商配置列表
     */
    List<ProviderConfig> findByEnabledTrueOrderByPriorityAsc();

    /**
     * 根据协议类型查询厂商配置列表
     *
     * @param protocolType 协议类型（如 anthropic、openai）
     * @return 符合条件的厂商配置列表
     */
    List<ProviderConfig> findByProtocolType(String protocolType);
}
