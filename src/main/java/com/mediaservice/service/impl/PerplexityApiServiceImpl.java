package com.mediaservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediaservice.model.MediaRecommendation;
import com.mediaservice.service.PerplexityApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PerplexityApiServiceImpl implements PerplexityApiService {

    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${perplexity.api.key}")
    private String apiKey;

    @Value("${perplexity.model.name:sonar}")
    private String modelName;

    @Override
    public List<MediaRecommendation> searchYouTubeVideosForRealTime(String searchPrompt) {
        try {
            log.info("🤖 Perplexity API 호출 시작");

            String url = "https://api.perplexity.ai/chat/completions";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "Bearer " + apiKey);

            // 실시간 추천용 YouTube 영상 검색 프롬프트
            String searchPromptWithInstructions = buildRealTimeSearchPrompt(searchPrompt);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", modelName);
            requestBody.put("messages", List.of(
                Map.of("role", "user", "content", searchPromptWithInstructions)
            ));
            requestBody.put("temperature", 0.3);
            requestBody.put("max_tokens", 3000);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            log.debug("📝 Perplexity API 요청 URL: {}", url);
            log.debug("📝 Perplexity API 요청 본문: {}", objectMapper.writeValueAsString(requestBody));

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

            log.info("📝 Perplexity API 응답: {}", objectMapper.writeValueAsString(response.getBody()));

            if (response.getBody() != null && response.getBody().containsKey("choices")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    if (choice.containsKey("message")) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> message = (Map<String, Object>) choice.get("message");
                        if (message.containsKey("content")) {
                            String responseText = (String) message.get("content");
                            log.debug("📝 Perplexity API 응답 텍스트: {}", responseText);

                            // JSON 파싱 시도 - 더 안전한 접근 방식
                            try {
                                String cleanedJson = responseText;
                                
                                // 1. 마크다운 코드 블록 제거
                                int jsonStart = cleanedJson.indexOf("```json");
                                if (jsonStart >= 0) {
                                    cleanedJson = cleanedJson.substring(jsonStart + 7);
                                }
                                int jsonEnd = cleanedJson.lastIndexOf("```");
                                if (jsonEnd >= 0) {
                                    cleanedJson = cleanedJson.substring(0, jsonEnd);
                                }
                                
                                // 2. 앞뒤 공백 제거
                                cleanedJson = cleanedJson.trim();
                                
                                // 3. JSON 배열 부분 추출 ([...] 부분만)
                                int arrayStart = cleanedJson.indexOf('[');
                                int arrayEnd = cleanedJson.lastIndexOf(']');
                                
                                if (arrayStart >= 0 && arrayEnd >= 0 && arrayEnd > arrayStart) {
                                    cleanedJson = cleanedJson.substring(arrayStart, arrayEnd + 1);
                                    
                                    log.debug("📝 정리된 JSON: {}", cleanedJson.length() > 500 ? 
                                        cleanedJson.substring(0, 500) + "..." : cleanedJson);
                                    
                                    // 4. JSON 파싱
                                    List<MediaRecommendation> recommendations = objectMapper.readValue(cleanedJson, 
                                        objectMapper.getTypeFactory().constructCollectionType(List.class, MediaRecommendation.class));
                                    
                                    if (recommendations != null && !recommendations.isEmpty()) {
                                        log.info("✅ Perplexity API 호출 완료 - 추천 개수: {}", recommendations.size());
                                        return recommendations;
                                    }
                                }
                            } catch (Exception parseException) {
                                log.warn("⚠️ Perplexity API 응답 JSON 파싱 실패: {}", parseException.getMessage());
                                log.debug("⚠️ 파싱 실패한 응답 텍스트 (처음 1000자): {}", 
                                    responseText.length() > 1000 ? responseText.substring(0, 1000) + "..." : responseText);
                            }
                        }
                    }
                }
            }

            log.warn("⚠️ Perplexity API 응답에서 추천 데이터를 파싱할 수 없습니다.");
            return new ArrayList<>();

        } catch (Exception e) {
            log.error("❌ Perplexity API 호출 중 오류 발생 - Error: {}", e.getMessage(), e);
            throw new RuntimeException("Perplexity API 호출 실패", e);
        }
    }

    @Override
    public List<MediaRecommendation> searchMediaForUserRequested(String searchPrompt) {
        try {
            log.info("🤖 Perplexity API 호출 시작 - 사용자 요청용");

            String url = "https://api.perplexity.ai/chat/completions";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "Bearer " + apiKey);

            // 사용자 요청용 다양한 미디어 검색 프롬프트
            String searchPromptWithInstructions = buildUserRequestedSearchPrompt(searchPrompt);
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", modelName);
            requestBody.put("messages", List.of(
                Map.of("role", "user", "content", searchPromptWithInstructions)
            ));
            requestBody.put("temperature", 0.3);
            requestBody.put("max_tokens", 3000);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            log.debug("📝 Perplexity API 요청 URL: {}", url);
            log.debug("📝 Perplexity API 요청 본문: {}", objectMapper.writeValueAsString(requestBody));

            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, request, Map.class);

            log.info("📝 Perplexity API 응답: {}", objectMapper.writeValueAsString(response.getBody()));

            if (response.getBody() != null &&
                response.getBody().containsKey("choices")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> choice = choices.get(0);
                    if (choice.containsKey("message")) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> message = (Map<String, Object>) choice.get("message");
                        if (message.containsKey("content")) {
                            String responseText = (String) message.get("content");
                            log.debug("📝 Perplexity API 응답 텍스트: {}", responseText);

                            // JSON 파싱 시도
                            try {
                                String cleanedJson = responseText;
                                
                                // JSON 배열 부분만 추출
                                int arrayStart = cleanedJson.indexOf('[');
                                int arrayEnd = cleanedJson.lastIndexOf(']');
                                
                                if (arrayStart >= 0 && arrayEnd >= 0 && arrayEnd > arrayStart) {
                                    cleanedJson = cleanedJson.substring(arrayStart, arrayEnd + 1);
                                    
                                    log.debug("📝 정리된 JSON: {}", cleanedJson.length() > 500 ? 
                                        cleanedJson.substring(0, 500) + "..." : cleanedJson);
                                    
                                    // JSON 파싱
                                    List<MediaRecommendation> recommendations = objectMapper.readValue(cleanedJson, 
                                        objectMapper.getTypeFactory().constructCollectionType(List.class, MediaRecommendation.class));
                                    
                                    if (recommendations != null && !recommendations.isEmpty()) {
                                        log.info("✅ Perplexity API 호출 완료 - 추천 개수: {}", recommendations.size());
                                        return recommendations;
                                    }
                                }
                            } catch (Exception parseException) {
                                log.warn("⚠️ Perplexity API 응답 JSON 파싱 실패: {}", parseException.getMessage());
                                log.debug("⚠️ 파싱 실패한 응답 텍스트 (처음 1000자): {}", 
                                    responseText.length() > 1000 ? responseText.substring(0, 1000) + "..." : responseText);
                            }
                        }
                    }
                }
            }

            log.warn("⚠️ Perplexity API 응답에서 추천 데이터를 파싱할 수 없습니다.");
            return new ArrayList<>();

        } catch (Exception e) {
            log.error("❌ Perplexity API 호출 중 오류 발생 - Error: {}", e.getMessage(), e);
            throw new RuntimeException("Perplexity API 호출 실패", e);
        }
    }

    private String buildRealTimeSearchPrompt(String searchPrompt) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("다음 검색어로 영어 학습에 도움되는 YouTube 영상을 찾아주세요: ").append(searchPrompt).append("\n\n");
        
        prompt.append("다음 조건을 만족하는 영상들을 찾아주세요:\n");
        prompt.append("1. 영어 학습 채널의 영상 (BBC Learning English, EnglishClass101, TED-Ed, Speak English With Vanessa 등)\n");
        prompt.append("2. 실제로 존재하고 재생 가능한 영상\n");
        prompt.append("3. 교육적이고 영어 학습에 도움이 되는 내용\n");
        prompt.append("4. 0~3분 사이의 짧은 영상 (실시간 추천용)\n\n");
        
        prompt.append("다음 JSON 형식으로 응답해주세요:\n");
        prompt.append("[\n");
        prompt.append("  {\n");
        prompt.append("    \"title\": \"영상 제목\",\n");
        prompt.append("    \"description\": \"영상 설명 (한국어)\",\n");
        prompt.append("    \"url\": \"https://www.youtube.com/watch?v=VIDEO_ID\",\n");
        prompt.append("    \"mediaType\": \"YOUTUBE_VIDEO\",\n");
        prompt.append("    \"platform\": \"YouTube\",\n");
        prompt.append("    \"difficultyLevel\": \"Beginner|Intermediate|Advanced\",\n");
        prompt.append("    \"recommendationReason\": \"추천 이유\",\n");
        prompt.append("    \"estimatedDuration\": 3\n");
        prompt.append("  }\n");
        prompt.append("]\n\n");
        
        prompt.append("정확히 2개의 짧은 YouTube 영상을 추천해주세요.");
        
        return prompt.toString();
    }

    private String buildUserRequestedSearchPrompt(String searchPrompt) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("다음 검색어로 영어 학습에 도움되는 다양한 미디어 콘텐츠를 찾아주세요: ").append(searchPrompt).append("\n\n");
        
        prompt.append("다음 조건을 만족하는 콘텐츠들을 찾아주세요:\n");
        prompt.append("1. YouTube 영상: 영어 학습 채널의 영상 (BBC Learning English, EnglishClass101, TED-Ed 등)\n");
        prompt.append("2. 영화: Netflix, Disney+, Amazon Prime 등에서 시청 가능한 영어 영화\n");
        prompt.append("3. 드라마: Netflix, HBO Max 등에서 시청 가능한 영어 드라마\n");
        prompt.append("4. 오디오북: Audible 등에서 구매 가능한 영어 오디오북\n");
        prompt.append("5. 실제로 존재하고 접근 가능한 콘텐츠\n");
        prompt.append("6. 교육적이고 영어 학습에 도움이 되는 내용\n");
        prompt.append("7. YouTube 영상은 50분 이하\n\n");
        
        prompt.append("다음 JSON 형식으로 정확히 8개의 콘텐츠를 응답해주세요:\n");
        prompt.append("[\n");
        prompt.append("  {\n");
        prompt.append("    \"title\": \"YouTube 영상 1\",\n");
        prompt.append("    \"description\": \"YouTube 영상 설명\",\n");
        prompt.append("    \"url\": \"https://www.youtube.com/watch?v=VIDEO_ID\",\n");
        prompt.append("    \"mediaType\": \"YOUTUBE_VIDEO\",\n");
        prompt.append("    \"platform\": \"YouTube\",\n");
        prompt.append("    \"difficultyLevel\": \"Not required for user-requested recommendations\",\n");
        prompt.append("    \"recommendationReason\": \"추천 이유\",\n");
        prompt.append("    \"estimatedDuration\": 30\n");
        prompt.append("  },\n");
        prompt.append("  {\n");
        prompt.append("    \"title\": \"YouTube 영상 2\",\n");
        prompt.append("    \"description\": \"YouTube 영상 설명\",\n");
        prompt.append("    \"url\": \"https://www.youtube.com/watch?v=VIDEO_ID\",\n");
        prompt.append("    \"mediaType\": \"YOUTUBE_VIDEO\",\n");
        prompt.append("    \"platform\": \"YouTube\",\n");
        prompt.append("    \"difficultyLevel\": \"Not required for user-requested recommendations\",\n");
        prompt.append("    \"recommendationReason\": \"추천 이유\",\n");
        prompt.append("    \"estimatedDuration\": 30\n");
        prompt.append("  },\n");
        prompt.append("  {\n");
        prompt.append("    \"title\": \"영화 1\",\n");
        prompt.append("    \"description\": \"영화 설명\",\n");
        prompt.append("    \"url\": \"N/A\",\n");
        prompt.append("    \"mediaType\": \"MOVIE\",\n");
        prompt.append("    \"platform\": \"Netflix\",\n");
        prompt.append("    \"difficultyLevel\": \"Not required for user-requested recommendations\",\n");
        prompt.append("    \"recommendationReason\": \"추천 이유\",\n");
        prompt.append("    \"estimatedDuration\": 120\n");
        prompt.append("  },\n");
        prompt.append("  {\n");
        prompt.append("    \"title\": \"영화 2\",\n");
        prompt.append("    \"description\": \"영화 설명\",\n");
        prompt.append("    \"url\": \"N/A\",\n");
        prompt.append("    \"mediaType\": \"MOVIE\",\n");
        prompt.append("    \"platform\": \"Disney+\",\n");
        prompt.append("    \"difficultyLevel\": \"Not required for user-requested recommendations\",\n");
        prompt.append("    \"recommendationReason\": \"추천 이유\",\n");
        prompt.append("    \"estimatedDuration\": 120\n");
        prompt.append("  },\n");
        prompt.append("  {\n");
        prompt.append("    \"title\": \"드라마 1\",\n");
        prompt.append("    \"description\": \"드라마 설명\",\n");
        prompt.append("    \"url\": \"N/A\",\n");
        prompt.append("    \"mediaType\": \"DRAMA\",\n");
        prompt.append("    \"platform\": \"HBO Max\",\n");
        prompt.append("    \"difficultyLevel\": \"Not required for user-requested recommendations\",\n");
        prompt.append("    \"recommendationReason\": \"추천 이유\",\n");
        prompt.append("    \"estimatedDuration\": 45\n");
        prompt.append("  },\n");
        prompt.append("  {\n");
        prompt.append("    \"title\": \"드라마 2\",\n");
        prompt.append("    \"description\": \"드라마 설명\",\n");
        prompt.append("    \"url\": \"N/A\",\n");
        prompt.append("    \"mediaType\": \"DRAMA\",\n");
        prompt.append("    \"platform\": \"Netflix\",\n");
        prompt.append("    \"difficultyLevel\": \"Not required for user-requested recommendations\",\n");
        prompt.append("    \"recommendationReason\": \"추천 이유\",\n");
        prompt.append("    \"estimatedDuration\": 45\n");
        prompt.append("  },\n");
        prompt.append("  {\n");
        prompt.append("    \"title\": \"오디오북 1\",\n");
        prompt.append("    \"description\": \"오디오북 설명\",\n");
        prompt.append("    \"url\": \"N/A\",\n");
        prompt.append("    \"mediaType\": \"AUDIOBOOK\",\n");
        prompt.append("    \"platform\": \"Audible\",\n");
        prompt.append("    \"difficultyLevel\": \"Not required for user-requested recommendations\",\n");
        prompt.append("    \"recommendationReason\": \"추천 이유\",\n");
        prompt.append("    \"estimatedDuration\": 600\n");
        prompt.append("  },\n");
        prompt.append("  {\n");
        prompt.append("    \"title\": \"오디오북 2\",\n");
        prompt.append("    \"description\": \"오디오북 설명\",\n");
        prompt.append("    \"url\": \"N/A\",\n");
        prompt.append("    \"mediaType\": \"AUDIOBOOK\",\n");
        prompt.append("    \"platform\": \"Audible\",\n");
        prompt.append("    \"difficultyLevel\": \"Not required for user-requested recommendations\",\n");
        prompt.append("    \"recommendationReason\": \"추천 이유\",\n");
        prompt.append("    \"estimatedDuration\": 600\n");
        prompt.append("  }\n");
        prompt.append("]\n\n");
        
        prompt.append("정확히 8개의 콘텐츠를 추천해주세요:\n");
        prompt.append("- YouTube 영상 2개\n");
        prompt.append("- 영화 2개\n");
        prompt.append("- 드라마 2개\n");
        prompt.append("- 오디오북 2개\n\n");
        prompt.append("총 8개를 JSON 배열로 응답해주세요.");
        
        return prompt.toString();
    }
}
