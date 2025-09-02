package com.mediaservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserActivitySummary {
    
    // =====================================================
    // 기본 사용자 정보
    // =====================================================
    private String userId;                       // 사용자 ID
    private LocalDateTime analysisStartDate;     // 분석 시작일 (예: 1주일 전)
    private LocalDateTime analysisEndDate;       // 분석 종료일 (현재)
    private String analysisPeriod;               // 분석 기간 (예: "1주일", "1개월")
    
    // =====================================================
    // 학습 활동 통계 (user_learning_analytics 뷰 기반)
    // =====================================================
    private Integer totalSessions;               // 총 학습 세션 수 (user_learning_analytics.total_sessions)
    private Integer completedSessions;           // 완료된 세션 수 (status = 'COMPLETED'인 세션)
    private Integer totalQuestionsSolved;        // 총 풀이한 문제 수 (user_learning_analytics.total_questions_solved)
    private Integer totalCorrectAnswers;         // 총 정답 수 (user_learning_analytics.total_correct_answers)
    private Integer totalWrongAnswers;           // 총 오답 수 (user_learning_analytics.total_questions_solved - total_correct_answers)
    private Double overallAccuracyRate;          // 전체 정답률 % (user_learning_analytics.accuracy_rate)
    private Double overallErrorRate;             // 전체 오답률 % (user_learning_analytics.error_rate)
    private Double avgSolveTime;                 // 평균 풀이 시간 초 (user_learning_analytics.avg_solve_time)
    private Integer totalLearningTimeMinutes;    // 총 학습 시간 분 (user_learning_analytics.total_learning_time_minutes)
    
    // =====================================================
    // 카테고리별 성과 분석 (category_performance_view 기반)
    // =====================================================
    private List<CategoryPerformance> categoryPerformances;
    
    // =====================================================
    // 난이도별 성취도 분석 (difficulty_achievement_view 기반)
    // =====================================================
    private List<DifficultyAchievement> difficultyAchievements;
    
    // =====================================================
    // 학습 패턴 분석
    // =====================================================
    private Map<String, Integer> sessionTypeDistribution;    // 세션 타입별 분포 (learning_sessions.session_type)
    private Map<String, Integer> questionTypeDistribution;   // 문제 유형별 분포 (question.question_type)
    private List<String> preferredLearningTimes;             // 선호하는 학습 시간대 (추정)
    private List<String> weakAreas;                          // 약점 영역 (낮은 정답률 카테고리)
    private List<String> strongAreas;                        // 강점 영역 (높은 정답률 카테고리)
    
    // =====================================================
    // 트렌드 및 변화 분석
    // =====================================================
    private Double accuracyTrend;               // 정답률 변화 트렌드 (증가/감소 %)
    private Double timeEfficiencyTrend;        // 시간 효율성 변화 트렌드
    private String learningProgress;            // 학습 진도 상태 (user_learning_analytics.learning_progress_rate 기반)
    private List<String> recentImprovements;   // 최근 개선된 영역
    private List<String> areasNeedingFocus;    // 집중이 필요한 영역
    
    // =====================================================
    // 내부 클래스: 카테고리별 성과 (category_performance_view 기반)
    // =====================================================
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryPerformance {
        private String majorCategory;           // 대분류 (category_performance_view.major_category)
        private String minorCategory;           // 소분류 (category_performance_view.minor_category)
        private Integer questionsSolved;        // 풀이한 문제 수 (category_performance_view.questions_solved)
        private Integer correctAnswers;         // 정답 수 (category_performance_view.correct_answers)
        private Double proficiencyRate;         // 숙련도 % (category_performance_view.category_proficiency)
        private Double avgSolveTime;            // 평균 풀이 시간 초 (category_performance_view.avg_category_solve_time)
        private LocalDateTime lastPracticeDate; // 마지막 연습일 (category_performance_view.last_category_practice_date)
    }
    
    // =====================================================
    // 내부 클래스: 난이도별 성취도 (difficulty_achievement_view 기반)
    // =====================================================
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DifficultyAchievement {
        private Integer difficultyLevel;        // 난이도 (difficulty_achievement_view.difficulty_level: 1, 2, 3)
        private Integer questionsSolved;        // 풀이한 문제 수 (difficulty_achievement_view.questions_solved)
        private Integer correctAnswers;         // 정답 수 (difficulty_achievement_view.correct_answers)
        private Double achievementRate;         // 성취도 % (difficulty_achievement_view.difficulty_achievement_rate)
        private Double avgSolveTime;            // 평균 풀이 시간 초 (difficulty_achievement_view.avg_difficulty_solve_time)
        private Double avgAttemptsPerQuestion;  // 문제당 평균 시도 횟수 (difficulty_achievement_view.avg_attempts_per_question)
    }
}
