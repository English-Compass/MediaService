package com.mediaservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 추천 생성 이벤트
 * 사용자가 새로운 미디어 추천을 받았을 때 발행되는 이벤트
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationCreatedEvent {
    
    /**
     * 이벤트 타입
     */
    private String eventType = "RECOMMENDATION_CREATED";
    
    /**
     * 사용자 ID
     */
    private String userId;
    
    /**
     * 추천 ID
     */
    private String recommendationId;
    
    /**
     * 추천 타입 (USER_REQUESTED, REAL_TIME, PERIODIC)
     */
    private String recommendationType;
    
    /**
     * 추천된 미디어 개수
     */
    private Integer mediaCount;
    
    /**
     * 선택된 장르 목록
     */
    private List<String> genres;
    
    /**
     * 세션 ID (실시간 추천인 경우)
     */
    private String sessionId;
    
    /**
     * 이벤트 발생 시간
     */
    private LocalDateTime timestamp;
    
    /**
     * 추천 생성 이벤트 생성
     */
    public static RecommendationCreatedEvent create(String userId, String recommendationId, 
                                                   String recommendationType, Integer mediaCount, 
                                                   List<String> genres, String sessionId) {
        return RecommendationCreatedEvent.builder()
                .userId(userId)
                .recommendationId(recommendationId)
                .recommendationType(recommendationType)
                .mediaCount(mediaCount)
                .genres(genres)
                .sessionId(sessionId)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
