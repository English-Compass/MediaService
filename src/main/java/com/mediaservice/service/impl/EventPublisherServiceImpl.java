package com.mediaservice.service.impl;

import com.mediaservice.event.RecommendationCreatedEvent;
import com.mediaservice.service.EventPublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * 이벤트 발행 서비스 구현체
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EventPublisherServiceImpl implements EventPublisherService {

    private final KafkaTemplate<String, RecommendationCreatedEvent> kafkaTemplate;

    @Value("${kafka.topics.recommendation-created:recommendation-created}")
    private String recommendationCreatedTopic;

    @Override
    public void publishRecommendationCreatedEvent(String userId, String recommendationId, 
                                                  String recommendationType, Integer mediaCount, 
                                                  List<String> genres, String sessionId) {
        try {
            // 이벤트 생성
            RecommendationCreatedEvent event = RecommendationCreatedEvent.create(
                    userId, recommendationId, recommendationType, mediaCount, genres, sessionId);

            // Kafka로 이벤트 발행
            CompletableFuture<SendResult<String, RecommendationCreatedEvent>> future = 
                    kafkaTemplate.send(recommendationCreatedTopic, userId, event);

            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("✅ 추천 생성 이벤트 발행 성공 - UserId: {}, RecommendationId: {}, Topic: {}", 
                            userId, recommendationId, recommendationCreatedTopic);
                } else {
                    log.error("❌ 추천 생성 이벤트 발행 실패 - UserId: {}, RecommendationId: {}, Error: {}", 
                            userId, recommendationId, ex.getMessage(), ex);
                }
            });

        } catch (Exception e) {
            log.error("❌ 추천 생성 이벤트 발행 중 오류 발생 - UserId: {}, RecommendationId: {}, Error: {}", 
                    userId, recommendationId, e.getMessage(), e);
        }
    }
}
