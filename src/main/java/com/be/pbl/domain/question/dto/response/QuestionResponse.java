package com.be.pbl.domain.question.dto.response;

import com.be.pbl.domain.exhibition.entity.Category;
import com.be.pbl.domain.exhibition.entity.Tag;
import com.be.pbl.domain.question.entity.Question;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(title = "QuestionResponse", description = "이상형 월드컵 질문 응답 DTO")
public class QuestionResponse {

    @Schema(description = "질문 ID")
    private Long id;

    @Schema(description = "질문 카테고리 (ERA, EXPRESSION, ...)")
    private Category category;

    @Schema(description = "질문 내용")
    private String content;

    @Schema(description = "왼쪽 태그")
    private String leftTag;

    @Schema(description = "오른쪽 태그")
    private String rightTag;

    @Schema(description = "왼쪽 이미지 URL")
    private String leftImageUrl;

    @Schema(description = "오른쪽 이미지 URL")
    private String rightImageUrl;
}

