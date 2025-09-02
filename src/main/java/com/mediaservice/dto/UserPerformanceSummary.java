package com.mediaservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 주기적 추천을 위한 사용자 성과 요약 정보
 * 
 * 핵심 성과 지표만 포함하여 효율적인 추천 생성을 지원합니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPerformanceSummary {
    
    // =====================================================
    // 1. 카테고리별 성과 (대분류 + 소분류)
    // =====================================================
    private Map<String, Double> categoryPerformance; // "여행-가족": 80.0, "비즈니스-회의": 65.0
    
    // =====================================================
    // 2. 난이도별 성과
    // =====================================================
    private Map<Integer, Double> difficultyPerformance; // 1(초급): 90.0, 2(중급): 75.0, 3(고급): 45.0
}
