-- =====================================================
-- 사용자 성과 분석을 위한 뷰 생성 스크립트
-- =====================================================

-- =====================================================
-- 12. 카테고리별 성과 뷰 (Category Performance View)
-- =====================================================
CREATE OR REPLACE VIEW category_performance_view AS
SELECT 
    ls.user_id,
    q.major_category,
    q.minor_category,
    
    -- 카테고리별 통계
    COUNT(qa.id) as questions_solved,
    SUM(CASE WHEN qa.is_correct = 1 THEN 1 ELSE 0 END) as correct_answers,
    
    -- 카테고리별 숙련도 (정답률, %)
    ROUND(
        (SUM(CASE WHEN qa.is_correct = 1 THEN 1 ELSE 0 END) * 100.0 / 
         NULLIF(COUNT(qa.id), 0)), 2
    ) as category_proficiency,
    
    -- 카테고리별 평균 풀이 시간
    ROUND(AVG(qa.time_spent), 2) as avg_category_solve_time,
    
    -- 카테고리별 최고 성과일
    MAX(qa.answered_at) as last_category_practice_date
    
FROM learning_sessions ls
JOIN question_answer qa ON ls.session_id = qa.session_id
JOIN question q ON qa.question_id = q.question_id
GROUP BY ls.user_id, q.major_category, q.minor_category;

-- =====================================================
-- 13. 난이도별 성취도 뷰 (Difficulty Achievement View)
-- =====================================================
CREATE OR REPLACE VIEW difficulty_achievement_view AS
SELECT 
    ls.user_id,
    q.difficulty_level,
    
    -- 난이도별 통계
    COUNT(qa.id) as questions_solved,
    SUM(CASE WHEN qa.is_correct = 1 THEN 1 ELSE 0 END) as correct_answers,
    
    -- 난이도별 성취도 (정답률, %)
    ROUND(
        (SUM(CASE WHEN qa.is_correct = 1 THEN 1 ELSE 0 END) * 100.0 / 
         NULLIF(COUNT(qa.id), 0)), 2
    ) as difficulty_achievement_rate,
    
    -- 난이도별 평균 풀이 시간
    ROUND(AVG(qa.time_spent), 2) as avg_difficulty_solve_time,
    
    -- 난이도별 도전 횟수
    ROUND(AVG(qa.solve_count), 2) as avg_attempts_per_question
    
FROM learning_sessions ls
JOIN question_answer qa ON ls.session_id = qa.session_id
JOIN question q ON qa.question_id = q.question_id
GROUP BY ls.user_id, q.difficulty_level;
