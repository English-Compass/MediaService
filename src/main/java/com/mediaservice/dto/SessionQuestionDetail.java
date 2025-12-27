package com.mediaservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionQuestionDetail {
    private String questionId;
    private String questionText;
    private List<String> options; // A, B, C
    private String correctAnswer; // "A" | "B" | "C"
    private String explanation;

    // Category info
    private String majorCategory; // 대분류
    private String minorCategory; // 소분류

    // Difficulty
    private Integer difficultyLevel; // 1, 2, 3

    // User answer context from session_question
    private String userAnswer;
    private boolean isCorrect;
    private Integer timeSpent;     // seconds
    private Integer attemptCount;  // number of tries
}
