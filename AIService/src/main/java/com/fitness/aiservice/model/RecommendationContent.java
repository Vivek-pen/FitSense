package com.fitness.aiservice.model;

import java.util.List;

public record RecommendationContent(
        String recommendation,
        List<String> improvements,
        List<String> suggestions,
        List<String> safety
) {
}