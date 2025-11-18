package com.be.pbl.domain.exhibition.dto.response;

import com.be.pbl.domain.exhibition.entity.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(title = "ExhibitionTag DTO", description = "전시회 관련 태그 응답 반환")
public class ExhibitionTag {

    @Schema(description = "전시회 태그")
    private String tagName;

    @Schema(description = "전시회 태그 tjfaud")
    private String tagDescription;
}
