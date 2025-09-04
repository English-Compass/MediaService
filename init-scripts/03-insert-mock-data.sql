-- =====================================================
-- 목 데이터 삽입 스크립트
-- 목적: 개발 및 테스트를 위한 샘플 데이터 생성
-- =====================================================

USE media_service_db;

-- =====================================================
-- 1. 샘플 문제 데이터 삽입
-- =====================================================
INSERT INTO question (question_id, question_text, option_a, option_b, option_c, correct_answer, major_category, minor_category, question_type, explanation, difficulty_level) VALUES
('Q001', 'What is the past tense of "go"?', 'goed', 'went', 'gone', 'B', 'Grammar', 'Past Tense', 'Multiple Choice', 'The past tense of "go" is "went".', 1),
('Q002', 'Choose the correct article: "___ apple is red."', 'A', 'An', 'The', 'B', 'Grammar', 'Articles', 'Multiple Choice', 'Use "an" before words starting with a vowel sound.', 1),
('Q003', 'What does "procrastinate" mean?', 'To delay or postpone', 'To hurry up', 'To organize', 'A', 'Vocabulary', 'Advanced Words', 'Multiple Choice', 'Procrastinate means to delay or postpone doing something.', 3),
('Q004', 'Which sentence is correct?', 'I have went to school.', 'I have gone to school.', 'I have go to school.', 'B', 'Grammar', 'Present Perfect', 'Multiple Choice', 'Present perfect uses "have/has + past participle".', 2),
('Q005', 'What is the synonym of "happy"?', 'Sad', 'Joyful', 'Angry', 'B', 'Vocabulary', 'Synonyms', 'Multiple Choice', 'Joyful is a synonym of happy.', 1);

-- =====================================================
-- 2. 샘플 학습 세션 데이터 삽입
-- =====================================================
INSERT INTO learning_sessions (session_id, user_id, created_at, started_at, completed_at, status, session_type, total_questions, answered_questions, correct_answers, wrong_answers, progress_percentage) VALUES
('SESS001', 'user123', '2024-01-15 10:00:00', '2024-01-15 10:00:00', '2024-01-15 10:30:00', 'COMPLETED', 'PRACTICE', 5, 5, 3, 2, 100.0),
('SESS002', 'user123', '2024-01-16 14:00:00', '2024-01-16 14:00:00', '2024-01-16 14:25:00', 'COMPLETED', 'PRACTICE', 5, 5, 4, 1, 100.0),
('SESS003', 'user456', '2024-01-15 09:00:00', '2024-01-15 09:00:00', '2024-01-15 09:20:00', 'COMPLETED', 'PRACTICE', 5, 5, 2, 3, 100.0);

-- =====================================================
-- 3. 샘플 세션-문제 연결 데이터 삽입
-- =====================================================
INSERT INTO session_question (session_id, question_id, question_order) VALUES
('SESS001', 'Q001', 1),
('SESS001', 'Q002', 2),
('SESS001', 'Q003', 3),
('SESS001', 'Q004', 4),
('SESS001', 'Q005', 5),
('SESS002', 'Q001', 1),
('SESS002', 'Q002', 2),
('SESS002', 'Q003', 3),
('SESS002', 'Q004', 4),
('SESS002', 'Q005', 5),
('SESS003', 'Q001', 1),
('SESS003', 'Q002', 2),
('SESS003', 'Q003', 3),
('SESS003', 'Q004', 4),
('SESS003', 'Q005', 5);

-- =====================================================
-- 4. 샘플 문제 답변 데이터 삽입
-- =====================================================
INSERT INTO question_answer (session_id, question_id, session_type, user_answer, is_correct, time_spent, answered_at, solve_count) VALUES
-- SESS001 (user123) - 3개 정답, 2개 오답
('SESS001', 'Q001', 'PRACTICE', 'B', 1, 15, '2024-01-15 10:05:00', 1),
('SESS001', 'Q002', 'PRACTICE', 'A', 0, 20, '2024-01-15 10:08:00', 1),
('SESS001', 'Q003', 'PRACTICE', 'A', 1, 45, '2024-01-15 10:12:00', 1),
('SESS001', 'Q004', 'PRACTICE', 'B', 1, 30, '2024-01-15 10:18:00', 1),
('SESS001', 'Q005', 'PRACTICE', 'A', 0, 25, '2024-01-15 10:22:00', 1),

-- SESS002 (user123) - 4개 정답, 1개 오답 (개선됨)
('SESS002', 'Q001', 'PRACTICE', 'B', 1, 12, '2024-01-16 14:05:00', 1),
('SESS002', 'Q002', 'PRACTICE', 'B', 1, 18, '2024-01-16 14:08:00', 1),
('SESS002', 'Q003', 'PRACTICE', 'A', 1, 40, '2024-01-16 14:12:00', 1),
('SESS002', 'Q004', 'PRACTICE', 'B', 1, 28, '2024-01-16 14:18:00', 1),
('SESS002', 'Q005', 'PRACTICE', 'A', 0, 22, '2024-01-16 14:22:00', 1),

-- SESS003 (user456) - 2개 정답, 3개 오답
('SESS003', 'Q001', 'PRACTICE', 'A', 0, 18, '2024-01-15 09:05:00', 1),
('SESS003', 'Q002', 'PRACTICE', 'B', 1, 15, '2024-01-15 09:08:00', 1),
('SESS003', 'Q003', 'PRACTICE', 'B', 0, 50, '2024-01-15 09:12:00', 1),
('SESS003', 'Q004', 'PRACTICE', 'A', 0, 35, '2024-01-15 09:18:00', 1),
('SESS003', 'Q005', 'PRACTICE', 'B', 1, 20, '2024-01-15 09:22:00', 1);
