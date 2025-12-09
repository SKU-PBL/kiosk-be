package com.be.pbl.domain.exhibition.dto.request;
import com.be.pbl.domain.exhibition.entity.Genre;
import com.be.pbl.domain.exhibition.entity.Tag;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@Schema(description = "전시회 수정 요청 DTO")
public class ExhibitionPatchRequest {

    private String title;
    private String description;
    private String address;
    private String author;

    private LocalDate startDate;
    private LocalDate endDate;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime openTime;
    @JsonFormat(pattern = "HH:mm")
    private LocalTime closeTime;

    private List<String> imageUrls;
    private List<Tag> tags;

    private Genre genre;

    private String galleryName;
    private String phoneNum;
}
