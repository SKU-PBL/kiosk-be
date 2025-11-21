package com.be.pbl.domain.question.dto.request;

import com.be.pbl.domain.exhibition.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Schema(title = "QuestionRequest", description = "질문 생성 요청")
public class QuestionCreateRequest {
    @Schema(description = "질문이 속한 카테고리")
    private Category category;

    @Schema(description = "질문 내용")
    private String content;

    @Schema(description = "왼쪽 이미지 URL")
    private String leftImageUrl;

    @Schema(description = "오른쪽 이미지 URL")
    private String rightImageUrl;
}
