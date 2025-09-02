package com.mediaservice.service.impl;

import com.mediaservice.dto.UserActivitySummary;
import com.mediaservice.event.LearningCompletedEvent;
import com.mediaservice.service.PromptTemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import com.mediaservice.dto.SessionQuestionDetail;
import com.mediaservice.dto.UserPerformanceSummary;

import java.util.Map;

@Slf4j
@Service
public class PromptTemplateServiceImpl implements PromptTemplateService {

    @Override
    public String generateRealTimeSessionPrompt(LearningCompletedEvent event) {
        
        StringBuilder prompt = new StringBuilder();
        prompt.append("당신은 영어 학습자를 위한 맞춤형 미디어 콘텐츠 추천 전문가입니다.\n\n");
        prompt.append("## 🎯 실시간 세션 기반 추천 요청 (짧은 유튜브 동영상 위주)\n");
        prompt.append("사용자가 방금 완료한 학습 세션을 분석하여 **어려워하는 부분을 보완**할 수 있는 **짧은 유튜브 동영상**을 우선적으로 추천해주세요.\n\n");
        
        // =====================================================
        // 학습 세션 정보 (learning_sessions 테이블 기반)
        // =====================================================
        prompt.append("## 📚 방금 완료한 학습 세션 정보\n");
        prompt.append("- 세션 ID: ").append(event.getSessionId()).append("\n");
        prompt.append("- 세션 상태: ").append(event.getSessionStatus()).append(" (STARTED, IN_PROGRESS, COMPLETED)\n");
        prompt.append("- 세션 타입: ").append(event.getSessionType()).append(" (PRACTICE, REVIEW, WRONG_ANSWER)\n");
        prompt.append("- 진행률: ").append(event.getProgressPercentage()).append("%\n");
        prompt.append("- 세션 완료: ").append(event.getSessionCompletedAt()).append("\n\n");
        
        // =====================================================
        // 학습 성과 통계 (집계 데이터)
        // =====================================================
        prompt.append("## 📊 이번 세션 학습 성과\n");
        prompt.append("- 총 문제: ").append(event.getTotalQuestions()).append("개, 답변: ").append(event.getAnsweredQuestions()).append("개\n");
        prompt.append("- 정답: ").append(event.getCorrectAnswers()).append("개, 오답: ").append(event.getWrongAnswers()).append("개\n");
        prompt.append("- **정답률: ").append(event.getAccuracyRate()).append("%**, **오답률: ").append(event.getErrorRate()).append("%**\n");
        prompt.append("- 평균 풀이 시간: ").append(event.getAvgTimeSpent()).append("초\n");
        prompt.append("- 총 학습 시간: ").append(event.getTotalLearningTimeMinutes()).append("분\n\n");
        
        // =====================================================
        // 문제 카테고리 정보 (question 테이블 기반)
        // =====================================================
        prompt.append("## 🏷️ 이번 세션에서 학습한 문제 카테고리\n");
        prompt.append("- 대분류: ").append(event.getMajorCategory()).append("\n");
        prompt.append("- 소분류: ").append(event.getMinorCategory()).append("\n");
        prompt.append("- 문제 유형: ").append(event.getQuestionType()).append(" (MULTIPLE_CHOICE, FILL_IN_BLANK 등)\n");
        prompt.append("- 평균 난이도: ").append(event.getAvgDifficultyLevel()).append(" (1: 초급, 2: 중급, 3: 상급)\n\n");
        
        // =====================================================
        // 카테고리별 구체적 맥락 설명 (새로 추가)
        // =====================================================
        prompt.append("## 🎯 카테고리별 학습 맥락\n");
        String majorCategory = event.getMajorCategory();
        String minorCategory = event.getMinorCategory();
        
        if ("여행".equals(majorCategory)) {
            prompt.append("- **여행 영어**: ").append(getTravelContext(minorCategory)).append("\n");
        } else if ("비즈니스".equals(majorCategory)) {
            prompt.append("- **비즈니스 영어**: ").append(getBusinessContext(minorCategory)).append("\n");
        } else if ("학업".equals(majorCategory)) {
            prompt.append("- **학업 영어**: ").append(getEducationContext(minorCategory)).append("\n");
        } else if ("일상생활".equals(majorCategory)) {
            prompt.append("- **일상생활 영어**: ").append(getDailyLifeContext(minorCategory)).append("\n");
        }
        
        prompt.append("- **학습 목표**: ").append(majorCategory).append(" 상황에서 ").append(minorCategory).append("와 관련된 영어 표현과 어휘를 학습\n\n");
        
        // =====================================================
        // 사용자 학습 관심사 (major_category, minor_category 기반)
        // =====================================================
        prompt.append("## 👤 사용자 학습 관심사\n");
        prompt.append("- 학습 대분류: ").append(event.getMajorCategory()).append("\n");
        prompt.append("- 학습 소분류: ").append(event.getMinorCategory()).append("\n\n");
        
        // =====================================================
        // 틀린 문제 상세 분석 (새로 추가)
        // =====================================================
        if (event.getSessionQuestions() != null && !event.getSessionQuestions().isEmpty()) {
            prompt.append("## 🔍 틀린 문제 상세 분석 (AI 추천의 핵심 정보)\n");
            prompt.append("**중요**: 아래 틀린 문제들의 구체적인 내용을 바탕으로 정확한 약점을 파악하고 맞춤형 콘텐츠를 추천해주세요.\n\n");
            
            int wrongQuestionCount = 0;
            
            for (SessionQuestionDetail sqd : event.getSessionQuestions()) {
                if (sqd.isCorrect()) continue; // 맞은 문제 제외
                wrongQuestionCount++;
                
                prompt.append("- **틀린 문제 ").append(wrongQuestionCount).append("**: ").append(sqd.getQuestionText()).append("\n");
                prompt.append("  - **문제 유형**: ").append(sqd.getMajorCategory()).append(" - ").append(sqd.getMinorCategory()).append("\n");
                prompt.append("  - **난이도**: ").append(sqd.getDifficultyLevel()).append(" (1: 초급, 2: 중급, 3: 상급)\n");
                
                // 선택지 정보
                if (sqd.getOptions() != null && sqd.getOptions().size() >= 3) {
                    prompt.append("  - **선택지**: A) ").append(sqd.getOptions().get(0))
                          .append(", B) ").append(sqd.getOptions().get(1))
                          .append(", C) ").append(sqd.getOptions().get(2)).append("\n");
                }
                
                prompt.append("  - **사용자 답변**: ").append(sqd.getUserAnswer()).append(" (틀림)\n");
                prompt.append("  - **정답**: ").append(sqd.getCorrectAnswer()).append(" (맞음)\n");
                
                // 해설 정보
                if (sqd.getExplanation() != null && !sqd.getExplanation().trim().isEmpty()) {
                    prompt.append("  - **해설**: ").append(sqd.getExplanation()).append("\n");
                }
                
                prompt.append("  - **풀이 시간**: ").append(sqd.getTimeSpent()).append("초\n");
                prompt.append("  - **시도 횟수**: ").append(sqd.getAttemptCount()).append("회\n\n");
                
                // AI를 위한 구체적 분석 요청
                prompt.append("  **AI 분석 요청**: 이 문제에서 사용자가 틀린 구체적인 부분은 무엇인가요? 어떤 영어 표현이나 문법 규칙을 몰랐나요?\n\n");
            }
            
            if (wrongQuestionCount == 0) {
                prompt.append("- 모든 문제를 맞췄습니다! 🎉\n\n");
            }
        }
        
        // =====================================================
        // 틀린 문제 패턴 분석 (새로 추가)
        // =====================================================
        prompt.append("## 🎯 틀린 문제 패턴 분석\n");
        prompt.append("- 틀린 문제 수: ").append(event.getWrongAnswers()).append("개\n");
        prompt.append("- 정답 문제 수: ").append(event.getCorrectAnswers()).append("개\n");
        prompt.append("- 평균 풀이 시간: ").append(event.getAvgTimeSpent()).append("초\n");
        prompt.append("- 가장 많이 틀린 카테고리: ").append(event.getMajorCategory())
              .append(" - ").append(event.getMinorCategory()).append("\n");
        prompt.append("- 문제 유형: ").append(event.getQuestionType()).append("\n");
        prompt.append("- 평균 난이도: ").append(event.getAvgDifficultyLevel()).append("\n\n");
        
        // =====================================================
        // 실시간 추천 요청 (짧은 유튜브 동영상 위주)
        // =====================================================
        prompt.append("## 🎯 실시간 추천 요청 (짧은 유튜브 동영상 위주)\n");
        prompt.append("위 세션 결과를 바탕으로 **사용자가 어려워하는 부분을 보완**할 수 있는 **짧은 유튜브 동영상**을 우선적으로 추천해주세요:\n\n");
        
        prompt.append("1. **짧은 유튜브 동영상 우선**: 1-3분 이내의 짧고 집중적인 유튜브 동영상\n");
        prompt.append("2. **구체적 문제 내용 기반**: 위에서 분석한 틀린 문제들의 구체적인 내용을 바탕으로 정확한 약점 파악\n");
        prompt.append("3. **오답률 ").append(event.getErrorRate()).append("% 보완**: '").append(event.getMajorCategory()).append(" - ").append(event.getMinorCategory()).append("' 주제에서 틀린 부분을 보완할 수 있는 콘텐츠\n");
        prompt.append("4. **난이도 적합성**: 현재 평균 난이도 ").append(event.getAvgDifficultyLevel()).append("에서 약간 낮은 수준의 이해하기 쉬운 콘텐츠\n");
        prompt.append("5. **학습 패턴 반영**: ").append(event.getSessionType()).append(" 세션 유형에 적합한 실습/복습 콘텐츠\n");
        prompt.append("6. **즉시 활용**: 방금 학습한 내용을 바로 적용할 수 있는 실용적 콘텐츠\n");
        // 장르 선호도 제거: 실시간 추천은 대분류/소분류 중심
        prompt.append("7. **카테고리 적합성**: 해당 대분류/소분류 맥락에 직접적으로 연결된 콘텐츠 우선 고려\n\n");
        
        prompt.append("**중요**: 실시간 추천은 **짧은 유튜브 동영상(0-3분)**을 우선적으로 추천하고, 필요시에만 다른 미디어 유형을 포함하세요.\n\n");
        
        prompt.append("**추천 전략**:\n");
        prompt.append("- 틀린 문제의 구체적인 내용을 분석하여 어떤 영어 표현이나 문법 규칙을 몰랐는지 파악\n");
        prompt.append("- 해당 약점을 보완할 수 있는 구체적이고 실용적인 콘텐츠 우선 추천\n");
        prompt.append("- 문제에서 사용된 어휘나 표현과 직접적으로 연관된 콘텐츠 포함\n\n");
        
        // =====================================================
        // 응답 형식
        // =====================================================
        prompt.append("## 📝 응답 형식\n");
        prompt.append("다음 JSON 형식으로 응답해주세요:\n\n");
        prompt.append("```json\n");
        prompt.append("{\n");
        prompt.append("  \"recommendations\": [\n");
        prompt.append("    {\n");
        prompt.append("      \"title\": \"콘텐츠 제목\",\n");
        prompt.append("      \"description\": \"간단한 설명\",\n");
        prompt.append("      \"url\": \"콘텐츠 링크\",\n");
        prompt.append("      \"thumbnailUrl\": \"썰네일 이미지 URL\",\n");
        prompt.append("      \"playUrl\": \"재생용 URL (유튜브의 경우 embed URL)\",\n");
        prompt.append("      \"mediaType\": \"VIDEO|AUDIO|ARTICLE|BOOK\",\n");
        prompt.append("      \"platform\": \"YouTube|Spotify|Medium 등\",\n");
        prompt.append("      \"difficultyLevel\": \"초급|중급|고급\",\n");
        prompt.append("      \"recommendationReason\": \"이 콘텐츠가 오답률 ").append(event.getErrorRate()).append("%를 보완하는 구체적인 이유\",\n");
        prompt.append("      \"estimatedDuration\": 15,\n");
        prompt.append("      \"language\": \"en\",\n");
        prompt.append("      \"category\": \"카테고리 정보\",\n");
        prompt.append("      \"videoId\": \"유튜브 비디오 ID (watch?v= 뒤의 값)\",\n");
        prompt.append("      \"channelName\": \"유튜브 채널명\",\n");
        prompt.append("      \"viewCount\": \"조회수\",\n");
        prompt.append("      \"publishedAt\": \"업로드 날짜\"\n");
        prompt.append("    }\n");
        prompt.append("  ]\n");
        prompt.append("}\n");
        prompt.append("```\n\n");
        
        // =====================================================
        // 추가 지시사항 (실시간 추천 특화 - 짧은 유튜브 위주)
        // =====================================================
        prompt.append("## ⚡ 실시간 추천 특화 지시사항 (짧은 유튜브 동영상 위주)\n");
        prompt.append("- **짧은 유튜브 우선**: 1-3분 이내의 짧고 집중적인 유튜브 동영상을 우선적으로 추천\n");
        prompt.append("- **구체적 약점 분석**: 위에서 분석한 틀린 문제들의 구체적인 내용을 바탕으로 정확한 약점 파악\n");
        prompt.append("- **즉시 보완**: 방금 틀린 문제와 직접적으로 연관된 콘텐츠 우선 추천\n");
        prompt.append("- **난이도 조정**: 오답률이 높으므로 현재보다 낮은 난이도의 이해하기 쉬운 콘텐츠 포함\n");
        prompt.append("- **실용성**: 이론보다는 실습/예제 중심의 콘텐츠 제공\n");
        prompt.append("- **빠른 복습**: 세션 완료 직후 바로 활용할 수 있는 짧고 효과적인 콘텐츠\n");
        // 장르 선호도 제거: 실시간 추천은 학습 카테고리 맥락 중심
        prompt.append("- **카테고리 적합성**: 학습 대분류/소분류 맥락에 적합한 콘텐츠 우선 고려\n");
        prompt.append("- **미디어 유형**: 80% 유튜브 동영상(1-3분), 15% 팟캐스트, 5% 기타 (짧은 콘텐츠 위주)\n");
        prompt.append("- 모든 URL은 실제 접근 가능한 링크여야 합니다\n");
        prompt.append("- 추천 이유는 구체적이고 설득력 있어야 합니다\n");
        prompt.append("- 한국어로 응답하되, 콘텐츠 제목과 설명은 영어로 제공하세요\n");
        
        String finalPrompt = prompt.toString();
        log.debug("📝 실시간 세션 기반 프롬프트 생성 (짧은 유튜브 위주):\n{}", finalPrompt);
        
        return finalPrompt;
    }

