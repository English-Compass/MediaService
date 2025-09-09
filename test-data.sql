-- 테스트용 사용자 성과 데이터 생성

-- 1. 사용자 성과 뷰 데이터 삽입 (category_performance_view)
INSERT INTO category_performance_view (user_id, major_category, minor_category, total_questions, correct_answers, accuracy_rate, avg_solve_time, weak_areas) VALUES
('user123', '영어', '문법', 20, 15, 75.0, 45.5, '시제,조동사'),
('user123', '영어', '어휘', 15, 12, 80.0, 38.2, '동의어,반의어'),
('user123', '수학', '대수', 25, 18, 72.0, 52.1, '이차방정식,함수'),
('user123', '수학', '기하', 18, 14, 77.8, 48.7, '원의성질,삼각형'),
('user123', '과학', '물리', 22, 16, 72.7, 55.3, '힘과운동,에너지'),
('user123', '과학', '화학', 16, 13, 81.3, 42.8, '화학반응,원소주기율'),
('user123', '사회', '역사', 30, 24, 80.0, 41.2, '근현대사,세계사'),
('user123', '사회', '지리', 12, 9, 75.0, 39.6, '기후,지형');

-- 2. 난이도별 성취도 뷰 데이터 삽입 (difficulty_achievement_view)
INSERT INTO difficulty_achievement_view (user_id, difficulty_level, questions_solved, correct_answers, difficulty_achievement_rate, avg_attempts_per_question, avg_difficulty_solve_time) VALUES
('user123', 1, 35, 32, 91.4, 1.1, 25.3),
('user123', 2, 28, 22, 78.6, 1.4, 38.7),
('user123', 3, 20, 14, 70.0, 1.8, 52.1),
('user123', 4, 15, 8, 53.3, 2.2, 68.4),
('user123', 5, 8, 3, 37.5, 2.8, 85.2);

-- 3. 기존 추천 데이터가 있다면 삭제
DELETE FROM media_recommendation WHERE user_id = 'user123';

-- 4. 테스트용 기존 추천 데이터 생성
INSERT INTO media_recommendation (recommendation_id, user_id, recommendation_type, title, description, media_type, platform, duration_minutes, url, generated_at, status) VALUES
('rec_001', 'user123', 'USER_REQUESTED', '영어 문법 마스터하기', '시제와 조동사 학습을 위한 추천 콘텐츠', 'VIDEO', 'YouTube', 15, 'https://youtube.com/watch?v=example1', '2025-09-04 10:00:00', 'ACTIVE'),
('rec_002', 'user123', 'USER_REQUESTED', '수학 대수 기초', '이차방정식과 함수 개념 정리', 'VIDEO', 'YouTube', 25, 'https://youtube.com/watch?v=example2', '2025-09-04 11:00:00', 'ACTIVE'),
('rec_003', 'user123', 'REAL_TIME', '과학 물리 실험', '힘과 운동 원리 이해하기', 'VIDEO', 'YouTube', 20, 'https://youtube.com/watch?v=example3', '2025-09-04 12:00:00', 'ACTIVE');

