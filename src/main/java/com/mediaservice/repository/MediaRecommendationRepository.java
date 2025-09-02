package com.mediaservice.repository;

import com.mediaservice.model.MediaRecommendation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 미디어 추천을 위한 Repository
 */
@Repository
public interface MediaRecommendationRepository extends JpaRepository<MediaRecommendation, Long> {
    
    /**
     * 사용자 ID로 추천 목록을 조회합니다.
     */
    List<MediaRecommendation> findByUserIdOrderByGeneratedAtDesc(Long userId);
    
    /**
     * 사용자 ID와 추천 유형으로 추천 목록을 조회합니다.
     */
    List<MediaRecommendation> findByUserIdAndRecommendationTypeOrderByGeneratedAtDesc(Long userId, String recommendationType);
    
    /**
     * 사용자 ID와 세션 ID로 추천 목록을 조회합니다.
     */
    List<MediaRecommendation> findByUserIdAndSessionIdOrderByGeneratedAtDesc(Long userId, String sessionId);
}
