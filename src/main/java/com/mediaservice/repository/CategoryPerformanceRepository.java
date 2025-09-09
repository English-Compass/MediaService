package com.mediaservice.repository;

import com.mediaservice.dto.CategoryPerformanceView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 카테고리별 성과 뷰 Repository
 */
@Repository
public class CategoryPerformanceRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 특정 사용자의 카테고리별 성과를 조회합니다.
     * 
     * @param userId 사용자 ID
     * @return 카테고리별 성과 목록
     */
    public List<CategoryPerformanceView> findByUserId(String userId) {
        String sql = "SELECT * FROM category_performance_view WHERE user_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(CategoryPerformanceView.class), userId);
    }
}
