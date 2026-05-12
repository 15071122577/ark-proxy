package com.ark.proxy.repository.mysql;

import com.ark.proxy.model.entity.ModelMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 模型映射数据访问层，提供模型映射实体的 CRUD 及自定义查询操作
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
@Repository
public interface ModelMappingRepository extends JpaRepository<ModelMapping, String> {

    /**
     * 查询指定厂商下所有已启用的模型映射
     *
     * @param providerId 厂商ID
     * @return 该厂商下已启用的模型映射列表
     */
    List<ModelMapping> findByProviderIdAndEnabledTrue(String providerId);

    /**
     * 根据厂商ID和标准模型名称查询唯一的模型映射
     *
     * @param providerId         厂商ID
     * @param standardModelName  标准模型名称
     * @return 匹配的模型映射实体，若不存在则返回空的 Optional
     */
    Optional<ModelMapping> findByProviderIdAndStandardModelName(String providerId, String standardModelName);

    /**
     * 查询所有已启用的模型映射
     *
     * @return 所有已启用的模型映射列表
     */
    List<ModelMapping> findByEnabledTrue();
}
