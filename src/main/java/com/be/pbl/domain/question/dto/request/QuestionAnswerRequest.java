package com.be.pbl.domain.question.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 각 질문에 대한 응답 하나
@Getter
@NoArgsConstructor
public class QuestionAnswerRequest {
    @Schema(description = "질문 ID", example = "1")
    private Long questionId;

    @Schema(description = "사용자가 선택한 점수 (0~4)", example = "3")
    private int score;
}
