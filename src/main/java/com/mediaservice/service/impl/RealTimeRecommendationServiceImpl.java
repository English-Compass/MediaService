package com.mediaservice.service.impl;

import com.mediaservice.dto.SessionQuestionDetail;
import com.mediaservice.event.LearningCompletedEvent;
import com.mediaservice.model.MediaRecommendation;
import com.mediaservice.repository.MediaRecommendationRepository;
import com.mediaservice.repository.SessionQuestionRepository;
import com.mediaservice.service.RealTimeRecommendationService;
import com.mediaservice.service.GeminiApiService;
import com.mediaservice.service.PerplexityApiService;
import com.mediaservice.enums.RecommendationType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 실시간 세션 기반 미디어 추천 서비스 구현체
 * 
 * 학습 세션이 완료된 직후 즉시 실행되어 사용자의 약점을 보완할 수 있는
 * 짧은 유튜브 동영상 위주의 미디어 콘텐츠를 추천합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RealTimeRecommendationServiceImpl implements RealTimeRecommendationService {

    private final GeminiApiService geminiApiService;
    private final PerplexityApiService perplexityApiService;
    private final MediaRecommendationRepository mediaRecommendationRepository;
    private final SessionQuestionRepository sessionQuestionRepository;

    @Override
    @Transactional
    public void generateRecommendations(LearningCompletedEvent event) {
        try {
            log.info("🎯 실시간 세션 기반 미디어 추천 생성 시작 - UserId: {}, SessionId: {}", 
                    event.getUserId(), event.getSessionId());
            
            // 세션 문제 상세 정보 조회 (실제 DB에서 가져옴)
            List<SessionQuestionDetail> sessionQuestions = getSessionQuestionDetails(event.getSessionId());
            log.info("📊 세션 문제 상세 정보 조회 완료 - 문제 수: {}", sessionQuestions.size());
            
            // 이벤트에 세션 문제 정보 설정
            event.setSessionQuestions(sessionQuestions);
            
            // 1. Gemini API로 학습 세션 분석하여 검색 프롬프트 생성
            String searchPrompt = geminiApiService.generateSearchPromptForRealTime(event);
            log.info("🤖 Gemini API로 검색 프롬프트 생성 완료: {}", searchPrompt);
            
            // 2. Perplexity API로 실제 YouTube 영상 검색 및 추천 생성 (3분 이하)
            List<MediaRecommendation> recommendations = perplexityApiService.searchYouTubeVideosForRealTime(searchPrompt);
            log.info("🔍 Perplexity API를 통한 실시간 추천 생성 완료 - 추천 개수: {}", recommendations.size());
            
            // 추천 결과에 실시간 추천 정보 추가
            recommendations.forEach(recommendation -> {
                recommendation.setRecommendationId(generateRecommendationId(event.getUserId(), RecommendationType.REAL_TIME_SESSION));
                recommendation.setUserId(event.getUserId());
                recommendation.setRecommendationType(RecommendationType.REAL_TIME_SESSION);
                recommendation.setSessionId(event.getSessionId());
                recommendation.setGeneratedAt(LocalDateTime.now());
                recommendation.setPromptUsed(searchPrompt);
            });
            
            // 데이터베이스에 저장
            List<MediaRecommendation> savedRecommendations = mediaRecommendationRepository.saveAll(recommendations);
            log.info("💾 실시간 세션 기반 추천 저장 완료 - 저장된 추천 개수: {}", savedRecommendations.size());
            
            // 추천 결과 로깅
            savedRecommendations.forEach(recommendation -> 
                log.debug("📺 실시간 추천 생성됨 - ID: {}, 제목: {}, 미디어 타입: {}", 
                        recommendation.getId(), recommendation.getTitle(), recommendation.getMediaType()));
            
        } catch (Exception e) {
            log.error("❌ 실시간 세션 기반 미디어 추천 생성 중 오류 발생 - UserId: {}, SessionId: {}, Error: {}", 
                    event.getUserId(), event.getSessionId(), e.getMessage(), e);
            throw new RuntimeException("실시간 세션 기반 미디어 추천 생성 실패", e);
        }
    }
    
    /**
     * 세션 ID로 문제 상세 정보를 조회합니다. (실제 DB에서 가져옴)
     */
    private List<SessionQuestionDetail> getSessionQuestionDetails(String sessionId) {
        try {
            log.debug("🔍 세션 문제 상세 정보 조회 시작 - SessionId: {}", sessionId);
            
            // Repository에서 raw 데이터 조회
            List<Object[]> rawData = sessionQuestionRepository.findSessionQuestionDetails(sessionId);
            log.debug("📊 Raw 데이터 조회 완료 - 데이터 수: {}", rawData.size());
            
            // Object[]를 SessionQuestionDetail로 변환
            List<SessionQuestionDetail> details = new ArrayList<>();
            for (Object[] row : rawData) {
                try {
                    SessionQuestionDetail detail = mapToSessionQuestionDetail(row);
                    details.add(detail);
                } catch (Exception e) {
                    log.warn("⚠️ 데이터 매핑 중 오류 발생 - Row: {}, Error: {}", Arrays.toString(row), e.getMessage());
                }
            }
            
            log.info("✅ 세션 문제 상세 정보 변환 완료 - 변환된 데이터 수: {}", details.size());
            return details;
            
        } catch (Exception e) {
            log.error("❌ 세션 문제 상세 정보 조회 중 오류 발생 - SessionId: {}, Error: {}", sessionId, e.getMessage(), e);
            // 오류 발생 시 빈 리스트 반환하여 추천 생성은 계속 진행
            return new ArrayList<>();
        }
    }
    
    /**
     * Raw 데이터를 SessionQuestionDetail로 변환합니다.
     */
    private SessionQuestionDetail mapToSessionQuestionDetail(Object[] rawData) {
        try {
            return SessionQuestionDetail.builder()
                .questionId((String) rawData[0])
                .questionText((String) rawData[1])
                .options(Arrays.asList((String) rawData[2], (String) rawData[3], (String) rawData[4]))
                .correctAnswer((String) rawData[5])
                .explanation((String) rawData[6])
                .majorCategory((String) rawData[7])
                .minorCategory((String) rawData[8])
                .difficultyLevel((Integer) rawData[9])
                .userAnswer((String) rawData[10])
                .isCorrect((Boolean) rawData[11])
                .timeSpent((Integer) rawData[12])
                .attemptCount((Integer) rawData[13])
                .build();
                
        } catch (Exception e) {
            log.error("❌ 데이터 매핑 중 오류 발생 - RawData: {}, Error: {}", Arrays.toString(rawData), e.getMessage());
            throw new RuntimeException("데이터 매핑 실패", e);
        }
    }
    
    /**
     * 사용자 선호 장르를 조회합니다. (임시 구현)
     * 향후 User Service를 호출하여 실제 사용자 정보를 가져올 예정입니다.
     */
    // 장르 기반 관심사 조회 로직 제거: 실시간 추천은 학습 카테고리만 사용
    
    /**
     * 추천 결과를 로깅합니다. (향후 DB 저장으로 대체)
     */
    private void logRecommendations(List<MediaRecommendation> recommendations) {
        log.info("📊 생성된 추천 결과:");
        
        for (int i = 0; i < recommendations.size(); i++) {
            MediaRecommendation rec = recommendations.get(i);
            log.info("  {}. {} - {}", 
                    i + 1, 
                    rec.getTitle(), 
                    rec.getPlatform());
            log.info("     설명: {}", rec.getDescription());
            log.info("     추천 이유: {}", rec.getRecommendationReason());
            log.info("     난이도: {} | 예상 시간: {}분", 
                    rec.getDifficultyLevel(), 
                    rec.getEstimatedDuration());
            log.info("     링크: {}", rec.getUrl());
            log.info("");
        }
    }
    
    /**
     * 추천 ID를 생성합니다.
     */
    private String generateRecommendationId(String userId, RecommendationType type) {
        return String.format("REC_%s_%s_%s", 
            userId, 
            type.name(), 
            java.util.UUID.randomUUID().toString().substring(0, 8));
    }
}
