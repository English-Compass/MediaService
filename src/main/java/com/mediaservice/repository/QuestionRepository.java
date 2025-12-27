package com.mediaservice.repository;

import com.mediaservice.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * 문제를 위한 Repository
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, String> {
}
