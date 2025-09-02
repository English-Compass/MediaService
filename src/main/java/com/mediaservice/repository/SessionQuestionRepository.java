package com.mediaservice.repository;

import com.mediaservice.model.SessionQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 세션별 문제 답변 정보를 조회하는 Repository
 */
@Repository
public interface SessionQuestionRepository extends JpaRepository<SessionQuestion, Long> {
    
    /**
     * 세션 ID로 문제 상세 정보를 조회합니다.
     * question 테이블과 session_question 테이블을 JOIN하여 상세 정보를 가져옵니다.
     */
    @Query("SELECT q.questionId, q.questionText, q.optionA, q.optionB, q.optionC, " +
           "q.correctAnswer, q.explanation, q.majorCategory, q.minorCategory, " +
           "q.difficultyLevel, sq.userAnswer, sq.isCorrect, sq.timeSpent, sq.attemptCount " +
           "FROM Question q " +
           "JOIN SessionQuestion sq ON q.questionId = sq.questionId " +
           "WHERE sq.sessionId = :sessionId")
    List<Object[]> findSessionQuestionDetails(@Param("sessionId") String sessionId);
    
    /**
     * 세션 ID로 세션 문제 목록을 조회합니다.
     */
    List<SessionQuestion> findBySessionIdOrderByCreatedAtAsc(String sessionId);
}