    /**
     * 사용자 요청 기반 추천을 위한 프롬프트를 생성합니다.
     * 
     * 핵심 성과 지표만 사용하여 효율적인 추천을 생성합니다.
     */
    @Override
    public String generateUserRequestedPrompt(UserPerformanceSummary userPerformance, List<String> selectedGenres) {
        StringBuilder prompt = new StringBuilder();
        
        prompt.append("# 🎯 사용자 요청 기반 미디어 추천 프롬프트\n\n");
        
        // =====================================================
        // 1. 추천 목표 및 요구사항
        // =====================================================
        prompt.append("## 📋 추천 목표\n");
        prompt.append("사용자의 **핵심 성과 지표**를 분석하여 **다양한 미디어 타입**의 콘텐츠를 추천합니다.\n\n");
        
        prompt.append("## 🎬 추천 요구사항\n");
        prompt.append("- **유튜브 동영상**: 길이 상관없음 (URL 제공)\n");
        prompt.append("- **영화/드라마**: 짧은 설명과 함께 추천\n");
        prompt.append("- **오디오북**: 짧은 설명과 함께 추천\n");
        prompt.append("- **사용자가 선택한 장르**: ").append(String.join(", ", selectedGenres)).append("\n\n");
        
        // =====================================================
        // 2. 사용자 성과 분석
        // =====================================================
        prompt.append("## 📊 사용자 성과 분석\n");
        prompt.append("**사용자가 선택한 장르**: ").append(String.join(", ", selectedGenres)).append("\n\n");
        
        // =====================================================
        // 3. 카테고리별 성과 분석
        // =====================================================
        prompt.append("## 🏷️ 카테고리별 성과 분석\n");
        prompt.append("**중요**: 성과가 낮은 카테고리를 우선적으로 보완할 콘텐츠를 추천해주세요.\n\n");
        
        if (userPerformance.getCategoryPerformance() != null && !userPerformance.getCategoryPerformance().isEmpty()) {
            for (Map.Entry<String, Double> entry : userPerformance.getCategoryPerformance().entrySet()) {
                String category = entry.getKey();
                Double performance = entry.getValue();
                String status = performance >= 80 ? "🟢 우수" : performance >= 60 ? "🟡 보통" : "🔴 보완 필요";
                
                prompt.append("- **").append(category).append("**: ").append(performance).append("% (").append(status).append(")\n");
            }
            prompt.append("\n");
        }
        
        // =====================================================
        // 4. 난이도별 성과 분석
        // =====================================================
        prompt.append("## 📈 난이도별 성과 분석\n");
        prompt.append("**중요**: 성과가 낮은 난이도 단계를 단계적으로 향상시킬 콘텐츠를 추천해주세요.\n\n");
        
        if (userPerformance.getDifficultyPerformance() != null && !userPerformance.getDifficultyPerformance().isEmpty()) {
            for (Map.Entry<Integer, Double> entry : userPerformance.getDifficultyPerformance().entrySet()) {
                Integer difficulty = entry.getKey();
                Double performance = entry.getValue();
                String level = switch (difficulty) {
                    case 1 -> "초급";
                    case 2 -> "중급";
                    case 3 -> "고급";
                    default -> "기타";
                };
                String status = performance >= 80 ? "🟢 우수" : performance >= 60 ? "🟡 보통" : "🔴 보완 필요";
                
                prompt.append("- **").append(level).append(" (Lv.").append(difficulty).append(")**: ").append(performance).append("% (").append(status).append(")\n");
            }
            prompt.append("\n");
        }
        
        // =====================================================
        // 5. 추천 전략
        // =====================================================
        prompt.append("## 🎯 추천 전략\n");
        prompt.append("1. **성과가 낮은 카테고리 우선**: 보완이 필요한 영역의 콘텐츠를 먼저 추천\n");
        prompt.append("2. **난이도 단계별 향상**: 현재 단계에서 다음 단계로 나아갈 수 있는 콘텐츠 추천\n");
        prompt.append("3. **선택된 장르 반영**: 사용자가 선택한 장르의 콘텐츠를 포함\n");
        prompt.append("4. **다양한 미디어 타입**: 유튜브, 영화/드라마, 오디오북을 균형있게 추천\n\n");
        
        // =====================================================
        // 6. AI 분석 요청
        // =====================================================
        prompt.append("## 🤖 AI 분석 요청\n");
        prompt.append("위의 성과 지표를 분석하여 다음을 고려한 추천을 생성해주세요:\n\n");
        
        prompt.append("1. **성과가 낮은 카테고리**에서 어떤 콘텐츠가 도움이 될까요?\n");
        prompt.append("2. **현재 난이도**에서 **다음 단계**로 나아갈 수 있는 콘텐츠는 무엇일까요?\n");
        prompt.append("3. **사용자가 선택한 장르**를 반영하면서도 **학습 효과**를 높일 수 있는 콘텐츠는?\n");
        prompt.append("4. **다양한 미디어 타입**을 통해 **학습 동기**를 유지할 수 있는 콘텐츠는?\n\n");
        
        prompt.append("## 📝 출력 형식\n");
        prompt.append("JSON 형태로 다음 정보를 포함하여 추천해주세요:\n");
        prompt.append("- title: 콘텐츠 제목\n");
        prompt.append("- description: 콘텐츠 설명\n");
        prompt.append("- url: 콘텐츠 링크\n");
        prompt.append("- mediaType: VIDEO, AUDIO, ARTICLE, BOOK 중 하나\n");
        prompt.append("- platform: YouTube, Netflix, Spotify 등\n");
        prompt.append("- difficultyLevel: 초급, 중급, 고급\n");
        prompt.append("- recommendationReason: 이 콘텐츠를 추천하는 이유\n");
        prompt.append("- estimatedDuration: 예상 소요 시간 (분)\n\n");
        
        prompt.append("**총 5-8개의 다양한 콘텐츠를 추천해주세요.**\n");
        
        return prompt.toString();
    }

