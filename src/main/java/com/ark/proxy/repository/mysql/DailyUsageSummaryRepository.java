package com.ark.proxy.repository.mysql;

import com.ark.proxy.model.entity.DailyUsageSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 日用量汇总数据访问层，提供日用量汇总实体的 CRUD 及自定义查询操作
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
@Repository
public interface DailyUsageSummaryRepository extends JpaRepository<DailyUsageSummary, String> {

    /**
     * 根据日期和用户ID查询日用量汇总
     *
     * @param date   汇总日期
     * @param userId 用户ID
     * @return 匹配的日用量汇总实体，若不存在则返回空的 Optional
     */
    Optional<DailyUsageSummary> findByDateAndUserId(LocalDate date, String userId);

    /**
     * 查询指定用户在日期范围内的日用量汇总列表
     *
     * @param userId 用户ID
     * @param start  起始日期
     * @param end    结束日期
     * @return 符合条件的日用量汇总列表
     */
    List<DailyUsageSummary> findByUserIdAndDateBetween(String userId, LocalDate start, LocalDate end);

    /**
     * 查询指定团队在日期范围内的日用量汇总列表
     *
     * @param teamId 团队ID
     * @param start  起始日期
     * @param end    结束日期
     * @return 符合条件的日用量汇总列表
     */
    List<DailyUsageSummary> findByTeamIdAndDateBetween(String teamId, LocalDate start, LocalDate end);

    /**
     * 汇总指定用户在日期范围内的总请求数、总 Token 数和总费用
     *
     * @param userId 用户ID
     * @param start  起始日期
     * @param end    结束日期
     * @return 包含三个元素的数组：[总请求数, 总Token数, 总费用]，若无数据则各元素为 null
     */
    @Query("SELECT SUM(d.totalRequests), SUM(d.totalTokens), SUM(d.totalCost) FROM DailyUsageSummary d WHERE d.userId = :userId AND d.date BETWEEN :start AND :end")
    Object[] sumUsageByUserIdAndDateBetween(
            @Param("userId") String userId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);
}
