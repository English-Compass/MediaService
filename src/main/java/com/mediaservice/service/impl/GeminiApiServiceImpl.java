package com.mediaservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediaservice.event.LearningCompletedEvent;
import com.mediaservice.model.UserPerformanceSummary;
import com.mediaservice.service.GeminiApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiApiServiceImpl implements GeminiApiService {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model.name:gemini-1.5-flash}")
    private String modelName;

    @Override
    public String generateSearchPromptForRealTime(LearningCompletedEvent event) {
        try {
            log.info("🤖 Gemini API 호출 시작 - 실시간 추천용 검색 프롬프트 생성");

            String url = "https://generativelanguage.googleapis.com/v1beta/models/" + modelName + ":generateContent?key=" + apiKey;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            // 학습 세션 분석을 위한 프롬프트
            String analysisPrompt = buildRealTimeAnalysisPrompt(event);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", List.of(
                Map.of("parts", List.of(
                    Map.of("text", analysisPrompt)
                ))
            ));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

            if (response.getBody() != null && response.getBody().containsKey("candidates")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> candidate = candidates.get(0);
                    if (candidate.containsKey("content")) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> content = (Map<String, Object>) candidate.get("content");
                        if (content.containsKey("parts")) {
                            @SuppressWarnings("unchecked")
                            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                            if (!parts.isEmpty()) {
                                String searchPrompt = (String) parts.get(0).get("text");
                                log.info("✅ Gemini API 호출 완료 - 검색 프롬프트 생성 성공");
                                return searchPrompt;
                            }
                        }
                    }
                }
            }

            log.warn("⚠️ Gemini API 응답에서 검색 프롬프트를 파싱할 수 없습니다.");
            return "영어 학습에 도움되는 유튜브 영상 추천";

        } catch (Exception e) {
            log.error("❌ Gemini API 호출 실패", e);
            return "영어 학습에 도움되는 유튜브 영상 추천";
        }
    }

    @Override
    public String generateSearchPromptForUserRequested(UserPerformanceSummary userPerformance, List<String> selectedGenres) {
        try {
            log.info("🤖 Gemini API 호출 시작 - 사용자 요청용 검색 프롬프트 생성");

            String url = "https://generativelanguage.googleapis.com/v1beta/models/" + modelName + ":generateContent?key=" + apiKey;

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            // 사용자 성과 분석을 위한 프롬프트
            String analysisPrompt = buildUserRequestedAnalysisPrompt(userPerformance, selectedGenres);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", List.of(
                Map.of("parts", List.of(
                    Map.of("text", analysisPrompt)
                ))
            ));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

            if (response.getBody() != null && response.getBody().containsKey("candidates")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> candidates = (List<Map<String, Object>>) response.getBody().get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String, Object> candidate = candidates.get(0);
                    if (candidate.containsKey("content")) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> content = (Map<String, Object>) candidate.get("content");
                        if (content.containsKey("parts")) {
                            @SuppressWarnings("unchecked")
                            List<Map<String, Object>> parts = (List<Map<String, Object>>) content.get("parts");
                            if (!parts.isEmpty()) {
                                String searchPrompt = (String) parts.get(0).get("text");
                                log.info("✅ Gemini API 호출 완료 - 검색 프롬프트 생성 성공");
                                return searchPrompt;
                            }
                        }
                    }
                }
            }

            log.warn("⚠️ Gemini API 응답에서 검색 프롬프트를 파싱할 수 없습니다.");
            return "영어 학습에 도움되는 유튜브 영상 추천";

        } catch (Exception e) {
            log.error("❌ Gemini API 호출 실패", e);
            return "영어 학습에 도움되는 유튜브 영상 추천";
        }
    }

    private String buildRealTimeAnalysisPrompt(LearningCompletedEvent event) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("학습 세션 결과를 분석하여 Perplexity에게 보낼 YouTube 영상 검색 프롬프트를 작성해주세요.\n\n");
        
        prompt.append("## 학습 세션 정보\n");
        prompt.append("- 사용자 ID: ").append(event.getUserId()).append("\n");
        prompt.append("- 세션 ID: ").append(event.getSessionId()).append("\n");
        prompt.append("- 총 문제 수: ").append(event.getTotalQuestions()).append("\n");
        prompt.append("- 정답 수: ").append(event.getCorrectAnswers()).append("\n");
        prompt.append("- 정답률: ").append(event.getAccuracyRate()).append("%\n");
        prompt.append("- 총 학습 시간: ").append(event.getTotalLearningTimeMinutes()).append("분\n\n");
        
        if (event.getSessionQuestions() != null && !event.getSessionQuestions().isEmpty()) {
            prompt.append("## 틀린 문제 분석\n");
            for (int i = 0; i < Math.min(event.getSessionQuestions().size(), 3); i++) {
                var question = event.getSessionQuestions().get(i);
                prompt.append("- 문제: ").append(question.getQuestionText()).append("\n");
                prompt.append("- 사용자 답: ").append(question.getUserAnswer()).append("\n");
                prompt.append("- 정답: ").append(question.getCorrectAnswer()).append("\n");
                prompt.append("- 대분류: ").append(question.getMajorCategory()).append("\n");
                prompt.append("- 소분류: ").append(question.getMinorCategory()).append("\n");
                prompt.append("- 난이도: ").append(question.getDifficultyLevel()).append("\n\n");
            }
        }
        
        prompt.append("## 요청사항\n");
        prompt.append("위 학습 세션 결과를 바탕으로, 사용자의 약점을 보완할 수 있는 영어 학습 YouTube 영상을 찾기 위한 검색 프롬프트를 작성해주세요.\n");
        prompt.append("프롬프트는 간단하고 명확해야 하며, Perplexity가 실제로 검색할 수 있는 키워드를 포함해야 합니다.\n");
        prompt.append("예시: '영어 문법 기초 학습 유튜브 영상', '영어 어휘 확장 유튜브 영상' 등\n\n");
        prompt.append("검색 프롬프트만 답변해주세요:");
        
        return prompt.toString();
    }

    private String buildUserRequestedAnalysisPrompt(UserPerformanceSummary userPerformance, List<String> selectedGenres) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("사용자 성과를 분석하여 Perplexity에게 보낼 YouTube 영상 검색 프롬프트를 작성해주세요.\n\n");
        
        prompt.append("## 사용자 성과 정보\n");
        prompt.append("- 사용자 ID: ").append(userPerformance.getUserId()).append("\n");
        prompt.append("- 선택된 장르: ").append(String.join(", ", selectedGenres)).append("\n");
        prompt.append("- 전체 정답률: ").append(userPerformance.getOverallAccuracy()).append("%\n");
        prompt.append("- 총 학습 시간: ").append(userPerformance.getTotalStudyTime()).append("분\n\n");
        
        if (userPerformance.getCategoryPerformance() != null && !userPerformance.getCategoryPerformance().isEmpty()) {
            prompt.append("## 카테고리별 성과\n");
            for (Map.Entry<String, Double> entry : userPerformance.getCategoryPerformance().entrySet()) {
                String category = entry.getKey();
                Double performance = entry.getValue();
                String status = performance >= 80 ? "우수" : performance >= 60 ? "보통" : "개선 필요";
                prompt.append("- ").append(category).append(": ").append(performance).append("% (").append(status).append(")\n");
            }
            prompt.append("\n");
        }
        
        prompt.append("## 요청사항\n");
        prompt.append("위 사용자 성과를 바탕으로, 선택된 장르에 맞는 영어 학습 YouTube 영상을 찾기 위한 검색 프롬프트를 작성해주세요.\n");
        prompt.append("프롬프트는 간단하고 명확해야 하며, Perplexity가 실제로 검색할 수 있는 키워드를 포함해야 합니다.\n");
        prompt.append("예시: '영어 액션 영화 학습 유튜브 영상', '영어 코미디 드라마 학습 유튜브 영상' 등\n\n");
        prompt.append("검색 프롬프트만 답변해주세요:");
        
        return prompt.toString();
    }
}
