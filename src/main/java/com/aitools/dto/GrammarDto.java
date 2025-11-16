package com.aitools.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

public class GrammarDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private String text;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String originalText;
        private String correctedText;
        private Integer errorCount;
        private List<ErrorDetail> errors;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ErrorDetail {
        private String token;          // 오류 단어
        private String suggestion;     // 교정안
        private String help;           // 도움말
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class History {
        private Long id;
        private String originalText;
        private String correctedText;
        private Integer errorCount;
        private LocalDateTime createdAt;
    }
}