package com.mediaservice.dto;

import com.mediaservice.model.MediaRecommendation;
import lombok.Data;

import java.util.List;

/**
 * Gemini API의 최상위 JSON 응답을 파싱하기 위한 DTO입니다.
 */
@Data
public class GeminiResponse {

    /**
     * API로부터 반환된 추천 미디어 콘텐츠 리스트입니다.
     */
    private List<MediaRecommendation> recommendations;

}

