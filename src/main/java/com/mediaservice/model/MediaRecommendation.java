package com.mediaservice.model;

import com.mediaservice.enums.MediaType;
import com.mediaservice.enums.RecommendationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 미디어 추천 정보를 담는 엔티티
 */
@Entity
@Table(name = "media_recommendations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaRecommendation {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "recommendation_id", nullable = false, unique = true)
    private String recommendationId;
    
    @Column(name = "user_id", nullable = false)
    private String userId;
    
    @Column(name = "title", nullable = false)
    private String title;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "url", nullable = false)
    private String url;
    
    @Column(name = "thumbnail_url")
    private String thumbnailUrl;
    
    @Column(name = "play_url")
    private String playUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "media_type", nullable = false)
    private MediaType mediaType;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "recommendation_type", nullable = false)
    private RecommendationType recommendationType;
    
    @Column(name = "session_id")
    private String sessionId;
    
    @Column(name = "prompt_used", columnDefinition = "TEXT")
    private String promptUsed;
    
    @Column(name = "platform")
    private String platform;
    
    @Column(name = "difficulty_level")
    private String difficultyLevel;
    
    @Column(name = "recommendation_reason", columnDefinition = "TEXT")
    private String recommendationReason;
    
    @Column(name = "estimated_duration")
    private Integer estimatedDuration;
    
    @Column(name = "language")
    private String language;
    
    @Column(name = "category")
    private String category;
    
    @Column(name = "video_id")
    private String videoId;
    
    @Column(name = "channel_name")
    private String channelName;
    
    @Column(name = "view_count")
    private String viewCount;
    
    @Column(name = "published_at")
    private String publishedAt;
    
    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.generatedAt == null) {
            this.generatedAt = now;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}



