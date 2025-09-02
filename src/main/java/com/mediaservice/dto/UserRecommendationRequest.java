package com.mediaservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 사용자 추천 요청 DTO
 * 
 * 사용자가 미디어 추천 페이지에서 장르를 선택하여
 * 맞춤형 추천을 요청할 때 사용됩니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRecommendationRequest {
    
    /**
     * 사용자 ID
     */
    @NotNull(message = "사용자 ID는 필수입니다.")
    private Long userId;
    
    /**
     * 사용자가 선택한 장르 목록
     * 
     * 선택 가능한 장르:
     * 액션, 드라마, 코미디, 로맨스, 스릴러, 공포, 미스터리, 
     * SF, 판타지, 범죄, 전쟁, 음악, 애니메이션, 다큐멘터리
     */
    @NotEmpty(message = "최소 1개 이상의 장르를 선택해야 합니다.")
    @Size(min = 1, max = 5, message = "1개에서 5개까지 장르를 선택할 수 있습니다.")
    private List<String> selectedGenres;
}

