package com.aitools.service;

import com.aitools.dto.GrammarDto;
import com.aitools.entity.GrammarHistory;
import com.aitools.entity.User;
import com.aitools.repository.GrammarHistoryRepository;
import com.aitools.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GrammarService {

    private final GrammarHistoryRepository grammarHistoryRepository;
    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    // 네이버 맞춤법 검사 API
    private static final String NAVER_API_URL = "https://m.search.naver.com/p/csearch/ocontent/spellchecker.nhn";

    @Transactional
    public GrammarDto.Response checkGrammar(String identifier, String text) {
        User user = findUserByIdentifier(identifier);

        // 네이버 맞춤법 검사 API 호출
        Map<String, Object> result = callNaverSpellChecker(text);

        String correctedText = extractCorrectedText(result);
        List<GrammarDto.ErrorDetail> errors = extractErrors(result);
        int errorCount = errors.size();

        // DB 저장
        GrammarHistory history = GrammarHistory.builder()
                .user(user)
                .originalText(text)
                .correctedText(correctedText)
                .errorCount(errorCount)
                .build();

        grammarHistoryRepository.save(history);

        return GrammarDto.Response.builder()
                .originalText(text)
                .correctedText(correctedText)
                .errorCount(errorCount)
                .errors(errors)
                .build();
    }

    public List<GrammarDto.History> getHistory(String identifier) {
        User user = findUserByIdentifier(identifier);

        return grammarHistoryRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream()
                .map(h -> GrammarDto.History.builder()
                        .id(h.getId())
                        .originalText(h.getOriginalText())
                        .correctedText(h.getCorrectedText())
                        .errorCount(h.getErrorCount())
                        .createdAt(h.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteHistory(String identifier, Long historyId) {
        User user = findUserByIdentifier(identifier);
        grammarHistoryRepository.deleteByIdAndUserId(historyId, user.getId());
    }

    private Map<String, Object> callNaverSpellChecker(String text) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("_callback", "");
            params.add("q", text);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    NAVER_API_URL,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            return response.getBody();
        } catch (Exception e) {
            // 에러 발생 시 원본 텍스트 그대로 반환
            Map<String, Object> errorResult = new java.util.HashMap<>();
            errorResult.put("message", Map.of("result", Map.of("notag_html", text)));
            errorResult.put("errata_count", 0);
            return errorResult;
        }
    }

    private String extractCorrectedText(Map<String, Object> result) {
        try {
            Map<String, Object> message = (Map<String, Object>) result.get("message");
            Map<String, Object> resultMap = (Map<String, Object>) message.get("result");
            return (String) resultMap.get("notag_html");
        } catch (Exception e) {
            return "";
        }
    }

    private List<GrammarDto.ErrorDetail> extractErrors(Map<String, Object> result) {
        List<GrammarDto.ErrorDetail> errors = new ArrayList<>();

        try {
            Integer errataCount = (Integer) result.get("errata_count");

            if (errataCount != null && errataCount > 0) {
                List<Map<String, Object>> errataList =
                        (List<Map<String, Object>>) result.get("errata");

                if (errataList != null) {
                    for (Map<String, Object> errata : errataList) {
                        errors.add(GrammarDto.ErrorDetail.builder()
                                .token((String) errata.get("orgStr"))
                                .suggestion((String) errata.get("candWord"))
                                .help((String) errata.get("help"))
                                .build());
                    }
                }
            }
        } catch (Exception e) {
            // 에러 파싱 실패 시 빈 리스트 반환
        }

        return errors;
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