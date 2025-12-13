package com.be.pbl.domain.admin.controller;

import com.be.pbl.domain.admin.dto.request.ExhibitionCreateRequest;
import com.be.pbl.domain.admin.dto.response.TagResponse;
import com.be.pbl.domain.admin.service.AdminExhibitionService;
import com.be.pbl.domain.admin.service.TagService;
import com.be.pbl.domain.exhibition.dto.response.ExhibitionInfoResponse;
import com.be.pbl.domain.exhibition.service.ExhibitionService;
import com.be.pbl.global.response.BaseResponse;
import com.be.pbl.global.s3.PathName;
import com.be.pbl.global.s3.dto.response.S3Response;
import com.be.pbl.global.s3.service.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "관리자 전용 API")
@Slf4j
public class AdminController {

    private final AdminExhibitionService adminExhibitionService;
    private final S3Service s3Service;
    private final TagService  tagService;
    private final ExhibitionService exhibitionService;

    @PostMapping("/exhibitions")
    @Operation(
        summary = "전시회 생성",
        description = "관리자가 새로운 전시회를 생성"
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
        description = "isS3Upload가 false인 모든 전시회 이미지를 s3로 업로드"
    )
    public ResponseEntity<BaseResponse<S3Response>> uploadToS3(
        @RequestParam PathName pathName
        ) {
        log.info("S3 업로드 API 호출: pathName={}", pathName);
        S3Response response = s3Service.uploadExhibitionImages(pathName);
        return ResponseEntity.ok(BaseResponse.success("s3 동기화 결과", response));
    }

    @PostMapping("/createTag")
    @Operation(
        summary = "전시회 설명기반 태그 생성",
        description = "모든 전시회 내용을 프롬프트에 넘겨 태그를 생성한 후 태그 업데이트"
    )
    public ResponseEntity<BaseResponse<List<TagResponse>>> createTag() {
        log.info("전시회 설명 기반 태그 생성 시작");
        List<TagResponse> response = tagService.createTag();
        return ResponseEntity.ok(BaseResponse.success("태그 생성 성공",  response));
    }

    @PostMapping("/updateNaverCount")
    @Operation(
            summary = "(전시회 제목 + 갤러리 이름) 기반 네이버 블로그 카운트 저장",
            description = "naverCount가 비어있는 전시회를 대상으로 최근 1개월 네이버 블로그 게시물 수를 계산하여 저장합니다."
    )
    public ResponseEntity<BaseResponse<Void>> updateNaverCount(){
        log.info("전시회 제목 기반 네이버 블로그 카운트 저장 시작");
        exhibitionService.updateNaverCountForEmpty();
        return ResponseEntity.ok(BaseResponse.success("네이버 블로그 카운트 저장 성공", null));
    }
}