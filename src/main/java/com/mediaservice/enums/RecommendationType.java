package com.mediaservice.enums;

/**
 * 미디어 추천 유형을 정의하는 enum
 */
public enum RecommendationType {
    
    /**
     * 실시간 세션 기반 추천
     * - 학습 세션 완료 후 즉시 생성
     * - 짧은 유튜브 동영상 위주
     * - 특정 세션과 연결됨
     */
    REAL_TIME_SESSION("실시간 세션 기반"),
    
    /**
     * 사용자 요청 기반 추천
     * - 사용자가 장르를 선택하여 요청할 때마다 생성
     * - 다양한 미디어 유형 (유튜브 + 영화/드라마 + 오디오북)
     * - 특정 세션과 연결되지 않음
     */
    USER_REQUESTED("사용자 요청 기반");
    
    private final String description;
    
    RecommendationType(String description) {
        this.description = description;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return this.name() + " (" + this.description + ")";
    }
}
