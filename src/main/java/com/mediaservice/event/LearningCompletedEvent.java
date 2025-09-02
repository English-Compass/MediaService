package com.mediaservice.event;

import com.mediaservice.dto.SessionQuestionDetail;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LearningCompletedEvent {
    
    // =====================================================
    // 학습 세션 기본 정보 (learning_sessions 테이블)
    // =====================================================
    private String sessionId;                    // 세션 ID (learning_sessions.session_id)
    private String userId;                       // 사용자 ID (learning_sessions.user_id)
    private LocalDateTime sessionCreatedAt;      // 세션 생성 시간 (learning_sessions.created_at)
    private LocalDateTime sessionStartedAt;      // 세션 시작 시간 (learning_sessions.started_at)
    private LocalDateTime sessionCompletedAt;    // 세션 완료 시간 (learning_sessions.completed_at)
    private String sessionStatus;                // 세션 상태 (learning_sessions.status: STARTED, IN_PROGRESS, COMPLETED)
    private String sessionType;                  // 세션 타입 (learning_sessions.session_type: PRACTICE, REVIEW, WRONG_ANSWER)
    private String sessionMetadata;              // 세션 메타데이터 JSON (learning_sessions.session_metadata)
    
    // =====================================================
    // 학습 세션 통계 정보 (learning_sessions 테이블)
    // =====================================================
    private Integer totalQuestions;              // 총 문제 수 (learning_sessions.total_questions)
    private Integer answeredQuestions;           // 답변한 문제 수 (learning_sessions.answered_questions)
    private Integer correctAnswers;              // 정답 수 (learning_sessions.correct_answers)
    private Integer wrongAnswers;                // 오답 수 (learning_sessions.wrong_answers)
    private Double progressPercentage;           // 진행률 % (learning_sessions.progress_percentage)
    
    // =====================================================
    // 문제 답변 통계 정보 (question_answer 테이블 집계)
    // =====================================================
    private Integer totalSolveCount;             // 총 풀이 횟수 (question_answer.solve_count 합계)
    private Double avgTimeSpent;                 // 평균 풀이 시간 초 (question_answer.time_spent 평균)
    private LocalDateTime lastAnsweredAt;        // 마지막 답변 시간 (question_answer.answered_at 최대값)
    
    // =====================================================
    // 문제 카테고리 정보 (question 테이블 집계)
    // =====================================================
    private String majorCategory;                // 대분류 (question.major_category)
    private String minorCategory;                // 소분류 (question.minor_category)
    private String questionType;                 // 문제 유형 (question.question_type)
    private Integer avgDifficultyLevel;          // 평균 난이도 (question.difficulty_level 평균)
    
    // =====================================================
    // 계산된 성과 지표
    // =====================================================
    private Double accuracyRate;                 // 정답률 % (correct_answers / answered_questions * 100)
    private Double errorRate;                    // 오답률 % (wrong_answers / answered_questions * 100)
    private Integer totalLearningTimeMinutes;    // 총 학습 시간 분 (time_spent 합계 / 60)
    
    // =====================================================
    // 상세 문제 정보 (세션별 문제 상세)
    // =====================================================
    private List<SessionQuestionDetail> sessionQuestions;  // 세션별 문제 상세 정보
}


