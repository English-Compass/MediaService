package com.mediaservice.service;

import com.mediaservice.model.MediaRecommendation;

import java.util.List;

public interface PerplexityApiService {

    /**
     * 실시간 추천용: Perplexity API를 호출하여 YouTube 영상만 검색합니다 (0~3분).
     *
     * @param searchPrompt Gemini가 생성한 검색 프롬프트
     * @return 추천된 YouTube 영상 목록
     */
    List<MediaRecommendation> searchYouTubeVideosForRealTime(String searchPrompt);

    /**
     * 사용자 요청용: Perplexity API를 호출하여 다양한 미디어 콘텐츠를 검색합니다.
     *
     * @param searchPrompt Gemini가 생성한 검색 프롬프트
     * @return 추천된 미디어 콘텐츠 목록 (YouTube, 영화, 드라마, 오디오북)
     */
    List<MediaRecommendation> searchMediaForUserRequested(String searchPrompt);
}
