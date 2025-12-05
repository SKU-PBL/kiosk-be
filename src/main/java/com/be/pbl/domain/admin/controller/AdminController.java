package com.be.pbl.global.s3.controller;

import com.be.pbl.domain.exhibition.service.ExhibitionService;
import com.be.pbl.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "관리자 전용 API")
@Slf4j
public class AdminController {

    private final ExhibitionService exhibitionService;

    @PostMapping("/migrate-exhibition-images")
    @Operation(
        summary = "전시회 이미지 S3 마이그레이션",
        description = "DB에 저장된 외부 이미지 URL을 S3에 업로드하고 URL을 변경합니다. 이미 S3 URL인 경우 스킵됩니다."
    )
    public ResponseEntity<BaseResponse<String>> migrateExhibitionImages() {
        log.info("전시회 이미지 S3 마이그레이션 API 호출");

        try {
            exhibitionService.migrateAllExhibitionImagesToS3();
            return ResponseEntity.ok(
                BaseResponse.success("전시회 이미지 S3 마이그레이션이 완료되었습니다.", "SUCCESS")
            );
        } catch (Exception e) {
            log.error("전시회 이미지 S3 마이그레이션 실패", e);
            return ResponseEntity.internalServerError().body(
                BaseResponse.fail("전시회 이미지 S3 마이그레이션에 실패했습니다: " + e.getMessage())
            );
        }
    }
}