    private String getTravelContext(String minorCategory) {
        switch (minorCategory) {
            case "배낭":
                return "배낭 여행에 필요한 영어 표현과 어휘를 학습합니다.";
            case "가족":
                return "가족과 함께하는 여행에 필요한 영어 표현과 어휘를 학습합니다.";
            case "친구":
                return "친구와 함께하는 여행에 필요한 영어 표현과 어휘를 학습합니다.";
            default:
                return "여행 관련 영어 표현과 어휘를 학습합니다.";
        }
    }

    private String getBusinessContext(String minorCategory) {
        switch (minorCategory) {
            case "회사":
                return "회사 업무에 필요한 영어 표현과 어휘를 학습합니다.";
            case "미팅":
                return "미팅 진행과 참여에 필요한 영어 표현과 어휘를 학습합니다.";
            case "회의":
                return "회의 진행과 참여에 필요한 영어 표현과 어휘를 학습합니다.";
            default:
                return "비즈니스 관련 영어 표현과 어휘를 학습합니다.";
        }
    }

    private String getEducationContext(String minorCategory) {
        switch (minorCategory) {
            case "대학교":
                return "대학교 수업과 학업에 필요한 영어 표현과 어휘를 학습합니다.";
            case "학원":
                return "학원 수업과 학업에 필요한 영어 표현과 어휘를 학습합니다.";
            case "대학원":
                return "대학원 수업과 학업에 필요한 영어 표현과 어휘를 학습합니다.";
            default:
                return "학업 관련 영어 표현과 어휘를 학습합니다.";
        }
    }

    private String getDailyLifeContext(String minorCategory) {
        switch (minorCategory) {
            case "가족":
                return "가족과의 일상생활에 필요한 영어 표현과 어휘를 학습합니다.";
            case "친구":
                return "친구와의 일상생활에 필요한 영어 표현과 어휘를 학습합니다.";
            case "선생님":
                return "선생님과의 일상생활에 필요한 영어 표현과 어휘를 학습합니다.";
            default:
                return "일상생활 관련 영어 표현과 어휘를 학습합니다.";
        }
    }
}


