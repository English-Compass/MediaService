package com.mediaservice.service;

import com.mediaservice.dto.CategoryPerformanceView;
import com.mediaservice.dto.DifficultyAchievementView;
import com.mediaservice.dto.UserPerformanceSummary;
import com.mediaservice.model.MediaRecommendation;
import com.mediaservice.repository.CategoryPerformanceRepository;
import com.mediaservice.repository.DifficultyAchievementRepository;
import com.mediaservice.repository.MediaRecommendationRepository;
import com.mediaservice.service.impl.UserRequestedRecommendationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

/**
 * 사용자 요청 기반 미디어 추천 서비스 테스트
 */
@ExtendWith(MockitoExtension.class)
class UserRequestedRecommendationServiceTest {

    @Mock
    private PromptTemplateService promptTemplateService;

    @Mock
    private GeminiApiService geminiApiService;

    @Mock
    private MediaRecommendationRepository mediaRecommendationRepository;

    @Mock
    private CategoryPerformanceRepository categoryPerformanceRepository;

    @Mock
    private DifficultyAchievementRepository difficultyAchievementRepository;

    @InjectMocks
    private UserRequestedRecommendationServiceImpl userRequestedRecommendationService;

    private Long testUserId;
    private List<String> testSelectedGenres;
    private List<CategoryPerformanceView> testCategoryPerformances;
    private List<DifficultyAchievementView> testDifficultyAchievements;
    private List<MediaRecommendation> testRecommendations;

    @BeforeEach
    void setUp() {
        testUserId = 1L;
        testSelectedGenres = List.of("액션", "스릴러", "SF");

        // 카테고리별 성과 Mock 데이터
        testCategoryPerformances = List.of(
                CategoryPerformanceView.builder()
                        .userId(testUserId)
                        .majorCategory("여행")
                        .minorCategory("가족")
                        .categoryProficiency(80.0)
                        .build(),
                CategoryPerformanceView.builder()
                        .userId(testUserId)
                        .majorCategory("비즈니스")
                        .minorCategory("회의")
                        .categoryProficiency(65.0)
                        .build()
        );

        // 난이도별 성과 Mock 데이터
        testDifficultyAchievements = List.of(
                DifficultyAchievementView.builder()
                        .userId(testUserId)
                        .difficultyLevel(1)
                        .difficultyAchievementRate(90.0)
                        .build(),
                DifficultyAchievementView.builder()
                        .userId(testUserId)
                        .difficultyLevel(2)
                        .difficultyAchievementRate(75.0)
                        .build(),
                DifficultyAchievementView.builder()
                        .userId(testUserId)
                        .difficultyLevel(3)
                        .difficultyAchievementRate(45.0)
                        .build()
        );

        // 추천 결과 Mock 데이터
        testRecommendations = List.of(
                MediaRecommendation.builder()
                        .title("액션 영화 추천")
                        .description("영어 학습용 액션 영화")
                        .url("https://example.com/action-movie")
                        .mediaType(com.mediaservice.enums.MediaType.VIDEO)
                        .platform("Netflix")
                        .difficultyLevel("중급")
                        .recommendationReason("비즈니스-회의 카테고리 성과 향상을 위한 추천")
                        .estimatedDuration(120)
                        .build(),
                MediaRecommendation.builder()
                        .title("스릴러 유튜브 영상")
                        .description("영어 학습용 스릴러 콘텐츠")
                        .url("https://youtube.com/watch?v=example")
                        .mediaType(com.mediaservice.enums.MediaType.VIDEO)
                        .platform("YouTube")
                        .difficultyLevel("초급")
                        .recommendationReason("고급 난이도 성과 향상을 위한 기초 콘텐츠")
                        .estimatedDuration(15)
                        .build()
        );
    }

    @Test
    void 사용자_요청_기반_추천_생성_성공() {
        // Given
        when(categoryPerformanceRepository.findByUserId(testUserId))
                .thenReturn(testCategoryPerformances);
        when(difficultyAchievementRepository.findByUserId(testUserId))
                .thenReturn(testDifficultyAchievements);
        when(promptTemplateService.generateUserRequestedPrompt(any(UserPerformanceSummary.class), anyList()))
                .thenReturn("테스트 프롬프트");
        when(geminiApiService.generateRecommendations(any(String.class)))
                .thenReturn(testRecommendations);
        when(mediaRecommendationRepository.saveAll(anyList()))
                .thenReturn(testRecommendations);

        // When
        int result = userRequestedRecommendationService.generateUserRequestedRecommendations(testUserId, testSelectedGenres);

        // Then
        assertThat(result).isEqualTo(2);

        // 검증: Repository 호출 확인
        verify(categoryPerformanceRepository).findByUserId(testUserId);
        verify(difficultyAchievementRepository).findByUserId(testUserId);
        verify(promptTemplateService).generateUserRequestedPrompt(any(UserPerformanceSummary.class), eq(testSelectedGenres));
        verify(geminiApiService).generateRecommendations(any(String.class));
        verify(mediaRecommendationRepository).saveAll(anyList());
    }

    @Test
    void 사용자_요청_기반_추천_생성_데이터베이스_오류() {
        // Given
        when(categoryPerformanceRepository.findByUserId(testUserId))
                .thenThrow(new RuntimeException("데이터베이스 연결 오류"));

        // When & Then
        try {
            userRequestedRecommendationService.generateUserRequestedRecommendations(testUserId, testSelectedGenres);
        } catch (RuntimeException e) {
            assertThat(e.getMessage()).contains("사용자 " + testUserId + " 사용자 요청 추천 생성 실패");
        }

        // 검증: Repository 호출 확인
        verify(categoryPerformanceRepository).findByUserId(testUserId);
    }

    @Test
    void 사용자_요청_기반_추천_생성_빈_성과_데이터() {
        // Given
        when(categoryPerformanceRepository.findByUserId(testUserId))
                .thenReturn(List.of());
        when(difficultyAchievementRepository.findByUserId(testUserId))
                .thenReturn(List.of());
        when(promptTemplateService.generateUserRequestedPrompt(any(UserPerformanceSummary.class), anyList()))
                .thenReturn("테스트 프롬프트");
        when(geminiApiService.generateRecommendations(any(String.class)))
                .thenReturn(testRecommendations);
        when(mediaRecommendationRepository.saveAll(anyList()))
                .thenReturn(testRecommendations);

        // When
        int result = userRequestedRecommendationService.generateUserRequestedRecommendations(testUserId, testSelectedGenres);

        // Then
        assertThat(result).isEqualTo(2);

        // 검증: 빈 성과 데이터로도 정상 동작
        verify(promptTemplateService).generateUserRequestedPrompt(argThat(summary -> 
            summary.getCategoryPerformance().isEmpty() && 
            summary.getDifficultyPerformance().isEmpty()
        ), eq(testSelectedGenres));
    }
}
