package com.ark.proxy.repository.mysql;

import com.ark.proxy.model.entity.UsageLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用量日志数据访问层，提供用量日志实体的 CRUD 及自定义查询操作
 *
 * @author WangMiao
 * @date 2026-05-11
 * @version 1.0
 */
@Repository
public interface UsageLogRepository extends JpaRepository<UsageLog, String> {

    /**
     * 查询指定用户在时间范围内的用量日志
     *
     * @param userId 用户ID
     * @param start  起始时间
     * @param end    结束时间
     * @return 符合条件的用量日志列表
     */
    List<UsageLog> findByUserIdAndTimestampBetween(String userId, LocalDateTime start, LocalDateTime end);

    /**
     * 查询指定团队在时间范围内的用量日志
     *
     * @param teamId 团队ID
     * @param start  起始时间
     * @param end    结束时间
     * @return 符合条件的用量日志列表
     */
    List<UsageLog> findByTeamIdAndTimestampBetween(String teamId, LocalDateTime start, LocalDateTime end);

    /**
     * 查询指定用户、指定厂商在时间范围内的用量日志
     *
     * @param userId   用户ID
     * @param provider 厂商标识
     * @param start    起始时间
     * @param end      结束时间
     * @return 符合条件的用量日志列表
     */
    @Query("SELECT ul FROM UsageLog ul WHERE ul.userId = :userId AND ul.provider = :provider AND ul.timestamp BETWEEN :start AND :end")
    List<UsageLog> findByUserIdAndProviderAndTimestampBetween(
            @Param("userId") String userId,
            @Param("provider") String provider,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end);

    /**
     * 统计指定用户在指定时间之后的请求总数
     *
     * @param userId 用户ID
     * @param since  起始时间
     * @return 请求总数
     */
    @Query("SELECT COUNT(ul) FROM UsageLog ul WHERE ul.userId = :userId AND ul.timestamp >= :since")
    long countByUserIdAndTimestampAfter(@Param("userId") String userId, @Param("since") LocalDateTime since);
}
