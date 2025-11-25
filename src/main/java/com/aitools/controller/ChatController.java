package com.aitools.controller;

import com.aitools.dto.ChatDto;
import com.aitools.filter.RateLimitFilter;  // 추가!
import com.aitools.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;  // 추가!
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final RateLimitFilter rateLimitFilter;

    @PostMapping("/message")
    public ResponseEntity<ChatDto.Response> sendMessage(@RequestBody ChatDto.Request request) {
        String identifier = getCurrentUserIdentifier();
        ChatDto.Response response = chatService.sendMessage(identifier, request.getMessage());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<ChatDto.History>> getHistory() {
        String identifier = getCurrentUserIdentifier();
        List<ChatDto.History> history = chatService.getHistory(identifier);
        return ResponseEntity.ok(history);
    }

    @DeleteMapping("/history/{id}")
    public ResponseEntity<Void> deleteHistory(@PathVariable Long id) {
        String identifier = getCurrentUserIdentifier();
        chatService.deleteHistory(identifier, id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/remaining-tokens")
    public ResponseEntity<Map<String, Object>> getRemainingTokens() {
        String identifier = getCurrentUserIdentifier();
        int remaining = rateLimitFilter.getRemainingTokens(identifier);
        int dailyLimit = rateLimitFilter.getDailyLimit();
        int used = rateLimitFilter.getUsedTokens(identifier);

        Map<String, Object> result = new HashMap<>();
        result.put("remainingTokens", remaining);
        result.put("dailyLimit", dailyLimit);
        result.put("usedTokens", used);

        return ResponseEntity.ok(result);
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