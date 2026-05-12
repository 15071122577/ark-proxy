package com.ark.proxy.repository.mysql;

import com.ark.proxy.model.entity.AlertRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 预警规则数据访问层，提供预警规则实体的 CRUD 及自定义查询操作
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
@Repository
public interface AlertRuleRepository extends JpaRepository<AlertRule, String> {

    /**
     * 查询所有已启用的预警规则
     *
     * @return 已启用的预警规则列表
     */
    List<AlertRule> findByEnabledTrue();

    /**
     * 查询指定目标类型和目标ID的预警规则
     *
     * @param targetType 目标类型（如 user、team）
     * @param targetId   目标ID
     * @return 符合条件的预警规则列表
     */
    List<AlertRule> findByTargetTypeAndTargetId(String targetType, String targetId);

    /**
     * 查询指定目标类型且未绑定特定目标（全局规则）的预警规则
     *
     * @param targetType 目标类型（如 user、team）
     * @return 该类型下未指定 targetId 的全局预警规则列表
     */
    List<AlertRule> findByTargetTypeAndTargetIdIsNull(String targetType);
}
