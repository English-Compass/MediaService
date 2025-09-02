package com.mediaservice.service;

import com.mediaservice.model.MediaRecommendation;

import java.util.List;

public interface GeminiApiService {
    
    /**
     * Gemini API를 호출하여 미디어 추천을 생성합니다.
     * 
     * @param prompt Gemini API에 보낼 프롬프트
     * @return 추천된 미디어 콘텐츠 목록
     */
    List<MediaRecommendation> generateRecommendations(String prompt);
}


