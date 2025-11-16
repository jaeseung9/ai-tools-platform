package com.aitools.controller;

import com.aitools.dto.GrammarDto;
import com.aitools.service.GrammarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/grammar")
@RequiredArgsConstructor
public class GrammarController {

    private final GrammarService grammarService;

    @PostMapping("/check")
    public ResponseEntity<GrammarDto.Response> checkGrammar(@RequestBody GrammarDto.Request request) {
        String identifier = getCurrentUserIdentifier();
        GrammarDto.Response response = grammarService.checkGrammar(identifier, request.getText());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/history")
    public ResponseEntity<List<GrammarDto.History>> getHistory() {
        String identifier = getCurrentUserIdentifier();
        List<GrammarDto.History> history = grammarService.getHistory(identifier);
        return ResponseEntity.ok(history);
    }

    @DeleteMapping("/history/{id}")
    public ResponseEntity<Void> deleteHistory(@PathVariable Long id) {
        String identifier = getCurrentUserIdentifier();
        grammarService.deleteHistory(identifier, id);
        return ResponseEntity.ok().build();
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