package com.aitools.controller;

import com.aitools.service.ApiUsageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final ApiUsageService apiUsageService;

    /**
     * 이번 달 통계 조회
     */
    @GetMapping("/stats")
    public ResponseEntity<ApiUsageService.MonthlyStatsDto> getMonthlyStats() {
        String identifier = getCurrentUserIdentifier();
        ApiUsageService.MonthlyStatsDto stats = apiUsageService.getMonthlyStats(identifier);
        return ResponseEntity.ok(stats);
    }

    /**
     * 최근 N개월 통계 조회
     */
    @GetMapping("/usage-chart")
    public ResponseEntity<ApiUsageService.RecentMonthsStatsDto> getUsageChart(
            @RequestParam(defaultValue = "6") int months) {
        String identifier = getCurrentUserIdentifier();
        ApiUsageService.RecentMonthsStatsDto stats = apiUsageService.getRecentMonthsStats(identifier, months);
        return ResponseEntity.ok(stats);
    }

    /**
     * 도구별 비용 비율
     */
    @GetMapping("/cost-breakdown")
    public ResponseEntity<ApiUsageService.CostBreakdownDto> getCostBreakdown() {
        String identifier = getCurrentUserIdentifier();
        ApiUsageService.CostBreakdownDto breakdown = apiUsageService.getCostBreakdown(identifier);
        return ResponseEntity.ok(breakdown);
    }

    private String getCurrentUserIdentifier() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof String) {
            return (String) principal;
        }

        if (principal instanceof OAuth2User) {
            OAuth2User oAuth2User = (OAuth2User) principal;

            if (oAuth2User.getAttributes().containsKey("response")) {
                Map<String, Object> response = (Map<String, Object>) oAuth2User.getAttribute("response");
                return "naver_" + response.get("id");
            } else if (oAuth2User.getAttributes().containsKey("id")) {
                return "kakao_" + oAuth2User.getAttribute("id");
            }
        }

        throw new RuntimeException("인증 정보를 찾을 수 없습니다.");
    }
}