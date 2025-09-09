package com.mediaservice.service;

import com.mediaservice.event.RecommendationCreatedEvent;

import java.util.List;

/**
 * 이벤트 발행 서비스 인터페이스
 */
public interface EventPublisherService {
    
    /**
     * 추천 생성 이벤트 발행
     * 
     * @param userId 사용자 ID
     * @param recommendationId 추천 ID
     * @param recommendationType 추천 타입
     * @param mediaCount 추천된 미디어 개수
     * @param genres 선택된 장르 목록
     * @param sessionId 세션 ID (실시간 추천인 경우)
     */
    void publishRecommendationCreatedEvent(String userId, String recommendationId, 
                                          String recommendationType, Integer mediaCount, 
                                          List<String> genres, String sessionId);
}
