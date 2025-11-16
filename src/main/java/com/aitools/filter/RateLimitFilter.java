package com.aitools.filter;

import com.aitools.exception.RateLimitException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    // 하루 토큰 제한 (테스트용: 100, 운영: 10000)
    private static final int DAILY_TOKEN_LIMIT = 100;

    private final Map<String, Integer> dailyTokenUsage = new ConcurrentHashMap<>();
    private LocalDate lastResetDate = LocalDate.now();

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        if (requestURI.startsWith("/api/chat/message")) {
            String userIdentifier = getCurrentUserIdentifier();

            if (userIdentifier != null) {
                resetIfNewDay();

                String key = userIdentifier + "_" + LocalDate.now();
                int usedTokens = dailyTokenUsage.getOrDefault(key, 0);

                if (usedTokens >= DAILY_TOKEN_LIMIT) {
                    throw new RateLimitException(
                            String.format("일일 토큰 제한을 초과했습니다. (제한: %d, 사용: %d)",
                                    DAILY_TOKEN_LIMIT, usedTokens)
                    );
                }
            }
        }

        filterChain.doFilter(request, response);
    }

    public synchronized void addTokenUsage(String userIdentifier, int tokens) {
        String key = userIdentifier + "_" + LocalDate.now();
        dailyTokenUsage.merge(key, tokens, Integer::sum);
    }

    public int getRemainingTokens(String userIdentifier) {
        String key = userIdentifier + "_" + LocalDate.now();
        int usedTokens = dailyTokenUsage.getOrDefault(key, 0);
        int remaining = DAILY_TOKEN_LIMIT - usedTokens;

        // 음수면 0으로 반환 (수정!)
        return Math.max(remaining, 0);
    }

    public int getDailyLimit() {
        return DAILY_TOKEN_LIMIT;
    }

    public int getUsedTokens(String userIdentifier) {
        String key = userIdentifier + "_" + LocalDate.now();
        int used = dailyTokenUsage.getOrDefault(key, 0);

        // 사용량이 제한을 초과하면 제한값으로 표시 (추가!)
        return Math.min(used, DAILY_TOKEN_LIMIT);
    }

    private synchronized void resetIfNewDay() {
        LocalDate today = LocalDate.now();
        if (!today.equals(lastResetDate)) {
            dailyTokenUsage.clear();
            lastResetDate = today;
            System.out.println("✓ Rate Limit 리셋 완료: " + today);
        }
    }

    private String getCurrentUserIdentifier() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();

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

        return null;
    }
}