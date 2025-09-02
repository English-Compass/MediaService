package com.mediaservice.service.impl;

import com.mediaservice.dto.UserPerformanceSummary;
import com.mediaservice.dto.CategoryPerformanceView;
import com.mediaservice.dto.DifficultyAchievementView;
import com.mediaservice.enums.RecommendationType;
import com.mediaservice.model.MediaRecommendation;
import com.mediaservice.repository.MediaRecommendationRepository;
import com.mediaservice.repository.CategoryPerformanceRepository;
import com.mediaservice.repository.DifficultyAchievementRepository;
import com.mediaservice.service.GeminiApiService;
import com.mediaservice.service.PromptTemplateService;
import com.mediaservice.service.UserRequestedRecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 사용자 요청 기반 미디어 추천 서비스 구현체
 * 
 * 사용자가 장르를 선택하여 요청할 때마다
 * 선호 장르 + 카테고리별/난이도별 성과를 반영한 추천을 생성합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserRequestedRecommendationServiceImpl implements UserRequestedRecommendationService {

    private final PromptTemplateService promptTemplateService;
    private final GeminiApiService geminiApiService;
    private final MediaRecommendationRepository mediaRecommendationRepository;
    private final CategoryPerformanceRepository categoryPerformanceRepository;
    private final DifficultyAchievementRepository difficultyAchievementRepository;

    @Override
    @Transactional
    public void generateRecommendationsOnDemand() {
        try {
            log.info("🔄 사용자 요청 기반 미디어 추천 실행 시작");
            // TODO: REST API에서 호출될 때 사용자 ID와 선택된 장르를 받아서 처리
            log.info("📝 사용자가 장르를 선택하고 추천을 요청할 때 호출됩니다.");
            
        } catch (Exception e) {
            log.error("❌ 사용자 요청 기반 미디어 추천 실행 중 오류 발생 - Error: {}", e.getMessage(), e);
            throw new RuntimeException("사용자 요청 기반 미디어 추천 실행 실패", e);
        }
    }

    @Override
    @Transactional
    public int generateUserRequestedRecommendations(Long userId, List<String> selectedGenres) {
        try {
            log.info("🔄 사용자 {} 사용자 요청 미디어 추천 생성 시작 - 선택된 장르: {}", userId, selectedGenres);
            
            // 1. 사용자 성과 요약 생성 (핵심 지표만)
            UserPerformanceSummary userPerformance = generateUserPerformanceSummary(userId);
            log.debug("📊 사용자 {} 성과 요약 생성 완료", userId);
            
            // 2. 사용자 요청 기반 프롬프트 생성
            String prompt = promptTemplateService.generateUserRequestedPrompt(userPerformance, selectedGenres);
            log.debug("📝 사용자 {} 사용자 요청 기반 프롬프트 생성 완료", userId);
            
            // 3. Gemini API 호출하여 추천 생성
            List<MediaRecommendation> recommendations = geminiApiService.generateRecommendations(prompt);
            log.info("🤖 사용자 {} Gemini API를 통한 사용자 요청 추천 생성 완료 - 추천 개수: {}", userId, recommendations.size());
            
            // 4. 추천 결과에 사용자 요청 추천 정보 추가
            recommendations.forEach(recommendation -> {
                recommendation.setUserId(userId);
                recommendation.setRecommendationType(RecommendationType.USER_REQUESTED);
                recommendation.setSessionId(null); // 사용자 요청 추천은 특정 세션과 연결되지 않음
                recommendation.setGeneratedAt(LocalDateTime.now());
                recommendation.setPromptUsed(prompt);
            });
            
            // 5. 데이터베이스에 저장
            List<MediaRecommendation> savedRecommendations = mediaRecommendationRepository.saveAll(recommendations);
            log.info("💾 사용자 {} 사용자 요청 추천 저장 완료 - 저장된 추천 개수: {}", userId, savedRecommendations.size());
            
            // 6. 추천 결과 로깅
            savedRecommendations.forEach(recommendation -> 
                log.debug("📺 사용자 요청 추천 생성됨 - 사용자: {}, ID: {}, 제목: {}, 미디어 타입: {}", 
                        userId, recommendation.getId(), recommendation.getTitle(), recommendation.getMediaType()));
            
            return savedRecommendations.size();
            
        } catch (Exception e) {
            log.error("❌ 사용자 {} 사용자 요청 추천 생성 중 오류 발생 - Error: {}", userId, e.getMessage(), e);
            throw new RuntimeException("사용자 " + userId + " 사용자 요청 추천 생성 실패", e);
        }
    }
    
    /**
     * 사용자 성과 요약 정보를 생성합니다. (실제 DB 데이터 사용)
     */
    private UserPerformanceSummary generateUserPerformanceSummary(Long userId) {
        log.debug("📊 사용자 {} 성과 요약 정보 생성 시작", userId);
        
        try {
            // 1. 카테고리별 성과 조회
            List<CategoryPerformanceView> categoryPerformances = categoryPerformanceRepository.findByUserId(userId);
            Map<String, Double> categoryPerformance = new HashMap<>();
            
            for (CategoryPerformanceView cpv : categoryPerformances) {
                String categoryKey = cpv.getMajorCategory() + "-" + cpv.getMinorCategory();
                categoryPerformance.put(categoryKey, cpv.getCategoryProficiency());
            }
            
            log.debug("📊 사용자 {} 카테고리별 성과 조회 완료 - 카테고리 수: {}", userId, categoryPerformance.size());
            
            // 2. 난이도별 성과 조회
            List<DifficultyAchievementView> difficultyAchievements = difficultyAchievementRepository.findByUserId(userId);
            Map<Integer, Double> difficultyPerformance = new HashMap<>();
            
            for (DifficultyAchievementView dav : difficultyAchievements) {
                difficultyPerformance.put(dav.getDifficultyLevel(), dav.getDifficultyAchievementRate());
            }
            
            log.debug("📊 사용자 {} 난이도별 성과 조회 완료 - 난이도 수: {}", userId, difficultyPerformance.size());
            
            return UserPerformanceSummary.builder()
                    .categoryPerformance(categoryPerformance)
                    .difficultyPerformance(difficultyPerformance)
                    .build();
                    
        } catch (Exception e) {
            log.error("❌ 사용자 {} 성과 요약 정보 생성 중 오류 발생 - Error: {}", userId, e.getMessage(), e);
            
            // 오류 발생 시 빈 성과 정보 반환
            return UserPerformanceSummary.builder()
                    .categoryPerformance(new HashMap<>())
                    .difficultyPerformance(new HashMap<>())
                    .build();
        }
    }
    
    // 장르 선호 조회는 사용자 요청 추천에서는 외부 입력(selectedGenres)로 대체
}
