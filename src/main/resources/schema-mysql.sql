-- =====================================================
-- 미디어 추천 서비스 데이터베이스 스키마 (MySQL 호환)
-- 목적: 생성된 미디어 추천 결과 저장 및 사용자 상호작용 관리
-- =====================================================

-- =====================================================
-- 1. 미디어 추천 결과 테이블 (media_recommendations)
-- =====================================================
CREATE TABLE IF NOT EXISTS media_recommendations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- 추천 생성 정보
    recommendation_id VARCHAR(255) NOT NULL UNIQUE,
    user_id VARCHAR(255) NOT NULL,
    session_id VARCHAR(255),
    
    -- 추천 유형 및 상태
    recommendation_type VARCHAR(50) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    
    -- 추천 생성 정보
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP,
    
    -- 추천 우선순위 및 점수
    priority_score DECIMAL(5,2) DEFAULT 0.00,
    relevance_score DECIMAL(5,2) DEFAULT 0.00,
    
    -- 분석 기간 정보 (주기적 추천의 경우)
    analysis_start_date TIMESTAMP,
    analysis_end_date TIMESTAMP,
    analysis_period VARCHAR(20)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 인덱스 생성
CREATE INDEX idx_media_recommendations_user ON media_recommendations(user_id);
CREATE INDEX idx_media_recommendations_session ON media_recommendations(session_id);
CREATE INDEX idx_media_recommendations_type ON media_recommendations(recommendation_type);
CREATE INDEX idx_media_recommendations_status ON media_recommendations(status);
CREATE INDEX idx_media_recommendations_created ON media_recommendations(created_at);
CREATE INDEX idx_media_recommendations_priority ON media_recommendations(priority_score DESC);
CREATE INDEX idx_media_recommendations_user_type_status ON media_recommendations(user_id, recommendation_type, status);
CREATE INDEX idx_media_recommendations_user_created ON media_recommendations(user_id, created_at DESC);

-- =====================================================
-- 2. 추천된 미디어 콘텐츠 테이블 (recommended_media_contents)
-- =====================================================
CREATE TABLE IF NOT EXISTS recommended_media_contents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- 추천 결과 연결
    recommendation_id VARCHAR(255) NOT NULL,
    content_order INT NOT NULL,
    
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
    content_relevance_score DECIMAL(5,2) DEFAULT 0.00,
    
    -- 유튜브 전용 정보
    video_id VARCHAR(100),
    channel_name VARCHAR(200),
    view_count VARCHAR(50),
    published_at DATE,
    
    -- 생성 시간
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- 유니크 제약조건
    CONSTRAINT uk_recommendation_content_order UNIQUE (recommendation_id, content_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 인덱스 생성
CREATE INDEX idx_recommended_media_recommendation ON recommended_media_contents(recommendation_id);
CREATE INDEX idx_recommended_media_type ON recommended_media_contents(media_type);
CREATE INDEX idx_recommended_media_platform ON recommended_media_contents(platform);
CREATE INDEX idx_recommended_media_category ON recommended_media_contents(category);
CREATE INDEX idx_recommended_media_difficulty ON recommended_media_contents(difficulty_level);
CREATE INDEX idx_recommended_media_video_id ON recommended_media_contents(video_id);
CREATE INDEX idx_recommended_media_relevance ON recommended_media_contents(content_relevance_score DESC);

-- =====================================================
-- 3. 사용자 추천 상호작용 테이블 (user_recommendation_interactions)
-- =====================================================
CREATE TABLE IF NOT EXISTS user_recommendation_interactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- 사용자 및 추천 연결
    user_id VARCHAR(255) NOT NULL,
    recommendation_id VARCHAR(255) NOT NULL,
    content_id BIGINT,
    
    -- 상호작용 유형
    interaction_type VARCHAR(20) NOT NULL,
    
    -- 상호작용 메타데이터
    interaction_value TEXT,
    interaction_metadata TEXT,
    
    -- 상호작용 시간
    interacted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    -- 유니크 제약조건 (같은 사용자가 같은 추천에 같은 상호작용을 중복으로 할 수 없음)
    CONSTRAINT uk_user_recommendation_interaction UNIQUE (user_id, recommendation_id, content_id, interaction_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 인덱스 생성
CREATE INDEX idx_user_interactions_user ON user_recommendation_interactions(user_id);
CREATE INDEX idx_user_interactions_recommendation ON user_recommendation_interactions(recommendation_id);
CREATE INDEX idx_user_interactions_content ON user_recommendation_interactions(content_id);
CREATE INDEX idx_user_interactions_type ON user_recommendation_interactions(interaction_type);
CREATE INDEX idx_user_interactions_interacted ON user_recommendation_interactions(interacted_at);
CREATE INDEX idx_user_interactions_user_type ON user_recommendation_interactions(user_id, interaction_type);

-- =====================================================
-- 4. 추천 성과 분석 테이블 (recommendation_performance_metrics)
-- =====================================================
CREATE TABLE IF NOT EXISTS recommendation_performance_metrics (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- 추천 결과 연결
    recommendation_id VARCHAR(255) NOT NULL,
    
    -- 성과 지표
    total_impressions INT DEFAULT 0,
    total_clicks INT DEFAULT 0,
    total_plays INT DEFAULT 0,
    total_likes INT DEFAULT 0,
    total_dislikes INT DEFAULT 0,
    total_shares INT DEFAULT 0,
    total_bookmarks INT DEFAULT 0,
    
    -- 전환율 계산
    click_through_rate DECIMAL(5,2) DEFAULT 0.00,
    play_rate DECIMAL(5,2) DEFAULT 0.00,
    engagement_rate DECIMAL(5,2) DEFAULT 0.00,
    
    -- 평균 지표
    avg_play_duration DECIMAL(8,2) DEFAULT 0.00,
    avg_interaction_score DECIMAL(5,2) DEFAULT 0.00,
    
    -- 업데이트 시간
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 인덱스 생성
CREATE INDEX idx_performance_recommendation ON recommendation_performance_metrics(recommendation_id);
CREATE INDEX idx_performance_engagement ON recommendation_performance_metrics(engagement_rate DESC);
CREATE INDEX idx_performance_updated ON recommendation_performance_metrics(updated_at DESC);

-- =====================================================
-- 5. 추천 설정 테이블 (recommendation_settings)
-- =====================================================
CREATE TABLE IF NOT EXISTS recommendation_settings (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    
    -- 사용자별 설정
    user_id VARCHAR(255) NOT NULL,
    
    -- 추천 설정
    enable_realtime_recommendations BOOLEAN DEFAULT TRUE,
    enable_periodic_recommendations BOOLEAN DEFAULT TRUE,
    preferred_categories TEXT,
    preferred_difficulty_levels TEXT,
    preferred_media_types TEXT,
    
    -- 알림 설정
    notification_enabled BOOLEAN DEFAULT TRUE,
    notification_frequency VARCHAR(20) DEFAULT 'IMMEDIATE',
    
    -- 개인정보 설정
    data_collection_enabled BOOLEAN DEFAULT TRUE,
    personalized_recommendations BOOLEAN DEFAULT TRUE,
    
    -- 생성 및 업데이트 시간
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    -- 유니크 제약조건
    CONSTRAINT uk_recommendation_settings_user UNIQUE (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 인덱스 생성
CREATE INDEX idx_settings_user ON recommendation_settings(user_id);

-- =====================================================
-- 스키마 생성 완료
-- =====================================================
