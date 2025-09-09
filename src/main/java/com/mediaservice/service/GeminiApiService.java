package com.mediaservice.service;

import com.mediaservice.event.LearningCompletedEvent;
import com.mediaservice.model.UserPerformanceSummary;

import java.util.List;

public interface GeminiApiService {

    /**
     * 학습 세션 완료 이벤트를 분석하여 Perplexity용 검색 프롬프트를 생성합니다.
     *
     * @param event 학습 세션 완료 이벤트
     * @return Perplexity API에 보낼 검색 프롬프트
     */
    String generateSearchPromptForRealTime(LearningCompletedEvent event);

    /**
     * 사용자 성과 요약을 분석하여 Perplexity용 검색 프롬프트를 생성합니다.
     *
     * @param userPerformance 사용자 성과 요약
     * @param selectedGenres 선택된 장르 목록
     * @return Perplexity API에 보낼 검색 프롬프트
     */
    String generateSearchPromptForUserRequested(UserPerformanceSummary userPerformance, List<String> selectedGenres);
}
