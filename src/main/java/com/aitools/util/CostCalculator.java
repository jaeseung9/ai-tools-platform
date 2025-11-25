package com.aitools.util;

public class CostCalculator {


    private static final double INPUT_PRICE_PER_1K = 0.0015;
    private static final double OUTPUT_PRICE_PER_1K = 0.002;

    public static double calculateChatCost(int inputTokens, int outputTokens) {
        double inputCost = (inputTokens / 1000.0) * INPUT_PRICE_PER_1K;
        double outputCost = (outputTokens / 1000.0) * OUTPUT_PRICE_PER_1K;
        return inputCost + outputCost;
    }

    public static double calculateImageCost(String size) {

        return 0.04;
    }
}