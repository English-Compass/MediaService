package com.mediaservice.service.impl;

import com.mediaservice.dto.SessionQuestionDetail;
import com.mediaservice.event.LearningCompletedEvent;
import com.mediaservice.model.Question;
import com.mediaservice.model.SessionQuestion;
import com.mediaservice.repository.QuestionRepository;
import com.mediaservice.repository.SessionQuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 테스트를 위한 Mock 데이터를 생성하는 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MockDataService implements CommandLineRunner {

    private final QuestionRepository questionRepository;
    private final SessionQuestionRepository sessionQuestionRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("🚀 Mock 데이터 생성 시작");
        
        try {
            // Mock 문제 데이터 생성
            createMockQuestions();
            
            // Mock 세션 문제 데이터 생성
            createMockSessionQuestions();
            
            log.info("✅ Mock 데이터 생성 완료");
            
        } catch (Exception e) {
            log.error("❌ Mock 데이터 생성 중 오류 발생 - Error: {}", e.getMessage(), e);
        }
    }
    
    /**
     * Mock 문제 데이터를 생성합니다.
     */
    private void createMockQuestions() {
        if (questionRepository.count() > 0) {
            log.info("📝 이미 문제 데이터가 존재합니다. Mock 데이터 생성을 건너뜁니다.");
            return;
        }
        
        List<Question> questions = Arrays.asList(
            // 여행 - 가족 카테고리
            Question.builder()
                .questionId("Q001")
                .questionText("We're going on a family trip to _____ next month.")
                .optionA("go")
                .optionB("take")
                .optionC("make")
                .correctAnswer("B")
                .explanation("go on a trip은 '여행을 가다'라는 의미입니다.")
                .majorCategory("여행")
                .minorCategory("가족")
                .questionType("MULTIPLE_CHOICE")
                .difficultyLevel(2)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build(),
                
            Question.builder()
                .questionId("Q002")
                .questionText("My family and I _____ to Paris last summer.")
                .optionA("go")
                .optionB("went")
                .optionC("gone")
                .correctAnswer("B")
                .explanation("last summer가 있으므로 과거시제 went를 사용해야 합니다.")
                .majorCategory("여행")
                .minorCategory("가족")
                .questionType("MULTIPLE_CHOICE")
                .difficultyLevel(2)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build(),
                
            // 비즈니스 - 회사 카테고리
            Question.builder()
                .questionId("Q003")
                .questionText("I need to _____ a meeting with the client tomorrow.")
                .optionA("make")
                .optionB("do")
                .optionC("have")
                .correctAnswer("C")
                .explanation("have a meeting은 '회의를 하다'라는 의미입니다.")
                .majorCategory("비즈니스")
                .minorCategory("회사")
                .questionType("MULTIPLE_CHOICE")
                .difficultyLevel(2)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()
        );
        
        questionRepository.saveAll(questions);
        log.info("📝 Mock 문제 데이터 생성 완료 - {}개", questions.size());
    }
    
    /**
     * Mock 세션 문제 데이터를 생성합니다.
     */
    private void createMockSessionQuestions() {
        if (sessionQuestionRepository.count() > 0) {
            log.info("📝 이미 세션 문제 데이터가 존재합니다. Mock 데이터 생성을 건너뜁니다.");
            return;
        }
        
        List<SessionQuestion> sessionQuestions = Arrays.asList(
            // 세션 S001의 문제들
            SessionQuestion.builder()
                .sessionId("S001")
                .questionId("Q001")
                .userAnswer("A")
                .isCorrect(false)
                .timeSpent(15)
                .attemptCount(1)
                .answeredAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build(),
                
            SessionQuestion.builder()
                .sessionId("S001")
                .questionId("Q002")
                .userAnswer("B")
                .isCorrect(true)
                .timeSpent(12)
                .attemptCount(1)
                .answeredAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build(),
                
            SessionQuestion.builder()
                .sessionId("S001")
                .questionId("Q003")
                .userAnswer("A")
                .isCorrect(false)
                .timeSpent(20)
                .attemptCount(2)
                .answeredAt(LocalDateTime.now())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build()
        );
        
        sessionQuestionRepository.saveAll(sessionQuestions);
        log.info("📝 Mock 세션 문제 데이터 생성 완료 - {}개", sessionQuestions.size());
    }
}

