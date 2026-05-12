package com.ark.proxy.repository.mysql;

import com.ark.proxy.model.entity.AlertHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预警历史数据访问层，提供预警历史实体的 CRUD 及自定义查询操作
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
@Repository
public interface AlertHistoryRepository extends JpaRepository<AlertHistory, String> {

    /**
     * 查询指定用户的预警历史，按创建时间倒序排列
     *
     * @param userId 用户ID
     * @return 该用户的预警历史列表，最新的排在前面
     */
    List<AlertHistory> findByUserIdOrderByCreatedAtDesc(String userId);

    /**
     * 根据预警状态查询预警历史列表
     *
     * @param status 预警状态（如 active、resolved）
     * @return 符合条件的预警历史列表
     */
    List<AlertHistory> findByStatus(String status);

    /**
     * 查询指定时间范围内的预警历史
     *
     * @param start 起始时间
     * @param end   结束时间
     * @return 符合条件的预警历史列表
     */
    List<AlertHistory> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
