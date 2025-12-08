package com.be.pbl.domain.admin.dto.response;

import com.be.pbl.domain.exhibition.entity.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "TagResponse DTO", description = "전시회 정보기반 태그 응답 DTO")
public class TagResponse {

    @Schema(description = "전시회 태그")
    private Tag tag; // 추후에 enum 타입으로 변경

    @Schema(description = "전시회 태그 설명")
    private String tagDescription = tag.getDescription();

}
