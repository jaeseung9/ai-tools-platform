package com.aitools.service;

import com.aitools.dto.ChatDto;
import com.aitools.entity.ChatHistory;
import com.aitools.entity.User;
import com.aitools.filter.RateLimitFilter;  // 추가!
import com.aitools.repository.ChatHistoryRepository;
import com.aitools.repository.UserRepository;
import com.aitools.util.CostCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatHistoryRepository chatHistoryRepository;
    private final UserRepository userRepository;
    private final RateLimitFilter rateLimitFilter;  // 추가!
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    @Transactional
    public ChatDto.Response sendMessage(String identifier, String message) {
        User user = findUserByIdentifier(identifier);

        // Gemini API 호출
        Map<String, Object> response = callGemini(message);

        String aiMessage = extractMessage(response);
        int totalTokens = extractTokens(response);
        double cost = CostCalculator.calculateChatCost(totalTokens / 2, totalTokens / 2);

        // 토큰 사용량 기록 (추가!)
        rateLimitFilter.addTokenUsage(identifier, totalTokens);

        // DB 저장
        ChatHistory history = ChatHistory.builder()
                .user(user)
                .userMessage(message)
                .aiResponse(aiMessage)
                .tokenUsed(totalTokens)
                .estimatedCost(cost)
                .build();

        chatHistoryRepository.save(history);

        return ChatDto.Response.builder()
                .message(aiMessage)
                .tokenUsed(totalTokens)
                .estimatedCost(cost)
                .build();
    }

    // 나머지 메서드는 그대로...

    public List<ChatDto.History> getHistory(String identifier) {
        User user = findUserByIdentifier(identifier);

        return chatHistoryRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(h -> ChatDto.History.builder()
                        .id(h.getId())
                        .userMessage(h.getUserMessage())
                        .aiResponse(h.getAiResponse())
                        .tokenUsed(h.getTokenUsed())
                        .estimatedCost(h.getEstimatedCost())
                        .createdAt(h.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteHistory(String identifier, Long historyId) {
        User user = findUserByIdentifier(identifier);
        chatHistoryRepository.deleteByIdAndUserId(historyId, user.getId());
    }

    private User findUserByIdentifier(String identifier) {
        if (identifier.contains("@")) {
            return userRepository.findByEmail(identifier)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        } else {
            return userRepository.findByOauthId(identifier)
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }
    }

    private Map<String, Object> callGemini(String message) {
        String url = apiUrl + "?key=" + apiKey;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> requestBody = new HashMap<>();

        Map<String, Object> part = new HashMap<>();
        part.put("text", message);

        Map<String, Object> content = new HashMap<>();
        content.put("parts", List.of(part));

        requestBody.put("contents", List.of(content));

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        return response.getBody();
    }

    private String extractMessage(Map<String, Object> response) {
        try {
            List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.get("candidates");
            Map<String, Object> content = (Map<String, Object>) candidates.get(0).get("content");
            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
            return (String) parts.get(0).get("text");
        } catch (Exception e) {
            return "응답 파싱 오류: " + e.getMessage();
        }
    }

    private int extractTokens(Map<String, Object> response) {
        try {
            Map<String, Object> usageMetadata = (Map<String, Object>) response.get("usageMetadata");
            return (int) usageMetadata.get("totalTokenCount");
        } catch (Exception e) {
            return 100;
        }
    }
}