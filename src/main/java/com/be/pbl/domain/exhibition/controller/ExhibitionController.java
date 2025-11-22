package com.be.pbl.domain.exhibition.controller;

import com.be.pbl.domain.exhibition.dto.response.ExhibitionInfoResponse;
import com.be.pbl.domain.exhibition.service.ExhibitionService;
import com.be.pbl.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Exhibition", description = "전시회 관련 API")
public class ExhibitionController {

    private final ExhibitionService exhibitionService;

    @GetMapping("/exhibition/{id}")
    @Operation(summary = "전시회 정보 단일 조회", description = "전시회id로 특정 전시회 정보 조회")
    public ResponseEntity<BaseResponse<ExhibitionInfoResponse>> getExhibitionById(@PathVariable Long id) {
        ExhibitionInfoResponse response = exhibitionService.getExhibition(id);
        return ResponseEntity.ok(BaseResponse.success("특정 전시회 정보 조회에 성공했습니다.",response));
    }

    @GetMapping("/exhibitions")
    @Operation(summary = "전시회 정보 전체 조회", description = "전시회 정보 전체 조회")
    public ResponseEntity<BaseResponse<List<ExhibitionInfoResponse>>> getExhibition() {
        List<ExhibitionInfoResponse> response = exhibitionService.getAllExhibition();
        return ResponseEntity.ok(BaseResponse.success("전시회 정보 전체 조회에 성공했습니다.",response));
    }

}
