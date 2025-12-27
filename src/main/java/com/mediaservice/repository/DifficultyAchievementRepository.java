package com.mediaservice.repository;

import com.mediaservice.dto.DifficultyAchievementView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 난이도별 성취도 뷰 Repository
 */
@Repository
public interface DifficultyAchievementRepository extends JpaRepository<DifficultyAchievementView, Long> {
    
    /**
     * 특정 사용자의 난이도별 성취도를 조회합니다.
     * 
     * @param userId 사용자 ID
     * @return 난이도별 성취도 목록
     */
    @Query(value = "SELECT * FROM difficulty_achievement_view WHERE user_id = :userId", nativeQuery = true)
    List<DifficultyAchievementView> findByUserId(@Param("userId") Long userId);
}
