package com.fitness.aiservice.service.impl;

import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.model.RecommendationContent;
import com.fitness.aiservice.repository.RecommendationRepository;
import com.fitness.aiservice.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {

    @Autowired
    private final RecommendationRepository recommendationRepository;

    @Autowired
    private final ChatClient.Builder chatClientBuilder;

    @Override
    public List<Recommendation> getRecommendationsForUser(String userId) {
        return recommendationRepository.findByUserId(userId);
    }

    @Override
    public Recommendation getRecommendationsForActivity(String activityId) {
        return recommendationRepository.findByActivityId(activityId).orElse(null);
    }

    @Override
    public Recommendation generateRecommendation(Activity activity) {
        log.info("Generating recommendation for activity: {}", activity.getId());
        ChatClient chatClient = chatClientBuilder.build();

        String activityDetails = """
                Activity ID: %s
                User ID: %s
                Activity Type: %s
                Duration in minutes: %s
                Calories burned: %s
                Start time: %s
                Additional metrics: %s
                """.formatted(
                activity.getId(),
                activity.getUserId(),
                activity.getType(),
                activity.getDuration(),
                activity.getCaloriesBurned(),
                activity.getStartTime(),
                activity.getAdditionalMetrics()
        );

        RecommendationContent content = chatClient.prompt()
                .system("""
                        You are a fitness recommendation assistant.

                        Analyze the supplied workout activity.
                        Return:
                        1. A concise analysis of the workout.
                        2. A list of practical improvements.
                        3. A list of actionable suggestions.
                        4. A list of safety precautions before, during or after workout

                        Base your response only on the supplied activity.
                        Do not invent missing measurements or diagnose illnesses.
                        Do not give response based on the time the activity is created at.
                        Keep all recommendations clear and practical.
                        """)
                .user("""
                        Generate a recommendation for this activity:

                        %s
                        """.formatted(activityDetails))
                .call()
                .entity(RecommendationContent.class);

        if (content == null || content.recommendation() == null) {
            throw new IllegalStateException(
                    "AI did not return a valid recommendation"
            );
        }

        Recommendation recommendation = Recommendation.builder()
                .activityId(activity.getId())
                .userId(activity.getUserId())
                .activityType(String.valueOf(activity.getType()))
                .recommendation(content.recommendation())
                .improvements(content.improvements() == null
                        ? List.of() : content.improvements())
                .suggestions(content.suggestions() == null
                        ? List.of() : content.suggestions())
                .safety(content.safety()==null
                        ? List.of() : content.safety())
                .createdAt(LocalDateTime.now())
                .build();

        Recommendation saved =
                recommendationRepository.save(recommendation);

        log.info("Recommendation saved for activity: {}", activity.getId());

        return saved;
    }
}
