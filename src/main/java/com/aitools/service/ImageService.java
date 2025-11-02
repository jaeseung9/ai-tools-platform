package com.aitools.service;

import com.aitools.dto.ImageDto;
import com.aitools.entity.ImageHistory;
import com.aitools.entity.User;
import com.aitools.exception.ApiException;
import com.aitools.repository.ImageHistoryRepository;
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
public class ImageService {

    private final ImageHistoryRepository imageHistoryRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate;

    @Value("${stability.api.key}")
    private String apiKey;

    @Value("${stability.api.url}")
    private String apiUrl;

    @Transactional
    public ImageDto.Response generateImage(String identifier, String prompt, String size) {
        User user = findUserByIdentifier(identifier);

        // DALL-E 3 API 호출
        String imageUrl = callDallE3(prompt, size);
        double cost = CostCalculator.calculateImageCost(size);

        // DB 저장
        ImageHistory history = ImageHistory.builder()
                .user(user)
                .prompt(prompt)
                .imageUrl(imageUrl)
                .imageSize(size)
                .estimatedCost(cost)
                .build();

        imageHistoryRepository.save(history);

        return ImageDto.Response.builder()
                .imageUrl(imageUrl)
                .prompt(prompt)
                .estimatedCost(cost)
                .build();
    }

    public List<ImageDto.History> getHistory(String identifier) {
        User user = findUserByIdentifier(identifier);

        return imageHistoryRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(h -> ImageDto.History.builder()
                        .id(h.getId())
                        .prompt(h.getPrompt())
                        .imageUrl(h.getImageUrl())
                        .imageSize(h.getImageSize())
                        .estimatedCost(h.getEstimatedCost())
                        .createdAt(h.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteHistory(String identifier, Long historyId) {
        User user = findUserByIdentifier(identifier);
        imageHistoryRepository.deleteByIdAndUserId(historyId, user.getId());
    }

    private String callDallE3(String prompt, String size) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            headers.set("Accept", "application/json");

            // Stability AI는 width x height를 별도로 받음
            String[] dimensions = size.split("x");
            int width = Integer.parseInt(dimensions[0]);
            int height = Integer.parseInt(dimensions[1]);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("text_prompts", List.of(Map.of("text", prompt)));
            requestBody.put("cfg_scale", 7);
            requestBody.put("height", height);
            requestBody.put("width", width);
            requestBody.put("samples", 1);
            requestBody.put("steps", 30);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            ResponseEntity<Map> response = restTemplate.exchange(apiUrl, HttpMethod.POST, entity, Map.class);

            // Base64 이미지 데이터 추출
            List<Map<String, Object>> artifacts = (List<Map<String, Object>>) response.getBody().get("artifacts");
            String base64Image = (String) artifacts.get(0).get("base64");

            // Base64를 Data URL로 변환 (프론트엔드에서 바로 표시 가능)
            return "data:image/png;base64," + base64Image;

        } catch (Exception e) {
            throw new ApiException("Stability AI API 호출 실패: " + e.getMessage(), e);
        }
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
}