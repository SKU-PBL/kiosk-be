package com.be.pbl.domain.exhibition.dto.response;

import com.be.pbl.domain.exhibition.entity.Tag;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@Schema(title = "ExhibitionInfoResponse DTO", description = "전시회 관련 정보 응답 반환")
public class ExhibitionInfoResponse {

    @Schema(description = "전시회 id")
    private Long id;

    @Schema(description = "전시회 제목")
    private String title;

    @Schema(description = "전시회 설명")
    private String description;

    @Schema(description = "전시회 주소")
    private String address;

    @Schema(description = "작가 이름")
    private String author;

    @Schema(description = "전시 시작일")
    private LocalDate startDate;

    @Schema(description = "전시 종료일")
    private LocalDate endDate;

    @JsonFormat(pattern = "HH:mm")
    @Schema(description = "시작 시간", example = "10:00")
    private LocalTime openTime;

    @JsonFormat(pattern = "HH:mm")
    @Schema(description = "마감 시간", example = "18:00")
    private LocalTime closeTime;

    /*@Schema(description = "운영 안내", example = "매주 월요일은 휴관")
    private String operatingNotice;*/

    @Schema(description = "작품 태그")
    private List<ExhibitionTag> tags;

    @Schema(description = "조회수")
    private int views;

    @Schema(description = "이미지 url 리스트")
    private List<String> imagesUrls;

    @Schema(description = "갤러리명")
    private String galleryName;

    @Schema(description = "전화번호")
    private String phoneNum;
}
