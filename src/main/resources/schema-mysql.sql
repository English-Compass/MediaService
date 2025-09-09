-- =====================================================
-- 미디어 추천 서비스 데이터베이스 스키마 (MySQL 호환)
-- 목적: 생성된 미디어 추천 결과 저장 및 사용자 상호작용 관리
-- =====================================================

-- =====================================================
-- 1. 미디어 추천 결과 테이블 (media_recommendations) - 단순화된 버전
-- =====================================================
CREATE TABLE IF NOT EXISTS media_recommendations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- 추천 생성 정보
    recommendation_id VARCHAR(255) NOT NULL UNIQUE,
    user_id VARCHAR(255) NOT NULL,
    session_id VARCHAR(255),
    
    -- 추천 유형 및 상태
    recommendation_type VARCHAR(50) NOT NULL,
    
    -- 콘텐츠 기본 정보
    title VARCHAR(500) NOT NULL,
    description TEXT,
    url VARCHAR(1000),
    thumbnail_url VARCHAR(1000),
    play_url VARCHAR(1000),
    
    -- 미디어 유형 및 플랫폼
    media_type VARCHAR(20) NOT NULL,
    platform VARCHAR(100) NOT NULL,
    
    -- 콘텐츠 메타데이터
    difficulty_level VARCHAR(20),
    estimated_duration INT,
    language VARCHAR(10) DEFAULT 'en',
    category VARCHAR(100),
    
    -- 추천 이유 및 점수
    recommendation_reason TEXT,
    
    -- 유튜브 전용 정보
    video_id VARCHAR(100),
    channel_name VARCHAR(200),
    view_count VARCHAR(50),
    published_at DATE,
    
    -- 프롬프트 정보
    prompt_used TEXT,
    
    -- 생성 시간
    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 인덱스 생성
CREATE INDEX idx_media_recommendations_user ON media_recommendations(user_id);
CREATE INDEX idx_media_recommendations_session ON media_recommendations(session_id);
CREATE INDEX idx_media_recommendations_type ON media_recommendations(recommendation_type);
CREATE INDEX idx_media_recommendations_created ON media_recommendations(created_at);
CREATE INDEX idx_media_recommendations_user_type ON media_recommendations(user_id, recommendation_type);
CREATE INDEX idx_media_recommendations_user_created ON media_recommendations(user_id, created_at DESC);

-- =====================================================
-- 2. 사용자 추천 상호작용 테이블 (user_recommendation_interactions)
-- =====================================================
CREATE TABLE IF NOT EXISTS user_recommendation_interactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- 추천 결과 연결
    recommendation_id BIGINT NOT NULL,
    
    -- 사용자 정보
    user_id VARCHAR(255) NOT NULL,
    
    -- 상호작용 정보
    interaction_type VARCHAR(50) NOT NULL COMMENT 'CLICK, PLAY, LIKE, DISLIKE, SHARE, BOOKMARK',
    interaction_value VARCHAR(255) COMMENT '상호작용 값 (예: 재생 시간, 평점 등)',
    
    -- 상호작용 메타데이터
    interaction_metadata TEXT COMMENT '상호작용 메타데이터 (JSON)',
    
    -- 생성 시간
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- 외래키 제약조건
    FOREIGN KEY (recommendation_id) REFERENCES media_recommendations(id) ON DELETE CASCADE,
    
    -- 인덱스
    INDEX idx_user_recommendation_interactions_recommendation (recommendation_id),
    INDEX idx_user_recommendation_interactions_user (user_id),
    INDEX idx_user_recommendation_interactions_type (interaction_type),
    INDEX idx_user_recommendation_interactions_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 3. 추천 성과 메트릭 테이블 (recommendation_performance_metrics)
-- =====================================================
CREATE TABLE IF NOT EXISTS recommendation_performance_metrics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- 추천 결과 연결
    recommendation_id BIGINT NOT NULL,
    
    -- 성과 지표
    click_rate DECIMAL(5,2) DEFAULT 0.00 COMMENT '클릭률 (%)',
    play_rate DECIMAL(5,2) DEFAULT 0.00 COMMENT '재생률 (%)',
    completion_rate DECIMAL(5,2) DEFAULT 0.00 COMMENT '완료률 (%)',
    engagement_score DECIMAL(5,2) DEFAULT 0.00 COMMENT '참여도 점수',
    
    -- 시간 기반 메트릭
    avg_watch_time INT DEFAULT 0 COMMENT '평균 시청 시간 (초)',
    total_views INT DEFAULT 0 COMMENT '총 조회수',
    unique_viewers INT DEFAULT 0 COMMENT '고유 시청자 수',
    
    -- 피드백 메트릭
    like_count INT DEFAULT 0 COMMENT '좋아요 수',
    dislike_count INT DEFAULT 0 COMMENT '싫어요 수',
    share_count INT DEFAULT 0 COMMENT '공유 수',
    bookmark_count INT DEFAULT 0 COMMENT '북마크 수',
    
    -- 분석 기간
    analysis_start_date TIMESTAMP,
    analysis_end_date TIMESTAMP,
    
    -- 생성 시간
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- 외래키 제약조건
    FOREIGN KEY (recommendation_id) REFERENCES media_recommendations(id) ON DELETE CASCADE,
    
    -- 인덱스
    INDEX idx_recommendation_performance_metrics_recommendation (recommendation_id),
    INDEX idx_recommendation_performance_metrics_engagement (engagement_score DESC),
    INDEX idx_recommendation_performance_metrics_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =====================================================
-- 스키마 생성 완료
-- =====================================================
