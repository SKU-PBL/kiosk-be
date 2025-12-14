package com.be.pbl.global.s3.dto.response;

import com.be.pbl.domain.exhibition.entity.Category;
import com.be.pbl.domain.question.entity.ImageDirection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "QuestionS3Response DTO", description = "질문에 맞는 이미지 s3로 업로드 응답")
public class QuestionS3Response {

    @Schema(description = "추가한 이미지의 질문 id", example = "1")
    private Long id;

    @Schema(description = "이미지 URL", example = "https://~")
    private String imageUrl; // S3 버킷 안에 있는 객체 url

    @Schema(description = "추가한 이미지의 질문 카테고리", example = "ERA(시대감)")
    private Category category;

    @Schema(description = "추가한 이미지 방향", example = "오른쪽")
    private ImageDirection direction;



}
