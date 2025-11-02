package com.aitools.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

public class ImageDto {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private String prompt;
        private String size = "1024x1024";  // 기본값
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private String imageUrl;
        private String prompt;
        private Double estimatedCost;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class History {
        private Long id;
        private String prompt;
        private String imageUrl;
        private String imageSize;
        private Double estimatedCost;
        private LocalDateTime createdAt;
    }
}