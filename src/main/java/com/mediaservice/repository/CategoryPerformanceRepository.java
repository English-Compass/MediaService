package com.mediaservice.repository;

import com.mediaservice.dto.CategoryPerformanceView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 카테고리별 성과 뷰 Repository
 */
@Repository
public interface CategoryPerformanceRepository extends JpaRepository<CategoryPerformanceView, Long> {
    
    /**
     * 특정 사용자의 카테고리별 성과를 조회합니다.
     * 
     * @param userId 사용자 ID
     * @return 카테고리별 성과 목록
     */
    @Query(value = "SELECT * FROM category_performance_view WHERE user_id = :userId", nativeQuery = true)
    List<CategoryPerformanceView> findByUserId(@Param("userId") Long userId);
}
