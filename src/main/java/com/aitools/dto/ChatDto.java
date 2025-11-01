package com.aitools.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

public class ChatDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private String message;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String message;
        private Integer tokenUsed;
        private Double estimatedCost;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class History {
        private Long id;
        private String userMessage;
        private String aiResponse;
        private Integer tokenUsed;
        private Double estimatedCost;
        private LocalDateTime createdAt;
    }
}