package com.be.pbl.domain.exhibition.controller;

import com.be.pbl.domain.exhibition.dto.request.ExhibitionPatchRequest;
import com.be.pbl.domain.exhibition.dto.response.ExhibitionInfoResponse;
import com.be.pbl.domain.exhibition.dto.response.ExhibitionRecommendResponse;
import com.be.pbl.domain.exhibition.entity.Genre;
import com.be.pbl.domain.exhibition.service.ExhibitionService;
import com.be.pbl.domain.question.dto.request.QuestionAnswerListRequest;
import com.be.pbl.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Exhibition", description = "전시회 관련 API")
public class ExhibitionController {

    private final ExhibitionService exhibitionService;

    @GetMapping("/exhibition/{id}")
    @Operation(summary = "전시회 정보 단일 조회", description = "전시회id로 특정 전시회 정보 조회 후 조회수 +1 증가")
    public ResponseEntity<BaseResponse<ExhibitionInfoResponse>> getExhibitionById(@PathVariable Long id) {
        ExhibitionInfoResponse response = exhibitionService.getExhibition(id);
        return ResponseEntity.ok(BaseResponse.success("특정 전시회 정보 조회에 성공했습니다.",response));
    }

    // 조회수 영향 x
    @GetMapping("/recommend-exhibition/{id}")
    @Operation(summary = "이상형 월드컵 종료 후 추천된 전시회 정보 단일 조회", description = "전시회id로 특정 전시회 정보 조회(조회수 증가 없음)")
    public ResponseEntity<BaseResponse<ExhibitionInfoResponse>> getRecommendExhibitionById(@PathVariable Long id) {
        ExhibitionInfoResponse response = exhibitionService.getExhibitionUnaffectedViews(id);
        return ResponseEntity.ok(BaseResponse.success("추천 받은 특정 전시회 정보 조회에 성공했습니다.",response));
    }

    @GetMapping("/exhibitions")
    @Operation(summary = "전시회 정보 전체 조회", description = "전시회 정보 전체 조회")
    public ResponseEntity<BaseResponse<List<ExhibitionInfoResponse>>> getExhibition() {
        List<ExhibitionInfoResponse> response = exhibitionService.getAllExhibition();
        return ResponseEntity.ok(BaseResponse.success("전시회 정보 전체 조회에 성공했습니다.",response));
    }
    @PatchMapping("/exhibitions/{id}")
    @Operation(summary = "전시회 정보 수정", description = "전시회 필드 정보를 수정합니다.")

    public ResponseEntity<BaseResponse<Void>> updateExhibition(
            @PathVariable Long id,
            @RequestBody ExhibitionPatchRequest request
    ) {
        exhibitionService.updateExhibition(id, request);
        return ResponseEntity.ok(BaseResponse.success("전시회 수정 완료", null));
    }
    @GetMapping("/exhibitions/genre/{genre}")
    @Operation(summary = "장르별 전시회 조회", description = "장르를 기준으로 전시회 리스트를 조회합니다.")
    public ResponseEntity<BaseResponse<List<ExhibitionInfoResponse>>> getExhibitionsByGenre(
            @PathVariable Genre genre
    ) {
        List<ExhibitionInfoResponse> response = exhibitionService.getExhibitionsByGenre(genre);
        return ResponseEntity.ok(BaseResponse.success("장르별 전시회 조회 성공", response));
    }
    @PostMapping("/recommend")
    @Operation(summary = "이상형 월드컵(전시회 추천)", description = "5개의 질문에 대한 사용자 응답을 기반으로 전시회를 추천합니다.")
    public ResponseEntity<BaseResponse<ExhibitionRecommendResponse>> recommend(
            @RequestBody QuestionAnswerListRequest request
    ) {
        ExhibitionRecommendResponse response = exhibitionService.recommendExhibitions(request);

        return ResponseEntity.ok(
                BaseResponse.success("전시회 추천 성공", response)
        );
    }
    @PostMapping("/naver-count/reload")
    @Operation(
            summary = "모든 전시회 네이버 블로그 카운트 갱신", description = "모든 전시회의 제목으로 최근 1개월 기준 네이버 블로그 게시물 수를 naverCount 필드에 저장합니다."
    )
    public ResponseEntity<BaseResponse<Void>> reloadAllNaverCount() {
        exhibitionService.updateAllExhibitionsNaverCount();
        return ResponseEntity.ok(
                BaseResponse.success("네이버 블로그 카운트 전체 갱신 완료", null)
        );
    }

    @PostMapping("/{exhibitionId}/naver-count/reload")
    @Operation(
            summary = "단일 전시회 네이버 블로그 카운트 갱신", description = "특정 전시회의 제목으로 최근 1개월 기준 네이버 블로그 게시글 수를 naverCount 필드에 저장합니다."
    )
    public ResponseEntity<BaseResponse<Void>> reloadOneNaverCount(
            @PathVariable Long exhibitionId
    ) {
        exhibitionService.updateNaverCount(exhibitionId);
        return ResponseEntity.ok(
                BaseResponse.success("네이버 블로그 카운트 갱신 완료", null)
        );
    }
    @GetMapping("/exhibitions/naver-count")
    @Operation(
            summary = "네이버 블로그 언급 수 기준 전시회 정렬 조회",
            description = "naverCount 기준으로 전시회를 정렬하여 조회합니다. (기본: 내림차순)"
    )
    public ResponseEntity<BaseResponse<List<ExhibitionInfoResponse>>> getExhibitionsOrderByNaverCount(){
        List<ExhibitionInfoResponse> response =
                exhibitionService.getExhibitionsOrderByNaverCount();

        return ResponseEntity.ok(
                BaseResponse.success("네이버 블로그 언급 수 기준 전시회 조회 성공", response)
        );
    }

}
