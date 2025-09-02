package com.mediaservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediaservice.dto.UserRecommendationRequest;
import com.mediaservice.dto.UserRecommendationResponse;
import com.mediaservice.service.UserRequestedRecommendationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 사용자 요청 기반 미디어 추천 Controller 테스트
 */
@WebMvcTest(UserRecommendationController.class)
class UserRecommendationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRequestedRecommendationService userRequestedRecommendationService;

    @Test
    void 사용자_요청_기반_추천_생성_성공() throws Exception {
        // Given
        UserRecommendationRequest request = UserRecommendationRequest.builder()
                .userId(1L)
                .selectedGenres(List.of("액션", "스릴러", "SF"))
                .build();

        when(userRequestedRecommendationService.generateUserRequestedRecommendations(any(Long.class), anyList()))
                .thenReturn(5);

        // When & Then
        mockMvc.perform(post("/api/recommendations/user-requested")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.totalRecommendations").value(5))
                .andExpect(jsonPath("$.selectedGenres").isArray())
                .andExpect(jsonPath("$.selectedGenres[0]").value("액션"))
                .andExpect(jsonPath("$.selectedGenres[1]").value("스릴러"))
                .andExpect(jsonPath("$.selectedGenres[2]").value("SF"));
    }

    @Test
    void 사용자_요청_기반_추천_생성_유효성_검증_실패() throws Exception {
        // Given - 빈 장르 목록으로 유효성 검증 실패
        UserRecommendationRequest request = UserRecommendationRequest.builder()
                .userId(1L)
                .selectedGenres(List.of()) // 빈 목록
                .build();

        // When & Then
        mockMvc.perform(post("/api/recommendations/user-requested")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void 사용자_요청_기반_추천_생성_서비스_오류() throws Exception {
        // Given
        UserRecommendationRequest request = UserRecommendationRequest.builder()
                .userId(1L)
                .selectedGenres(List.of("액션", "드라마"))
                .build();

        when(userRequestedRecommendationService.generateUserRequestedRecommendations(any(Long.class), anyList()))
                .thenThrow(new RuntimeException("추천 생성 실패"));

        // When & Then
        mockMvc.perform(post("/api/recommendations/user-requested")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("ERROR"))
                .andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("추천 생성 중 오류가 발생했습니다")));
    }

    @Test
    void 사용_가능한_장르_목록_조회() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/recommendations/genres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.genres").isArray())
                .andExpect(jsonPath("$.totalCount").value(14))
                .andExpect(jsonPath("$.genres[0]").value("액션"))
                .andExpect(jsonPath("$.genres[1]").value("드라마"))
                .andExpect(jsonPath("$.genres[2]").value("코미디"));
    }

    @Test
    void 사용자_추천_히스토리_조회() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/recommendations/user-requested/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"))
                .andExpect(jsonPath("$.message").value("사용자 요청 기반 추천 히스토리 조회 완료"));
    }
}
