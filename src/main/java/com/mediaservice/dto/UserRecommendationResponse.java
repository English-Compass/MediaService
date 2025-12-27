package com.mediaservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.mediaservice.model.MediaRecommendation;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 사용자 추천 응답 DTO
 * 
 * 사용자가 요청한 장르 기반 추천 결과를 반환합니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRecommendationResponse {
    
    /**
     * 응답 상태
     */
    private String status;
    
    /**
     * 응답 메시지
     */
    private String message;
    
    /**
     * 생성된 추천 개수
     */
    private int totalRecommendations;
    
    /**
     * 사용자가 선택한 장르
     */
    private List<String> selectedGenres;
    
    /**
     * 추천 생성 시간
     */
    private LocalDateTime generatedAt;
    
    /**
     * 추천 결과 목록
     */
    private List<MediaRecommendation> recommendations;
    
    /**
     * 성공 응답 생성
     */
    public static UserRecommendationResponse success(
            int totalRecommendations, 
            List<String> selectedGenres, 
            List<MediaRecommendation> recommendations) {
        
        return UserRecommendationResponse.builder()
                .status("SUCCESS")
                .message("사용자 요청 기반 추천이 성공적으로 생성되었습니다.")
                .totalRecommendations(totalRecommendations)
                .selectedGenres(selectedGenres)
                .generatedAt(LocalDateTime.now())
                .recommendations(recommendations)
                .build();
    }
    
    /**
     * 실패 응답 생성
     */
    public static UserRecommendationResponse error(String errorMessage) {
        return UserRecommendationResponse.builder()
                .status("ERROR")
                .message(errorMessage)
                .totalRecommendations(0)
                .generatedAt(LocalDateTime.now())
                .build();
    }
}

