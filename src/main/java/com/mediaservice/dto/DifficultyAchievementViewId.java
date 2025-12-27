package com.mediaservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DifficultyAchievementView의 복합 키 클래스
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DifficultyAchievementViewId implements Serializable {
    private Long userId;
    private Integer difficultyLevel;
}
