-- =====================================================
-- 데이터베이스 초기화 스크립트
-- 목적: MySQL 컨테이너 시작 시 자동으로 실행되는 스크립트
-- =====================================================

-- 데이터베이스 생성 (이미 docker-compose.yml에서 생성됨)
-- CREATE DATABASE IF NOT EXISTS media_service_db;

-- 데이터베이스 사용
USE media_service_db;

-- =====================================================
-- 참조 테이블 생성 (Problem Service DDL 기반)
-- =====================================================

-- 1. 문제 테이블 (Question)
CREATE TABLE IF NOT EXISTS question (
    question_id VARCHAR(255) NOT NULL PRIMARY KEY,
    question_text TEXT NOT NULL COMMENT '문제 내용',
    option_a VARCHAR(500) NOT NULL COMMENT '선택지 A',
    option_b VARCHAR(500) NOT NULL COMMENT '선택지 B',
    option_c VARCHAR(500) NOT NULL COMMENT '선택지 C',
    correct_answer VARCHAR(1) NOT NULL COMMENT '정답 (A, B, C)',
    -- 카테고리 정보
    major_category VARCHAR(50) NOT NULL COMMENT '대분류',
    minor_category VARCHAR(50) NOT NULL COMMENT '소분류',
    question_type VARCHAR(50) NOT NULL COMMENT '문제 유형',
    -- 메타데이터
    explanation TEXT COMMENT '문제 해설',
    difficulty_level INT NOT NULL DEFAULT 1 COMMENT '난이도 (1: 초급, 2: 중급, 3: 상급)',
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    -- 인덱스
    INDEX idx_question_major_category (major_category),
    INDEX idx_question_minor_category (minor_category),
    INDEX idx_question_type (question_type),
    INDEX idx_question_difficulty (difficulty_level),
    INDEX idx_question_category_combo (major_category, minor_category, difficulty_level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='문제 마스터 테이블';

-- 2. 학습 세션 테이블 (LearningSession)
CREATE TABLE IF NOT EXISTS learning_sessions (
    session_id VARCHAR(255) NOT NULL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL COMMENT '사용자 ID',
    -- 시간 정보
    created_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '세션 생성 시간',
    started_at DATETIME(6) COMMENT '세션 시작 시간',
    completed_at DATETIME(6) COMMENT '세션 완료 시간',
    updated_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) ON UPDATE CURRENT_TIMESTAMP(6),
    -- 세션 메타데이터
    status ENUM('STARTED', 'IN_PROGRESS', 'COMPLETED') NOT NULL DEFAULT 'STARTED',
    session_type ENUM('PRACTICE', 'REVIEW', 'WRONG_ANSWER') NOT NULL DEFAULT 'PRACTICE',
    session_metadata TEXT COMMENT '세션 메타데이터 (JSON)',
    -- 통계 정보
    total_questions INT COMMENT '총 문제 수',
    answered_questions INT DEFAULT 0 COMMENT '답변한 문제 수',
    correct_answers INT DEFAULT 0 COMMENT '정답 수',
    wrong_answers INT DEFAULT 0 COMMENT '오답 수',
    progress_percentage DOUBLE DEFAULT 0.0 COMMENT '진행률 (%)',
    -- 인덱스
    INDEX idx_learning_sessions_user (user_id),
    INDEX idx_learning_sessions_status (status),
    INDEX idx_learning_sessions_type (session_type),
    INDEX idx_learning_sessions_user_status (user_id, status),
    INDEX idx_learning_sessions_created (created_at),
    INDEX idx_learning_sessions_completed (completed_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='학습 세션 테이블';

-- 3. 세션-문제 연결 테이블 (SessionQuestion)
CREATE TABLE IF NOT EXISTS session_question (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(255) NOT NULL COMMENT '세션 ID',
    question_id VARCHAR(255) NOT NULL COMMENT '문제 ID',
    question_order INT NOT NULL COMMENT '문제 순서 (1, 2, 3, ...)',
    -- 외래키 제약조건
    FOREIGN KEY (session_id) REFERENCES learning_sessions(session_id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES question(question_id) ON DELETE CASCADE,
    -- 유니크 제약조건
    UNIQUE KEY uk_session_question (session_id, question_id),
    UNIQUE KEY uk_session_order (session_id, question_order),
    -- 인덱스
    INDEX idx_session_question_session (session_id),
    INDEX idx_session_question_question (question_id),
    INDEX idx_session_question_order (session_id, question_order)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='세션별 문제 할당 테이블';

-- 4. 문제 답변 테이블 (QuestionAnswer)
CREATE TABLE IF NOT EXISTS question_answer (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    session_id VARCHAR(255) NOT NULL COMMENT '세션 ID',
    question_id VARCHAR(255) NOT NULL COMMENT '문제 ID',
    session_type VARCHAR(50) NOT NULL COMMENT '세션 타입',
    -- 답변 정보
    user_answer VARCHAR(1) NOT NULL COMMENT '사용자 답변 (A, B, C)',
    is_correct BIT(1) NOT NULL COMMENT '정답 여부',
    time_spent INT COMMENT '풀이 시간 (초)',
    answered_at DATETIME(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6) COMMENT '답변 시간',
    solve_count INT NOT NULL DEFAULT 1 COMMENT '해당 문제 풀이 횟수',
    -- 외래키 제약조건
    FOREIGN KEY (session_id) REFERENCES learning_sessions(session_id) ON DELETE CASCADE,
    FOREIGN KEY (question_id) REFERENCES question(question_id) ON DELETE CASCADE,
    -- 유니크 제약조건 (한 세션에서 같은 문제는 한 번만 답변)
    UNIQUE KEY uk_session_question_answer (session_id, question_id),
    -- 인덱스
    INDEX idx_question_answer_session (session_id),
    INDEX idx_question_answer_question (question_id),
    INDEX idx_question_answer_correct (is_correct),
    INDEX idx_question_answer_time (time_spent),
    INDEX idx_question_answer_answered_at (answered_at),
    INDEX idx_question_answer_user_session (session_id),
    INDEX idx_question_answer_combo (question_id, is_correct, time_spent)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='사용자 문제 답변 테이블';
