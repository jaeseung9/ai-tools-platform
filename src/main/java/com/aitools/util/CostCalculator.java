package com.aitools.util;

public class CostCalculator {

    // GPT-3.5-turbo 가격 (2024년 기준)
    private static final double INPUT_PRICE_PER_1K = 0.0015;  // $0.0015 / 1K tokens
    private static final double OUTPUT_PRICE_PER_1K = 0.002;  // $0.002 / 1K tokens

    public static double calculateChatCost(int inputTokens, int outputTokens) {
        double inputCost = (inputTokens / 1000.0) * INPUT_PRICE_PER_1K;
        double outputCost = (outputTokens / 1000.0) * OUTPUT_PRICE_PER_1K;
        return inputCost + outputCost;
    }

    public static double calculateImageCost(String size) {
        // DALL-E 3 가격
        return 0.04; // $0.04 for 1024x1024
    }
}