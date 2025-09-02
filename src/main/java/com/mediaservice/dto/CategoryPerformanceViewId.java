package com.mediaservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * CategoryPerformanceView의 복합 키 클래스
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryPerformanceViewId implements Serializable {
    private Long userId;
    private String majorCategory;
    private String minorCategory;
}
