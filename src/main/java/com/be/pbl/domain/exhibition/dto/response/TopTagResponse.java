package com.be.pbl.domain.exhibition.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "TopTagResponse DTO", description = "사용자 응답 기반 상위 선호 태그 정보")
public class TopTagResponse {

    @Schema(description = "태그 이름 (ENUM name)", example = "ABSTRACT")
    private String tagName;

    @Schema(description = "태그 설명", example = "추상적")
    private String tagDescription;

    @Schema(description = "해당 태그에 누적된 점수", example = "6")
    private int score;
}