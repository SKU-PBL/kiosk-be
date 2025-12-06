package com.be.pbl.domain.admin.controller;

import com.be.pbl.domain.admin.dto.request.ExhibitionCreateRequest;
import com.be.pbl.domain.admin.service.AdminExhibitionService;
import com.be.pbl.domain.exhibition.dto.response.ExhibitionInfoResponse;
import com.be.pbl.global.response.BaseResponse;
import com.be.pbl.global.s3.PathName;
import com.be.pbl.global.s3.dto.response.S3Response;
import com.be.pbl.global.s3.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "관리자 전용 API")
@Slf4j
public class AdminController {

    private final AdminExhibitionService adminExhibitionService;
    private final S3Service s3Service;

    @PostMapping("/exhibitions")
    @Operation(
        summary = "전시회 생성",
        description = "관리자가 새로운 전시회를 생성합니다."
    )
    public ResponseEntity<BaseResponse<ExhibitionInfoResponse>> createExhibition(
            @Valid @RequestBody ExhibitionCreateRequest request
    ) {
        log.info("전시회 생성 API 호출: {}", request.getTitle());

        ExhibitionInfoResponse response = adminExhibitionService.createExhibition(request);
        return ResponseEntity.ok(BaseResponse.success("전시회가 성공적으로 생성되었습니다.", response));
    }

    @PostMapping("/uploadToS3")
    @Operation(
        summary = "전시회 이미지 url s3 업로드",
        description = "전시회 이미지 url s3로 동기화"
    )
    public ResponseEntity<BaseResponse<S3Response>> uploadToS3(
        @RequestParam PathName pathName,
        @RequestParam Long id
        ) {
        S3Response response = s3Service.uploadExhibitionImages(pathName, id);
        return ResponseEntity.ok(BaseResponse.success("s3 동기화 결과", response));
    }
}