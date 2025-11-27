package com.be.pbl.domain.exhibition.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@Schema(title = "ExhibitionRecommendResponse DTO", description = "추천된 전시회 목록 + 선호 태그 응답")
@Builder
public class ExhibitionRecommendResponse {

    @Schema(description = "사용자 응답 기반 상위 선호 태그 목록 (최대 3개)")
    private List<TopTagResponse> topTags;


    @Schema(description = "추천 전시회 리스트")
    private List<ExhibitionInfoResponse> exhibitions;


}