package com.mediaservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 세션별 문제 답변 정보 엔티티
 */
@Entity
@Table(name = "session_question")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionQuestion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "session_id", length = 255, nullable = false)
    private String sessionId;
    
    @Column(name = "question_id", length = 255, nullable = false)
    private String questionId;
    
    @Column(name = "user_answer", length = 1)
    private String userAnswer;
    
    @Column(name = "is_correct")
    private Boolean isCorrect;
    
    @Column(name = "time_spent")
    private Integer timeSpent;
    
    @Column(name = "attempt_count", columnDefinition = "INT DEFAULT 1")
    private Integer attemptCount;
    
    @Column(name = "answered_at")
    private LocalDateTime answeredAt;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.answeredAt == null) {
            this.answeredAt = now;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
