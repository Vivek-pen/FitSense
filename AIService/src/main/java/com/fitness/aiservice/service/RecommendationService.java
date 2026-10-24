package com.fitness.aiservice.service;

import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;

import java.util.List;

public interface RecommendationService {


    List<Recommendation> getRecommendationsForUser(String userId);


    Recommendation getRecommendationsForActivity(String activityId);

    Recommendation generateRecommendation(Activity activity);
}
