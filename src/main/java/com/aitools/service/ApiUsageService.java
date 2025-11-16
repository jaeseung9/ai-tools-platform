// src/main/java/com/aitools/service/ApiUsageService.java
package com.aitools.service;

import com.aitools.entity.ApiUsageStats;
import com.aitools.entity.User;
import com.aitools.repository.ApiUsageStatsRepository;
import com.aitools.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApiUsageService {

    private final ApiUsageStatsRepository apiUsageStatsRepository;
    private final UserRepository userRepository;

    /**
     * API 사용량 기록 (Chat, Image, Grammar 호출 후 실행)
     */
    @Transactional
    public void recordUsage(String identifier, String toolType, double cost) {
        User user = findUserByIdentifier(identifier);
        String yearMonth = getCurrentYearMonth();

        // 기존 통계 조회 또는 새로 생성
        Optional<ApiUsageStats> existingStats = apiUsageStatsRepository
                .findByUserIdAndToolTypeAndYearMonth(user.getId(), toolType, yearMonth);

        if (existingStats.isPresent()) {
            // 기존 데이터 업데이트
            ApiUsageStats stats = existingStats.get();
            stats.setUsageCount(stats.getUsageCount() + 1);
            stats.setTotalCost(stats.getTotalCost() + cost);
            apiUsageStatsRepository.save(stats);
        } else {
            // 새 데이터 생성
            ApiUsageStats newStats = ApiUsageStats.builder()
                    .user(user)
                    .toolType(toolType)
                    .usageCount(1)
                    .totalCost(cost)
                    .yearMonth(yearMonth)
                    .build();
            apiUsageStatsRepository.save(newStats);
        }
    }

    /**
     * 이번 달 전체 통계 조회
     */
    public MonthlyStatsDto getMonthlyStats(String identifier) {
        User user = findUserByIdentifier(identifier);
        String yearMonth = getCurrentYearMonth();

        Integer chatCount = getUsageCount(user.getId(), "CHAT", yearMonth);
        Integer imageCount = getUsageCount(user.getId(), "IMAGE", yearMonth);
        Integer grammarCount = getUsageCount(user.getId(), "GRAMMAR", yearMonth);

        Double chatCost = getTotalCost(user.getId(), "CHAT", yearMonth);
        Double imageCost = getTotalCost(user.getId(), "IMAGE", yearMonth);
        Double grammarCost = getTotalCost(user.getId(), "GRAMMAR", yearMonth);

        Double totalCost = chatCost + imageCost + grammarCost;
        Integer totalUsage = chatCount + imageCount + grammarCount;

        return MonthlyStatsDto.builder()
                .chatCount(chatCount)
                .imageCount(imageCount)
                .grammarCount(grammarCount)
                .chatCost(chatCost)
                .imageCost(imageCost)
                .grammarCost(grammarCost)
                .totalCost(totalCost)
                .totalUsage(totalUsage)
                .yearMonth(yearMonth)
                .build();
    }

    /**
     * 최근 N개월 통계 조회
     */
    public RecentMonthsStatsDto getRecentMonthsStats(String identifier, int months) {
        User user = findUserByIdentifier(identifier);
        String startMonth = getStartYearMonth(months);

        var stats = apiUsageStatsRepository.findRecentMonthsStats(user.getId(), startMonth);

        return RecentMonthsStatsDto.builder()
                .stats(stats)
                .build();
    }

    /**
     * 도구별 비용 비율 계산
     */
    public CostBreakdownDto getCostBreakdown(String identifier) {
        User user = findUserByIdentifier(identifier);
        String yearMonth = getCurrentYearMonth();

        Double chatCost = getTotalCost(user.getId(), "CHAT", yearMonth);
        Double imageCost = getTotalCost(user.getId(), "IMAGE", yearMonth);
        Double grammarCost = getTotalCost(user.getId(), "GRAMMAR", yearMonth);

        Double totalCost = chatCost + imageCost + grammarCost;

        double chatPercentage = totalCost > 0 ? (chatCost / totalCost) * 100 : 0;
        double imagePercentage = totalCost > 0 ? (imageCost / totalCost) * 100 : 0;
        double grammarPercentage = totalCost > 0 ? (grammarCost / totalCost) * 100 : 0;

        return CostBreakdownDto.builder()
                .chatCost(chatCost)
                .imageCost(imageCost)
                .grammarCost(grammarCost)
                .chatPercentage(chatPercentage)
                .imagePercentage(imagePercentage)
                .grammarPercentage(grammarPercentage)
                .totalCost(totalCost)
                .build();
    }

    // ========== Private Methods ==========

    private User findUserByIdentifier(String identifier) {
        if (identifier.contains("@")) {
            return userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } else {
            return userRepository.findByOauthId(identifier)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
    }

    private String getCurrentYearMonth() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }

    private String getStartYearMonth(int months) {
        return LocalDate.now().minusMonths(months - 1)
                .format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }

    private Integer getUsageCount(Long userId, String toolType, String yearMonth) {
        return apiUsageStatsRepository
                .findByUserIdAndToolTypeAndYearMonth(userId, toolType, yearMonth)
                .map(ApiUsageStats::getUsageCount)
                .orElse(0);
    }

    private Double getTotalCost(Long userId, String toolType, String yearMonth) {
        return apiUsageStatsRepository
                .findByUserIdAndToolTypeAndYearMonth(userId, toolType, yearMonth)
                .map(ApiUsageStats::getTotalCost)
                .orElse(0.0);
    }

    // ========== DTO Classes ==========

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    @lombok.Builder
    public static class MonthlyStatsDto {
        private Integer chatCount;
        private Integer imageCount;
        private Integer grammarCount;
        private Double chatCost;
        private Double imageCost;
        private Double grammarCost;
        private Double totalCost;
        private Integer totalUsage;
        private String yearMonth;
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    @lombok.Builder
    public static class RecentMonthsStatsDto {
        private java.util.List<ApiUsageStats> stats;
    }

    @lombok.Data
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    @lombok.Builder
    public static class CostBreakdownDto {
        private Double chatCost;
        private Double imageCost;
        private Double grammarCost;
        private Double chatPercentage;
        private Double imagePercentage;
        private Double grammarPercentage;
        private Double totalCost;
    }
}