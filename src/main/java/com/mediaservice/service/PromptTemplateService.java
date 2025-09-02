package com.mediaservice.service;

import com.mediaservice.dto.UserPerformanceSummary;
import com.mediaservice.event.LearningCompletedEvent;

import java.util.List;

/**
 * AI 추천을 위한 프롬프트 템플릿 서비스
 */
public interface PromptTemplateService {
    
    /**
     * 실시간 세션 기반 추천을 위한 프롬프트를 생성합니다.
     * 
     * @param event 학습 세션 완료 이벤트
     * @return AI 추천용 프롬프트
     */
    String generateRealTimeSessionPrompt(LearningCompletedEvent event);
    
    /**
     * 사용자 요청 기반 추천을 위한 프롬프트를 생성합니다.
     * 
     * @param userPerformance 사용자 성과 요약 정보
     * @param selectedGenres 사용자가 선택한 장르 목록
     * @return AI 추천용 프롬프트
     */
    String generateUserRequestedPrompt(UserPerformanceSummary userPerformance, List<String> selectedGenres);
}


