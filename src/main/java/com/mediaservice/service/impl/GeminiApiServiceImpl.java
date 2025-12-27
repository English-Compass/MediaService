package com.mediaservice.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mediaservice.enums.MediaType;
import com.mediaservice.model.MediaRecommendation;
import com.mediaservice.service.GeminiApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiApiServiceImpl implements GeminiApiService {

    private final ObjectMapper objectMapper;

    @Override
    public List<MediaRecommendation> generateRecommendations(String prompt) {
        try {
            log.info("🤖 Mock Gemini API 호출 시작");
            
            // 프롬프트 유형 판단
            boolean isRealTimeRecommendation = prompt.contains("실시간 세션 기반 추천") || 
                                            prompt.contains("짧은 유튜브 동영상 위주");
            
            // Mock 응답 생성 (실시간/주기적 추천 구분)
            String mockResponse = generateMockResponse(prompt, isRealTimeRecommendation);
            log.debug("📝 Mock Gemini API 응답:\n{}", mockResponse);
            
            // JSON 응답 파싱
            List<MediaRecommendation> recommendations = parseMockResponse(mockResponse);
            log.info("✅ Mock Gemini API 호출 완료 - 추천 개수: {}, 유형: {}", 
                    recommendations.size(), 
                    isRealTimeRecommendation ? "실시간" : "주기적");
            
            return recommendations;
            
        } catch (Exception e) {
            log.error("❌ Mock Gemini API 호출 중 오류 발생 - Error: {}", e.getMessage(), e);
            throw new RuntimeException("Mock Gemini API 호출 실패", e);
        }
    }
    
    /**
     * Mock 응답을 생성합니다. (실시간/주기적 추천 구분)
     */
    private String generateMockResponse(String prompt, boolean isRealTimeRecommendation) {
        if (isRealTimeRecommendation) {
            return generateRealTimeMockResponse(prompt);
        } else {
            return generatePeriodicMockResponse(prompt);
        }
    }
    
    /**
     * 실시간 추천용 Mock 응답 생성 (짧은 유튜브 동영상 위주)
     */
    private String generateRealTimeMockResponse(String prompt) {
        // 프롬프트에서 학습 주제 추출
        String topic = extractTopicFromPrompt(prompt);
        
        return String.format("""
            ```json
            {
              "recommendations": [
                {
                  "title": "Quick English Grammar Fix - %s in 5 Minutes",
                  "description": "Fast-paced grammar lesson focusing on common mistakes in %s topics",
                  "url": "https://www.youtube.com/watch?v=realtime1",
                  "thumbnailUrl": "https://img.youtube.com/vi/realtime1/maxresdefault.jpg",
                  "playUrl": "https://www.youtube.com/embed/realtime1",
                  "mediaType": "VIDEO",
                  "platform": "YouTube",
                  "difficultyLevel": "초급",
                  "recommendationReason": "방금 학습한 내용의 약점을 빠르게 보완할 수 있는 짧은 동영상입니다",
                  "estimatedDuration": 5,
                  "language": "en",
                  "category": "문법",
                  "videoId": "realtime1",
                  "channelName": "Quick English Fix",
                  "viewCount": "89K",
                  "publishedAt": "2024-01-20"
                },
                {
                  "title": "Essential %s Vocabulary - 10 Minute Review",
                  "description": "Compact vocabulary review for %s with practical examples",
                  "url": "https://www.youtube.com/watch?v=realtime2",
                  "thumbnailUrl": "https://img.youtube.com/vi/realtime2/maxresdefault.jpg",
                  "playUrl": "https://www.youtube.com/embed/realtime2",
                  "mediaType": "VIDEO",
                  "platform": "YouTube",
                  "difficultyLevel": "초급",
                  "recommendationReason": "학습한 어휘를 즉시 복습하고 활용할 수 있는 짧은 동영상입니다",
                  "estimatedDuration": 10,
                  "language": "en",
                  "category": "어휘",
                  "videoId": "realtime2",
                  "channelName": "English Vocabulary Pro",
                  "viewCount": "156K",
                  "publishedAt": "2024-01-18"
                },
                {
                  "title": "5-Minute %s Practice Session",
                  "description": "Quick practice exercises to reinforce %s learning",
                  "url": "https://www.youtube.com/watch?v=realtime3",
                  "thumbnailUrl": "https://img.youtube.com/vi/realtime3/maxresdefault.jpg",
                  "playUrl": "https://www.youtube.com/embed/realtime3",
                  "mediaType": "VIDEO",
                  "platform": "YouTube",
                  "difficultyLevel": "초급",
                  "recommendationReason": "방금 학습한 내용을 바로 연습할 수 있는 실습 동영상입니다",
                  "estimatedDuration": 5,
                  "language": "en",
                  "category": "실습",
                  "videoId": "realtime3",
                  "channelName": "English Practice Hub",
                  "viewCount": "234K",
                  "publishedAt": "2024-01-15"
                }
              ]
            }
            ```
            """, topic, topic, topic, topic, topic, topic);
    }
    
    /**
     * 주기적 추천용 Mock 응답 생성 (다양한 미디어 유형)
     */
    private String generatePeriodicMockResponse(String prompt) {
        // 프롬프트에서 학습 주제 추출
        String topic = extractTopicFromPrompt(prompt);
        
        return String.format("""
            ```json
            {
              "recommendations": [
                {
                  "title": "Complete %s Mastery Course - 2 Hours",
                  "description": "Comprehensive course covering all aspects of %s from beginner to advanced",
                  "url": "https://www.youtube.com/watch?v=periodic1",
                  "thumbnailUrl": "https://img.youtube.com/vi/periodic1/maxresdefault.jpg",
                  "playUrl": "https://www.youtube.com/embed/periodic1",
                  "mediaType": "VIDEO",
                  "platform": "YouTube",
                  "difficultyLevel": "중급",
                  "recommendationReason": "장기적 성장을 위한 체계적이고 포괄적인 학습 콘텐츠입니다",
                  "estimatedDuration": 120,
                  "language": "en",
                  "category": "종합 과정",
                  "videoId": "periodic1",
                  "channelName": "English Mastery Academy",
                  "viewCount": "45K",
                  "publishedAt": "2024-01-10"
                },
                {
                  "title": "The %s Movie Collection",
                  "description": "Curated collection of movies and dramas perfect for %s learning",
                  "url": "https://www.netflix.com/collection/%s-learning",
                  "thumbnailUrl": "https://example.com/netflix-thumbnail.jpg",
                  "playUrl": "https://www.netflix.com/collection/%s-learning",
                  "mediaType": "VIDEO",
                  "platform": "Netflix",
                  "difficultyLevel": "중급",
                  "recommendationReason": "영화와 드라마를 통해 자연스러운 영어 표현을 학습할 수 있습니다",
                  "estimatedDuration": 480,
                  "language": "en",
                  "category": "영화/드라마",
                  "videoId": null,
                  "channelName": "Netflix",
                  "viewCount": null,
                  "publishedAt": "2024-01-01"
                },
                {
                  "title": "Advanced %s Podcast Series",
                  "description": "Weekly podcast episodes diving deep into advanced %s concepts",
                  "url": "https://open.spotify.com/show/advanced-%s-podcast",
                  "thumbnailUrl": "https://example.com/podcast-thumbnail.jpg",
                  "playUrl": "https://open.spotify.com/show/advanced-%s-podcast",
                  "mediaType": "AUDIO",
                  "platform": "Spotify",
                  "difficultyLevel": "고급",
                  "recommendationReason": "고급 수준의 %s 개념을 심화 학습할 수 있는 팟캐스트입니다",
                  "estimatedDuration": 45,
                  "language": "en",
                  "category": "팟캐스트",
                  "videoId": null,
                  "channelName": "Advanced English Learning",
                  "viewCount": "12K",
                  "publishedAt": "2024-01-05"
                },
                {
                  "title": "The Complete %s Audiobook",
                  "description": "Comprehensive audiobook covering all aspects of %s with native pronunciation",
                  "url": "https://www.audible.com/book/%s-complete",
                  "thumbnailUrl": "https://example.com/audiobook-thumbnail.jpg",
                  "playUrl": "https://www.audible.com/book/%s-complete",
                  "mediaType": "AUDIO",
                  "platform": "Audible",
                  "difficultyLevel": "중급",
                  "recommendationReason": "오디오북을 통해 %s를 체계적으로 학습하고 발음도 함께 연습할 수 있습니다",
                  "estimatedDuration": 360,
                  "language": "en",
                  "category": "오디오북",
                  "videoId": null,
                  "channelName": "Audible",
                  "viewCount": null,
                  "publishedAt": "2024-01-08"
                }
              ]
            }
            ```
            """, topic, topic, topic, topic, topic, topic, topic, topic, topic, topic, topic, topic, topic, topic, topic, topic);
    }
    
    /**
     * 프롬프트에서 학습 주제를 추출합니다.
     */
    private String extractTopicFromPrompt(String prompt) {
        if (prompt.contains("여행")) return "여행";
        if (prompt.contains("비즈니스")) return "비즈니스";
        if (prompt.contains("기술")) return "기술";
        return "일반";
    }
    
    /**
     * Mock 응답을 파싱합니다.
     */
    private List<MediaRecommendation> parseMockResponse(String responseText) {
        try {
            // JSON 부분 추출 (```json과 ``` 사이의 내용)
            String jsonContent = extractJsonContent(responseText);
            
            if (jsonContent == null) {
                log.warn("⚠️ JSON 응답을 찾을 수 없음 - 전체 응답: {}", responseText);
                return new ArrayList<>();
            }
            
            JsonNode rootNode = objectMapper.readTree(jsonContent);
            JsonNode recommendationsNode = rootNode.get("recommendations");
            
            if (recommendationsNode == null || !recommendationsNode.isArray()) {
                log.warn("⚠️ recommendations 배열을 찾을 수 없음");
                return new ArrayList<>();
            }
            
            List<MediaRecommendation> recommendations = new ArrayList<>();
            
            for (JsonNode recNode : recommendationsNode) {
                try {
                    MediaRecommendation recommendation = MediaRecommendation.builder()
                            .title(recNode.get("title").asText())
                            .description(recNode.get("description").asText())
                            .url(recNode.get("url").asText())
                            .mediaType(parseMediaType(recNode.get("mediaType").asText()))
                            .platform(recNode.get("platform").asText())
                            .difficultyLevel(recNode.get("difficultyLevel").asText())
                            .recommendationReason(recNode.get("recommendationReason").asText())
                            .estimatedDuration(recNode.has("estimatedDuration") ? 
                                    recNode.get("estimatedDuration").asInt() : null)
                            .language(recNode.has("language") ? 
                                    recNode.get("language").asText() : "en")
                            .category(recNode.has("category") ? 
                                    recNode.get("category").asText() : null)
                            .build();
                    
                    recommendations.add(recommendation);
                    
                } catch (Exception e) {
                    log.warn("⚠️ 개별 추천 항목 파싱 실패 - Node: {}, Error: {}", 
                            recNode, e.getMessage());
                }
            }
            
            return recommendations;
            
        } catch (JsonProcessingException e) {
            log.error("❌ JSON 파싱 실패 - Response: {}, Error: {}", 
                    responseText, e.getMessage(), e);
            throw new RuntimeException("Mock Gemini API 응답 파싱 실패", e);
        }
    }
    
    private String extractJsonContent(String responseText) {
        // ```json과 ``` 사이의 내용 추출
        int startIndex = responseText.indexOf("```json");
        if (startIndex == -1) {
            startIndex = responseText.indexOf("```");
        }
        
        if (startIndex == -1) {
            return null;
        }
        
        startIndex = responseText.indexOf("\n", startIndex) + 1;
        int endIndex = responseText.lastIndexOf("```");
        
        if (endIndex <= startIndex) {
            return null;
        }
        
        return responseText.substring(startIndex, endIndex).trim();
    }
    
    private MediaType parseMediaType(String mediaTypeStr) {
        try {
            return MediaType.valueOf(mediaTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("⚠️ 알 수 없는 미디어 타입: {}, 기본값 VIDEO 사용", mediaTypeStr);
            return MediaType.VIDEO;
        }
    }
}
