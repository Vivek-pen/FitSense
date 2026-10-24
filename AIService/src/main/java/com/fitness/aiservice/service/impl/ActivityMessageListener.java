package com.fitness.aiservice.service.impl;

import com.fitness.aiservice.model.Activity;
import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@RequiredArgsConstructor
@Slf4j
public class ActivityMessageListener {

    private final RecommendationService recommendationService;

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void processActivity(Activity activity){

        log.info("Received activity for processing: {}",activity.getId());

        Recommendation recommendation = recommendationService.generateRecommendation(activity);

        log.info("Recommendation Generated and saved: {}",recommendation.getId());
    }
}
