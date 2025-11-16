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

    // 부산대학교 맞춤법 검사 API (한글 맞춤법/문법 검사기)
    private static final String NAVER_API_URL = "https://m.search.naver.com/p/csearch/ocontent/util/SpellerProxy";

    @Transactional
    public GrammarDto.Response checkGrammar(String identifier, String text) {
        User user = findUserByIdentifier(identifier);

        System.out.println("========== 맞춤법 검사 시작 ==========");
        System.out.println("원본 텍스트: " + text);

        // 부산대 맞춤법 검사 API 호출
        Map<String, Object> result = callPnuSpellChecker(text);

        System.out.println("API 응답: " + result);

        String correctedText = extractCorrectedText(result, text);
        List<GrammarDto.ErrorDetail> errors = extractErrors(result);
        int errorCount = errors.size();

        System.out.println("교정된 텍스트: " + correctedText);
        System.out.println("오류 개수: " + errorCount);
        System.out.println("========================================");

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

    private Map<String, Object> callPnuSpellChecker(String text) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36");

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("text1", text);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    NAVER_API_URL,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            String html = response.getBody();

            System.out.println("✓ 부산대 API 호출 성공");

            // HTML 파싱해서 결과 추출
            return parseHtmlResponse(html, text);

        } catch (Exception e) {
            System.err.println("✗ 부산대 API 호출 실패: " + e.getMessage());
            e.printStackTrace();
            return createEmptyResult(text);
        }
    }

    private Map<String, Object> parseHtmlResponse(String html, String originalText) {
        Map<String, Object> result = new java.util.HashMap<>();
        List<Map<String, Object>> errors = new ArrayList<>();

        try {
            // HTML에서 교정 정보 추출
            String correctedText = originalText;

            // <span class="green_text"> 태그에서 오류 부분 찾기
            if (html.contains("class=\"green_text\"") || html.contains("class='green_text'")) {
                // 오류가 있는 경우

                // 간단한 파싱: <data>태그로 교정된 텍스트 찾기
                if (html.contains("<data")) {
                    int dataStart = html.indexOf("<data");
                    while (dataStart != -1) {
                        int dataEnd = html.indexOf("</data>", dataStart);
                        if (dataEnd != -1) {
                            String dataContent = html.substring(dataStart, dataEnd + 7);

                            // 원본과 교정 추출
                            String orgStr = extractAttribute(dataContent, "org");
                            String candWord = extractAttribute(dataContent, "new");
                            String help = extractAttribute(dataContent, "help");

                            if (orgStr != null && candWord != null) {
                                Map<String, Object> error = new java.util.HashMap<>();
                                error.put("orgStr", orgStr);
                                error.put("candWord", candWord);
                                error.put("help", help != null ? help : "");
                                errors.add(error);

                                // 교정된 텍스트 생성
                                correctedText = correctedText.replace(orgStr, candWord);
                            }
                        }
                        dataStart = html.indexOf("<data", dataEnd);
                    }
                }
            }

            result.put("corrected_text", correctedText);
            result.put("error_count", errors.size());
            result.put("errors", errors);

            System.out.println("→ 파싱 결과: 오류 " + errors.size() + "개");

        } catch (Exception e) {
            System.err.println("✗ HTML 파싱 실패: " + e.getMessage());
            return createEmptyResult(originalText);
        }

        return result;
    }

    private String extractAttribute(String html, String attrName) {
        String searchStr = attrName + "=\"";
        int start = html.indexOf(searchStr);
        if (start == -1) {
            searchStr = attrName + "='";
            start = html.indexOf(searchStr);
        }
        if (start == -1) return null;

        start += searchStr.length();
        int end = html.indexOf("\"", start);
        if (end == -1) end = html.indexOf("'", start);
        if (end == -1) return null;

        return html.substring(start, end);
    }

    private Map<String, Object> createEmptyResult(String text) {
        Map<String, Object> errorResult = new java.util.HashMap<>();
        errorResult.put("corrected_text", text);
        errorResult.put("error_count", 0);
        errorResult.put("errors", new ArrayList<>());
        return errorResult;
    }

    private String extractCorrectedText(Map<String, Object> result, String originalText) {
        try {
            if (result.containsKey("corrected_text")) {
                return (String) result.get("corrected_text");
            }
        } catch (Exception e) {
            System.err.println("✗ extractCorrectedText 오류: " + e.getMessage());
        }
        return originalText;
    }

    private List<GrammarDto.ErrorDetail> extractErrors(Map<String, Object> result) {
        List<GrammarDto.ErrorDetail> errors = new ArrayList<>();

        try {
            Object errorCountObj = result.get("error_count");
            Integer errorCount = errorCountObj instanceof Integer ? (Integer) errorCountObj : 0;

            if (errorCount > 0) {
                List<Map<String, Object>> errorsList =
                        (List<Map<String, Object>>) result.get("errors");

                if (errorsList != null) {
                    for (Map<String, Object> error : errorsList) {
                        errors.add(GrammarDto.ErrorDetail.builder()
                                .token((String) error.get("orgStr"))
                                .suggestion((String) error.get("candWord"))
                                .help((String) error.get("help"))
                                .build());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("✗ extractErrors 오류: " + e.getMessage());
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