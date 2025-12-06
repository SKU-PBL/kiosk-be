package com.be.pbl.domain.admin.dto.request;

import com.be.pbl.domain.exhibition.entity.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "전시회 생성 요청 DTO")
public class ExhibitionCreateRequest {

    @Schema(description = "전시회 제목", example = "모네: 빛의 순간들")
    private String title;

    @Schema(description = "전시회 설명", example = "인상주의의 거장 클로드 모네의 대표작들을 한자리에서 만나볼 수 있는 특별 전시")
    private String description;

    @Schema(description = "전시회 주소", example = "서울특별시 종로구 삼청로 30")
    private String address;

    @Schema(description = "작가명", example = "클로드 모네")
    private String author;

    @Schema(description = "전시 시작일", example = "2025-01-01")
    private LocalDate startDate;

    @Schema(description = "전시 종료일", example = "2025-03-31")
    private LocalDate endDate;

    @Schema(description = "오픈 시간", example = "10:00:00")
    private LocalTime openTime;

    @Schema(description = "종료 시간", example = "18:00:00")
    private LocalTime closeTime;

    @Schema(description = "태그 리스트", example = "[\"MODERN\", \"BRIGHT\"]")
    private List<Tag> tags;

    @Schema(description = "조회수 (기본값: 0)", example = "0")
    private String views;

    @Schema(description = "이미지 URL 리스트",
        example = "[\"https://www.insaartcenter.com/data/file/exhibition_current/f908992e10ec9c87f45e0f975130eaa9_XU48dzVM_63cc0f9944eb4da15b1a8f18eea32a1c941c3d25.jpg\", " +
            "\"https://www.insaartcenter.com/data/file/exhibition_current/f908992e10ec9c87f45e0f975130eaa9_eJQPS3Kz_894e3d3bd08f3f7153ebc9d27d4b676538b8cb6e.jpg\"]")
    private List<String> imageUrls;

    @Schema(description = "갤러리명", example = "국립현대미술관")
    private String galleryName;

    @Schema(description = "갤러리 전화번호", example = "02-1234-5678")
    private String phoneNum;
}