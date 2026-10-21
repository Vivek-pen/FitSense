package com.fitness.activityservice.service;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.model.Activity;
import com.fitness.activityservice.repository.ActivityRepository;

import java.util.List;

public interface ActivityService {
    public ActivityResponse trackActivity(ActivityRequest request);

    List<ActivityResponse> getUserActivities(String userId);

    ActivityResponse getActivity(String activityId);
}
