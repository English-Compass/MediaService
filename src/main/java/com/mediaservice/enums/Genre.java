package com.mediaservice.enums;

/**
 * 사용자가 선호하는 영화/드라마 장르를 정의하는 enum
 */
public enum Genre {
    
    ACTION("액션"),
    DRAMA("드라마"),
    COMEDY("코미디"),
    ROMANCE("로맨스"),
    THRILLER("스릴러"),
    HORROR("공포"),
    MYSTERY("미스터리"),
    SF("SF"),
    FANTASY("판타지"),
    CRIME("범죄"),
    WAR("전쟁"),
    MUSIC("음악"),
    ANIMATION("애니메이션"),
    DOCUMENTARY("다큐멘터리");
    
    private final String koreanName;
    
    Genre(String koreanName) {
        this.koreanName = koreanName;
    }
    
    public String getKoreanName() {
        return koreanName;
    }
    
    @Override
    public String toString() {
        return this.name() + " (" + this.koreanName + ")";
    }
}

