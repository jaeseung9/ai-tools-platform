package com.aitools.controller;

import com.aitools.dto.ChatDto;
import com.aitools.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/message")
    public ResponseEntity<ChatDto.Response> sendMessage(
            @RequestBody ChatDto.Request request) {

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

    private String getCurrentUserIdentifier() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        // 일반 로그인 (이메일)
        if (principal instanceof String) {
            return (String) principal;  // 이메일 반환
        }

        // 소셜 로그인 (OAuth2User)
        if (principal instanceof OAuth2User) {
            OAuth2User oAuth2User = (OAuth2User) principal;

            // 네이버
            if (oAuth2User.getAttributes().containsKey("response")) {
                Map<String, Object> response = (Map<String, Object>) oAuth2User.getAttribute("response");
                return "naver_" + response.get("id");
            }
            // 카카오
            else if (oAuth2User.getAttributes().containsKey("id")) {
                return "kakao_" + oAuth2User.getAttribute("id");
            }
        }

        throw new RuntimeException("인증 정보를 찾을 수 없습니다.");
    }
}