package com.mediaservice.kafka;

import com.mediaservice.event.LearningCompletedEvent;
import com.mediaservice.service.RealTimeRecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

/**
 * 학습 이벤트를 소비하여 실시간 미디어 추천을 트리거하는 Kafka Consumer
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LearningEventConsumer {

    private final RealTimeRecommendationService realTimeRecommendationService;

    @KafkaListener(
        topics = "${kafka.topics.learning-events}",
        groupId = "${kafka.consumer.group-id}",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void handleLearningCompletedEvent(
            @Payload LearningCompletedEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) Integer partition,
            @Header(KafkaHeaders.OFFSET) Long offset
    ) {
        try {
            log.info("📚 학습 완료 이벤트 수신 - Topic: {}, Partition: {}, Offset: {}", 
                    topic, partition, offset);
            log.info("📋 이벤트 내용: {}", event);
            
            // 미디어 추천 서비스 호출
            realTimeRecommendationService.generateRecommendations(event);
            
            log.info("✅ 학습 완료 이벤트 처리 완료 - UserId: {}", event.getUserId());
            
        } catch (Exception e) {
            log.error("❌ 학습 완료 이벤트 처리 중 오류 발생 - UserId: {}, Error: {}", 
                    event.getUserId(), e.getMessage(), e);
            // TODO: 에러 처리 및 재시도 로직 구현
            throw e; // 현재는 예외를 다시 던져서 Kafka의 재시도 메커니즘 활용
        }
    }
}


