package com.mediaservice.service;

import com.mediaservice.dto.UserPerformanceSummary;
import com.mediaservice.service.impl.PromptTemplateServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 프롬프트 템플릿 서비스 테스트
 */
@ExtendWith(MockitoExtension.class)
class PromptTemplateServiceTest {

    @InjectMocks
    private PromptTemplateServiceImpl promptTemplateService;

    private UserPerformanceSummary testUserPerformance;
    private List<String> testSelectedGenres;

    @BeforeEach
    void setUp() {
        // 카테고리별 성과 테스트 데이터
        Map<String, Double> categoryPerformance = new HashMap<>();
        categoryPerformance.put("여행-가족", 80.0);
        categoryPerformance.put("여행-친구", 75.0);
        categoryPerformance.put("비즈니스-회의", 65.0);
        categoryPerformance.put("일상-음식", 85.0);

        // 난이도별 성과 테스트 데이터
        Map<Integer, Double> difficultyPerformance = new HashMap<>();
        difficultyPerformance.put(1, 90.0); // 초급
        difficultyPerformance.put(2, 75.0); // 중급
        difficultyPerformance.put(3, 45.0); // 고급

        testUserPerformance = UserPerformanceSummary.builder()
                .categoryPerformance(categoryPerformance)
                .difficultyPerformance(difficultyPerformance)
                .build();

        testSelectedGenres = List.of("액션", "스릴러", "SF", "판타지");
    }

    @Test
    void 사용자_요청_기반_프롬프트_생성_성공() {
        // When
        String prompt = promptTemplateService.generateUserRequestedPrompt(testUserPerformance, testSelectedGenres);

        // Then
        assertThat(prompt).isNotNull();
        assertThat(prompt).isNotEmpty();

        // 프롬프트 내용 검증
        assertThat(prompt).contains("# 🎯 User Request-Based Media Recommendation Prompt");
        assertThat(prompt).contains("## 📋 Recommendation Goal");
        assertThat(prompt).contains("## 🎬 **English Content** Recommendation Requirements");
        assertThat(prompt).contains("## 📊 User Performance Analysis");
        assertThat(prompt).contains("## 🏷️ Category-wise Performance Analysis");
        assertThat(prompt).contains("## 📈 Difficulty-wise Performance Analysis");
        assertThat(prompt).contains("## 🎯 **English Learning Focused** Recommendation Strategy");
        assertThat(prompt).contains("## 🤖 **English Learning Focused** AI Analysis Request");
        assertThat(prompt).contains("## 📝 Output Format");

        // 선택된 장르 검증
        assertThat(prompt).contains("액션, 스릴러, SF, 판타지");

        // 카테고리별 성과 검증
        assertThat(prompt).contains("여행-가족");
        assertThat(prompt).contains("80.0%");
        assertThat(prompt).contains("🟢 우수");
        assertThat(prompt).contains("비즈니스-회의");
        assertThat(prompt).contains("65.0%");
        assertThat(prompt).contains("🔴 보완 필요");

        // 난이도별 성과 검증
        assertThat(prompt).contains("초급 (Lv.1)");
        assertThat(prompt).contains("90.0%");
        assertThat(prompt).contains("고급 (Lv.3)");
        assertThat(prompt).contains("45.0%");

        // 추천 전략 검증
        assertThat(prompt).contains("성과가 낮은 카테고리 우선");
        assertThat(prompt).contains("난이도 단계별 향상");
        assertThat(prompt).contains("선택된 장르 반영");
        assertThat(prompt).contains("다양한 미디어 타입");

        // 출력 형식 검증
        assertThat(prompt).contains("JSON 형태로");
        assertThat(prompt).contains("title:");
        assertThat(prompt).contains("description:");
        assertThat(prompt).contains("url:");
        assertThat(prompt).contains("mediaType:");
        assertThat(prompt).contains("platform:");
        assertThat(prompt).contains("difficultyLevel:");
        assertThat(prompt).contains("recommendationReason:");
        assertThat(prompt).contains("estimatedDuration:");
        assertThat(prompt).contains("총 5-8개의 다양한 콘텐츠를 추천해주세요");
    }

    @Test
    void 사용자_요청_기반_프롬프트_생성_빈_성과_데이터() {
        // Given
        UserPerformanceSummary emptyPerformance = UserPerformanceSummary.builder()
                .categoryPerformance(new HashMap<>())
                .difficultyPerformance(new HashMap<>())
                .build();

        // When
        String prompt = promptTemplateService.generateUserRequestedPrompt(emptyPerformance, testSelectedGenres);

        // Then
        assertThat(prompt).isNotNull();
        assertThat(prompt).isNotEmpty();

        // 기본 구조는 포함되어야 함
        assertThat(prompt).contains("# 🎯 사용자 요청 기반 미디어 추천 프롬프트");
        assertThat(prompt).contains("액션, 스릴러, SF, 판타지");

        // 성과 데이터가 없어도 프롬프트는 생성되어야 함
        assertThat(prompt).contains("## 🏷️ 카테고리별 성과 분석");
        assertThat(prompt).contains("## 📈 난이도별 성과 분석");
    }

    @Test
    void 사용자_요청_기반_프롬프트_생성_단일_장르() {
        // Given
        List<String> singleGenre = List.of("액션");

        // When
        String prompt = promptTemplateService.generateUserRequestedPrompt(testUserPerformance, singleGenre);

        // Then
        assertThat(prompt).isNotNull();
        assertThat(prompt).contains("액션");
        assertThat(prompt).doesNotContain("액션, 스릴러, SF, 판타지");
    }

    @Test
    void 사용자_요청_기반_프롬프트_생성_성과_상태_표시() {
        // When
        String prompt = promptTemplateService.generateUserRequestedPrompt(testUserPerformance, testSelectedGenres);

        // Then
        // 성과 상태 이모지 검증
        assertThat(prompt).contains("🟢 우수"); // 80% 이상
        assertThat(prompt).contains("🟡 보통"); // 60-79%
        assertThat(prompt).contains("🔴 보완 필요"); // 60% 미만

        // 구체적인 성과 값 검증
        assertThat(prompt).contains("80.0%");
        assertThat(prompt).contains("75.0%");
        assertThat(prompt).contains("65.0%");
        assertThat(prompt).contains("85.0%");
        assertThat(prompt).contains("90.0%");
        assertThat(prompt).contains("45.0%");
    }
}
