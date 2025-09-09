package com.mediaservice.service;

import com.mediaservice.model.MediaRecommendation;
import java.util.List;

/**
 * 사용자 요청 기반 미디어 추천 서비스
 *
 * 사용자가 장르를 선택하여 요청할 때마다
 * 선호 장르 + 카테고리별/난이도별 성과를 반영한 추천을 생성합니다.
 */
public interface UserRequestedRecommendationService {

    /**
     * 사용자 요청 미디어 추천을 생성합니다.
     *
     * @param userId 사용자 ID
     * @param selectedGenres 사용자가 선택한 장르 목록
     * @return 생성된 추천 데이터 목록
     */
    List<MediaRecommendation> generateUserRequestedRecommendations(String userId, List<String> selectedGenres);

    /**
     * 사용자 요청에 따른 즉시 추천을 생성합니다.
     * REST API에서 호출됩니다.
     */
    void generateRecommendationsOnDemand();
}
