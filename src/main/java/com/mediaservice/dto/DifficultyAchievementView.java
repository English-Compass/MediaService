package com.mediaservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

/**
 * 난이도별 성취도 뷰 DTO
 * 
 * difficulty_achievement_view 테이블의 데이터를 매핑합니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "difficulty_achievement_view")
@IdClass(DifficultyAchievementViewId.class)
public class DifficultyAchievementView {
    
    @Id
    private Long userId;
    
    @Id
    private Integer difficultyLevel;
    
    // 난이도별 통계
    private Long questionsSolved;
    private Long correctAnswers;
    
    // 난이도별 성취도 (정답률, %)
    private Double difficultyAchievementRate;
    
    // 난이도별 평균 풀이 시간
    private Double avgDifficultySolveTime;
    
    // 난이도별 도전 횟수
    private Double avgAttemptsPerQuestion;
}
