package com.mediaservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 카테고리별 성과 뷰 DTO
 * 
 * category_performance_view 테이블의 데이터를 매핑합니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryPerformanceView {
    
    private Long userId;
    private String majorCategory;
    private String minorCategory;
    
    // 카테고리별 통계
    private Long questionsSolved;
    private Long correctAnswers;
    
    // 카테고리별 숙련도 (정답률, %)
    private Double categoryProficiency;
    
    // 카테고리별 평균 풀이 시간
    private Double avgCategorySolveTime;
    
    // 카테고리별 최고 성과일
    private LocalDateTime lastCategoryPracticeDate;
}
