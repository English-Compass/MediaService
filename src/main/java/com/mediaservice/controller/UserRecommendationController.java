package com.mediaservice.controller;

import com.mediaservice.dto.UserRecommendationRequest;
import com.mediaservice.dto.UserRecommendationResponse;
import com.mediaservice.enums.RecommendationType;
import com.mediaservice.model.MediaRecommendation;
import com.mediaservice.repository.MediaRecommendationRepository;
import com.mediaservice.service.UserRequestedRecommendationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * 사용자 요청 기반 미디어 추천 Controller
 * 
 * 사용자가 미디어 추천 페이지에서 장르를 선택하여
 * 맞춤형 추천을 요청할 때 사용됩니다.
 */
@Slf4j
@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // CORS 설정 (개발 환경용)
public class UserRecommendationController {
    
    private final UserRequestedRecommendationService userRequestedRecommendationService;
    private final MediaRecommendationRepository mediaRecommendationRepository;
    
    /**
     * 사용자 요청 기반 미디어 추천 생성
     * 
     * POST /api/recommendations/user-requested
     * 
     * @param request 사용자 추천 요청 (사용자 ID + 선택된 장르)
     * @return 추천 결과 응답
     */
    @PostMapping("/user-requested")
    public ResponseEntity<UserRecommendationResponse> generateUserRequestedRecommendations(
            @Valid @RequestBody UserRecommendationRequest request) {
        
        try {
            log.info("🎯 사용자 요청 기반 추천 API 호출 - UserId: {}, 선택된 장르: {}", 
                    request.getUserId(), request.getSelectedGenres());
            
            // 사용자 요청 기반 추천 생성
            List<MediaRecommendation> recommendations = userRequestedRecommendationService
                    .generateUserRequestedRecommendations(request.getUserId(), request.getSelectedGenres());
            
            log.info("✅ 사용자 요청 기반 추천 생성 완료 - 총 추천 개수: {}", recommendations.size());
            
            // 성공 응답 반환
            UserRecommendationResponse response = UserRecommendationResponse.success(
                    recommendations.size(),
                    request.getSelectedGenres(),
                    recommendations
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ 사용자 요청 기반 추천 생성 중 오류 발생 - UserId: {}, Error: {}", 
                    request.getUserId(), e.getMessage(), e);
            
            // 오류 응답 반환
            UserRecommendationResponse errorResponse = UserRecommendationResponse.error(
                    "추천 생성 중 오류가 발생했습니다: " + e.getMessage()
            );
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * 사용자 추천 히스토리 조회
     * 
     * GET /api/recommendations/user-requested/{userId}
     * 
     * @param userId 사용자 ID
     * @return 해당 사용자의 사용자 요청 기반 추천 히스토리
     */
    @GetMapping("/user-requested/{userId}")
    public ResponseEntity<UserRecommendationResponse> getUserRequestedRecommendationHistory(
            @PathVariable String userId) {
        
        try {
            log.info("📚 사용자 {} 사용자 요청 기반 추천 히스토리 조회", userId);
            
            // 사용자 요청 기반 추천 히스토리 조회
            List<MediaRecommendation> recommendations = mediaRecommendationRepository
                    .findByUserIdAndRecommendationTypeOrderByGeneratedAtDesc(userId, RecommendationType.USER_REQUESTED);
            
            log.info("✅ 사용자 {} 추천 히스토리 조회 완료 - 총 추천 개수: {}", userId, recommendations.size());
            
            UserRecommendationResponse response = UserRecommendationResponse.builder()
                    .status("SUCCESS")
                    .message("사용자 요청 기반 추천 히스토리 조회 완료")
                    .totalRecommendations(recommendations.size())
                    .selectedGenres(null) // 히스토리에서는 선택된 장르 정보가 없음
                    .generatedAt(java.time.LocalDateTime.now())
                    .recommendations(recommendations)
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ 사용자 요청 기반 추천 히스토리 조회 중 오류 발생 - UserId: {}, Error: {}", 
                    userId, e.getMessage(), e);
            
            UserRecommendationResponse errorResponse = UserRecommendationResponse.error(
                    "추천 히스토리 조회 중 오류가 발생했습니다: " + e.getMessage()
            );
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * 사용자 전체 추천 히스토리 조회
     * 
     * GET /api/recommendations/history/{userId}
     * 
     * @param userId 사용자 ID
     * @return 해당 사용자의 모든 추천 히스토리 (실시간 + 사용자 요청)
     */
    @GetMapping("/history/{userId}")
    public ResponseEntity<UserRecommendationResponse> getAllRecommendationHistory(
            @PathVariable String userId) {
        
        try {
            log.info("📚 사용자 {} 전체 추천 히스토리 조회", userId);
            
            // 모든 추천 히스토리 조회 (실시간 + 사용자 요청)
            List<MediaRecommendation> recommendations = mediaRecommendationRepository
                    .findByUserIdOrderByGeneratedAtDesc(userId);
            
            log.info("✅ 사용자 {} 전체 추천 히스토리 조회 완료 - 총 추천 개수: {}", userId, recommendations.size());
            
            UserRecommendationResponse response = UserRecommendationResponse.builder()
                    .status("SUCCESS")
                    .message("전체 추천 히스토리 조회 완료")
                    .totalRecommendations(recommendations.size())
                    .selectedGenres(null) // 히스토리에서는 선택된 장르 정보가 없음
                    .generatedAt(java.time.LocalDateTime.now())
                    .recommendations(recommendations)
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("❌ 전체 추천 히스토리 조회 중 오류 발생 - UserId: {}, Error: {}", 
                    userId, e.getMessage(), e);
            
            UserRecommendationResponse errorResponse = UserRecommendationResponse.error(
                    "추천 히스토리 조회 중 오류가 발생했습니다: " + e.getMessage()
            );
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * 사용 가능한 장르 목록 조회
     * 
     * GET /api/recommendations/genres
     * 
     * @return 선택 가능한 장르 목록
     */
    @GetMapping("/genres")
    public ResponseEntity<Object> getAvailableGenres() {
        
        try {
            log.info("🎭 사용 가능한 장르 목록 조회");
            
            // 선택 가능한 장르 목록
            var availableGenres = new Object() {
                public final String[] genres = {
                    "액션", "드라마", "코미디", "로맨스", "스릴러", 
                    "공포", "미스터리", "SF", "판타지", "범죄", 
                    "전쟁", "음악", "애니메이션", "다큐멘터리"
                };
                public final String message = "사용 가능한 장르 목록입니다.";
                public final int totalCount = genres.length;
            };
            
            return ResponseEntity.ok(availableGenres);
            
        } catch (Exception e) {
            log.error("❌ 사용 가능한 장르 목록 조회 중 오류 발생 - Error: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("장르 목록 조회 중 오류가 발생했습니다.");
        }
    }
}

