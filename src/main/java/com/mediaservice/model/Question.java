package com.mediaservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 문제 마스터 테이블 엔티티
 */
@Entity
@Table(name = "question")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    
    @Id
    @Column(name = "question_id", length = 255)
    private String questionId;
    
    @Column(name = "question_text", columnDefinition = "TEXT", nullable = false)
    private String questionText;
    
    @Column(name = "option_a", length = 500, nullable = false)
    private String optionA;
    
    @Column(name = "option_b", length = 500, nullable = false)
    private String optionB;
    
    @Column(name = "option_c", length = 500, nullable = false)
    private String optionC;
    
    @Column(name = "correct_answer", length = 1, nullable = false)
    private String correctAnswer;
    
    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;
    
    @Column(name = "major_category", length = 50, nullable = false)
    private String majorCategory;
    
    @Column(name = "minor_category", length = 50, nullable = false)
    private String minorCategory;
    
    @Column(name = "question_type", length = 50, nullable = false)
    private String questionType;
    
    @Column(name = "difficulty_level", nullable = false)
    private Integer difficultyLevel;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
