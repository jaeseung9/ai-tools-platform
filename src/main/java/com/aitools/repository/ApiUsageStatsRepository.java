package com.aitools.repository;

import com.aitools.entity.ApiUsageStats;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApiUsageStatsRepository extends JpaRepository<ApiUsageStats, Long> {

    /**
     * 특정 사용자의 특정 월, 특정 도구의 통계 조회
     */
    Optional<ApiUsageStats> findByUserIdAndToolTypeAndYearMonth(
            Long userId, 
            String toolType, 
            String yearMonth
    );

    /**
     * 특정 사용자의 특정 월 전체 통계 조회
     */
    List<ApiUsageStats> findByUserIdAndYearMonth(Long userId, String yearMonth);

    /**
     * 특정 사용자의 최근 N개월 통계 조회
     */
    @Query("SELECT s FROM ApiUsageStats s " +
           "WHERE s.user.id = :userId " +
           "AND s.yearMonth >= :startMonth " +
           "ORDER BY s.yearMonth DESC, s.toolType")
    List<ApiUsageStats> findRecentMonthsStats(
            @Param("userId") Long userId,
            @Param("startMonth") String startMonth
    );

    /**
     * 특정 사용자의 이번 달 총 비용 계산
     */
    @Query("SELECT COALESCE(SUM(s.totalCost), 0.0) FROM ApiUsageStats s " +
           "WHERE s.user.id = :userId " +
           "AND s.yearMonth = :yearMonth")
    Double calculateMonthlyTotalCost(
            @Param("userId") Long userId,
            @Param("yearMonth") String yearMonth
    );

    /**
     * 특정 사용자의 이번 달 총 사용 횟수
     */
    @Query("SELECT COALESCE(SUM(s.usageCount), 0) FROM ApiUsageStats s " +
           "WHERE s.user.id = :userId " +
           "AND s.yearMonth = :yearMonth")
    Integer calculateMonthlyTotalUsage(
            @Param("userId") Long userId,
            @Param("yearMonth") String yearMonth
    );
}
