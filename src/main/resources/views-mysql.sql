-- =====================================================
-- 미디어 추천 서비스 데이터베이스 뷰 (MySQL 호환)
-- 목적: 사용자 성과 분석을 위한 뷰 생성
-- =====================================================

-- =====================================================
-- 1. 카테고리별 성과 뷰 (category_performance_view)
-- =====================================================
CREATE OR REPLACE VIEW category_performance_view AS
SELECT 
    ls.user_id,
    q.major_category,
    q.minor_category,
    COUNT(qa.id) as questions_solved,
    SUM(CASE WHEN qa.is_correct = 1 THEN 1 ELSE 0 END) as correct_answers,
    ROUND((SUM(CASE WHEN qa.is_correct = 1 THEN 1 ELSE 0 END) * 100.0 / NULLIF(COUNT(qa.id), 0)), 2) as category_proficiency,
    ROUND(AVG(qa.time_spent), 2) as avg_category_solve_time,
    MAX(qa.answered_at) as last_category_practice_date
FROM learning_sessions ls
JOIN question_answer qa ON ls.session_id = qa.session_id
JOIN question q ON qa.question_id = q.question_id
GROUP BY ls.user_id, q.major_category, q.minor_category;

-- =====================================================
-- 2. 난이도별 성취도 뷰 (difficulty_achievement_view)
-- =====================================================
CREATE OR REPLACE VIEW difficulty_achievement_view AS
SELECT 
    ls.user_id,
    q.difficulty_level,
    COUNT(qa.id) as questions_solved,
    SUM(CASE WHEN qa.is_correct = 1 THEN 1 ELSE 0 END) as correct_answers,
    ROUND((SUM(CASE WHEN qa.is_correct = 1 THEN 1 ELSE 0 END) * 100.0 / NULLIF(COUNT(qa.id), 0)), 2) as difficulty_achievement_rate,
    ROUND(AVG(qa.time_spent), 2) as avg_difficulty_solve_time,
    ROUND(AVG(qa.solve_count), 2) as avg_attempts_per_question
FROM learning_sessions ls
JOIN question_answer qa ON ls.session_id = qa.session_id
JOIN question q ON qa.question_id = q.question_id
GROUP BY ls.user_id, q.difficulty_level;

-- =====================================================
-- 3. 사용자 학습 분석 뷰 (user_learning_analytics)
-- =====================================================
CREATE OR REPLACE VIEW user_learning_analytics AS
SELECT 
    ls.user_id,
    -- 기본 학습 통계
    COUNT(DISTINCT ls.session_id) as total_sessions,
    COUNT(qa.id) as total_questions_solved,
    SUM(CASE WHEN qa.is_correct = 1 THEN 1 ELSE 0 END) as total_correct_answers,
    -- 정답률 (%)
    ROUND((SUM(CASE WHEN qa.is_correct = 1 THEN 1 ELSE 0 END) * 100.0 / NULLIF(COUNT(qa.id), 0)), 2) as accuracy_rate,
    -- 오답률 (%)
    ROUND((SUM(CASE WHEN qa.is_correct = 0 THEN 1 ELSE 0 END) * 100.0 / NULLIF(COUNT(qa.id), 0)), 2) as error_rate,
    -- 평균 풀이 시간 (초)
    ROUND(AVG(qa.time_spent), 2) as avg_solve_time,
    -- 문제 재시도율 (%)
    ROUND((SUM(qa.solve_count - 1) * 100.0 / NULLIF(COUNT(qa.id), 0)), 2) as retry_rate,
    -- 학습 진도율 (완료된 세션 비율, %)
    ROUND((COUNT(CASE WHEN ls.status = 'COMPLETED' THEN 1 END) * 100.0 / NULLIF(COUNT(DISTINCT ls.session_id), 0)), 2) as learning_progress_rate,
    -- 최근 학습일
    MAX(qa.answered_at) as last_learning_date,
    -- 총 학습 시간 (분)
    ROUND(SUM(qa.time_spent) / 60.0, 2) as total_learning_time_minutes
FROM learning_sessions ls
LEFT JOIN question_answer qa ON ls.session_id = qa.session_id
GROUP BY ls.user_id;

-- =====================================================
-- 4. 문제 통계 뷰 (question_stats_view)
-- =====================================================
CREATE OR REPLACE VIEW question_stats_view AS
SELECT 
    q.question_id,
    q.question_type,
    q.major_category as category,
    q.difficulty_level,
    -- 기본 통계
    COUNT(qa.id) as total_solve_count,
    SUM(CASE WHEN qa.is_correct = 1 THEN 1 ELSE 0 END) as correct_solve_count,
    -- 정답률 (소수점 2자리)
    ROUND((SUM(CASE WHEN qa.is_correct = 1 THEN 1 ELSE 0 END) * 100.0 / NULLIF(COUNT(qa.id), 0)), 2) as correct_rate,
    -- 평균 풀이 시간 (초)
    ROUND(AVG(qa.time_spent), 2) as avg_solve_time,
    -- 사용자당 평균 시도 횟수
    ROUND(COUNT(qa.id) * 1.0 / NULLIF(COUNT(DISTINCT ls.user_id), 0), 2) as avg_solve_attempts_per_user,
    -- 고유 사용자 수
    COUNT(DISTINCT ls.user_id) as distinct_user_count
FROM question q
LEFT JOIN question_answer qa ON q.question_id = qa.question_id
LEFT JOIN learning_sessions ls ON qa.session_id = ls.session_id
GROUP BY q.question_id, q.question_type, q.major_category, q.difficulty_level;

-- =====================================================
-- 뷰 생성 완료
-- =====================================================
