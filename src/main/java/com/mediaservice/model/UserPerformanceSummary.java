package com.mediaservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPerformanceSummary {
    private String userId;
    private Double overallAccuracy;
    private Integer totalStudyTime;
    private Map<String, Double> categoryPerformance;
    private Map<Integer, Double> difficultyPerformance;
}
