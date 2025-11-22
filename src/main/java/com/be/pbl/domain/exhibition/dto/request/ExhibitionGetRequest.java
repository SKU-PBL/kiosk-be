package com.be.pbl.domain.exhibition.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "ExhibitionGetRequest DTO", description = "전시회 관련 정보 조회 요청")
public class ExhibitionGetRequest {
    @Schema(description = "전시회 id")
    private Long id;
}
