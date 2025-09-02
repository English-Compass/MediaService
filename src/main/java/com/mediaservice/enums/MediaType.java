package com.mediaservice.enums;

/**
 * 미디어 콘텐츠 유형을 정의하는 enum
 */
public enum MediaType {
    
    /**
     * 비디오 콘텐츠
     * - 유튜브 동영상
     * - 영화, 드라마, 애니메이션
     * - 교육 비디오
     */
    VIDEO("비디오"),
    
    /**
     * 오디오 콘텐츠
     * - 오디오북
     * - 팟캐스트
     * - 음악
     */
    AUDIO("오디오"),
    
    /**
     * 텍스트 기반 콘텐츠
     * - 블로그 글
     * - 기사
     * - 전자책
     */
    ARTICLE("기사"),
    
    /**
     * 도서 콘텐츠
     * - 전자책
     * - 인쇄 도서
     * - 학습 자료
     */
    BOOK("도서");
    
    private final String description;
    
    MediaType(String description) {
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

