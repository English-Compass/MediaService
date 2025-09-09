package com.mediaservice.enums;

/**
 * 미디어 콘텐츠 유형을 정의하는 enum
 */
public enum MediaType {
    
    /**
     * 영화 콘텐츠
     * - 영화, 애니메이션
     * - OTT 플랫폼 영화
     */
    MOVIE("영화"),
    
    /**
     * 드라마 콘텐츠
     * - TV 드라마, 웹드라마
     * - OTT 플랫폼 드라마
     */
    DRAMA("드라마"),
    
    /**
     * 오디오북 콘텐츠
     * - 오디오북
     * - 유료/무료 오디오북 플랫폼
     */
    AUDIOBOOK("오디오북"),
    
    /**
     * 유튜브 비디오 콘텐츠
     * - 유튜브 동영상
     * - 교육 비디오
     */
    YOUTUBE_VIDEO("유튜브 비디오");
    
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

