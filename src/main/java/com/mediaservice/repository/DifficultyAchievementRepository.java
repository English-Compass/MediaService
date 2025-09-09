package com.mediaservice.repository;

import com.mediaservice.dto.DifficultyAchievementView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 난이도별 성취도 뷰 Repository
 */
@Repository
public class DifficultyAchievementRepository {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    /**
     * 특정 사용자의 난이도별 성취도를 조회합니다.
     * 
     * @param userId 사용자 ID
     * @return 난이도별 성취도 목록
     */
    public List<DifficultyAchievementView> findByUserId(String userId) {
        String sql = "SELECT * FROM difficulty_achievement_view WHERE user_id = ?";
        return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(DifficultyAchievementView.class), userId);
    }
}
