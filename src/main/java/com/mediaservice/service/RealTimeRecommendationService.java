package com.mediaservice.service;

import com.mediaservice.event.LearningCompletedEvent;

/**
 * 실시간 세션 기반 미디어 추천 서비스
 * 
 * 학습 세션이 완료된 직후 즉시 실행되어 사용자의 약점을 보완할 수 있는
 * 짧은 유튜브 동영상 위주의 미디어 콘텐츠를 추천합니다.
 */
public interface RealTimeRecommendationService {
    
    /**
     * 학습 세션 완료 이벤트를 받아 실시간 미디어 추천을 생성합니다.
     * 
     * @param event 학습 세션 완료 이벤트 (세션별 문제 상세 정보 포함)
     */
    void generateRecommendations(LearningCompletedEvent event);
}
