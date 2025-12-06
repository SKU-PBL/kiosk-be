package com.be.pbl.global.s3.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@Schema(title = "S3Response DTO", description = "전시회 이미지 url s3로 동기화 후 응답 반환")
public class S3Response {

    @Schema(description = "전시회 ID")
    private Long exhibitionId;

    @Schema(description = "업로드된 S3 URL 리스트")
    private List<String> s3Urls;

    @Schema(description = "성공한 이미지 개수")
    private int successCount;

    @Schema(description = "실패한 이미지 개수")
    private int failCount;
}
