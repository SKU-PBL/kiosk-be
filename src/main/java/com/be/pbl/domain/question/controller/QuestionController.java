package com.be.pbl.domain.question.controller;

import com.be.pbl.domain.exhibition.entity.Category;
import com.be.pbl.domain.question.dto.request.QuestionCreateRequest;
import com.be.pbl.domain.question.dto.response.QuestionResponse;
import com.be.pbl.domain.question.entity.ImageDirection;
import com.be.pbl.domain.question.service.QuestionService;
import com.be.pbl.global.response.BaseResponse;
import com.be.pbl.global.s3.PathName;
import com.be.pbl.global.s3.dto.response.QuestionS3Response;
import com.be.pbl.global.s3.service.QuestionS3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/questions")
@Tag(name = "Question API", description = "이상형 월드컵 질문 관련 API")
public class QuestionController {

    private final QuestionService questionService;
    private final QuestionS3Service questionS3Service;

    @GetMapping("/all")
    @Operation(summary = "질문 전체 조회", description = "등록된 모든 질문을 조회합니다.")
    public ResponseEntity<BaseResponse<List<QuestionResponse>>> getAllQuestions() {
        List<QuestionResponse> responses = questionService.getAllQuestions();
        return ResponseEntity.ok(
                BaseResponse.success("질문 전체 조회에 성공했습니다.", responses)
        );
    }

    @GetMapping
    @Operation(summary = "랜덤 질문 5개 조회", description = "카테고리 별 1개씩 총 5개의 무작위 질문 조회합니다.")
    public ResponseEntity<BaseResponse<List<QuestionResponse>>> getFiveRandomQuestions() {
        List<QuestionResponse> responses = questionService.getFiveRandomQuestions();
        return ResponseEntity.ok(
                BaseResponse.success("카테고리별 랜덤 질문 5개 조회에 성공했습니다.", responses)
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "질문 단건 조회", description = "특정 ID의 질문을 조회합니다.")
    public ResponseEntity<BaseResponse<QuestionResponse>> getQuestion(@PathVariable Long id) {
        QuestionResponse response = questionService.getQuestion(id);
        return ResponseEntity.ok(
                BaseResponse.success("질문 단건 조회에 성공했습니다.", response)
        );
    }
    @PostMapping("/questions")
    @Operation(summary = "질문 생성", description = "관리자가 질문을 새로 생성합니다.")
    public ResponseEntity<BaseResponse<QuestionResponse>> createQuestion(
            @RequestBody @Valid QuestionCreateRequest request
    ) {
        QuestionResponse response = questionService.createQuestion(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(BaseResponse.success("질문 생성에 성공했습니다.", response));
    }

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "질문에 맞는 이미지 s3로 업로드 후 DB 업데이트", description = "해당 질문에 맞는 이미지를 방향에 맞게 s3로 업로드하고 question 테이블을 업데이트합니다.")
    public ResponseEntity<BaseResponse<QuestionS3Response>> uploadQuestion(
        @Parameter(description = "업로드 할 이미지") @RequestParam("image") MultipartFile file,
        @Parameter(description = "이미지에 맞는 질문 id") @RequestParam("questionId") Long questionId,
        @Parameter(description = "질문의 카테고리") @RequestParam("category")Category category,
        @Parameter(description = "이미지 방향") @RequestParam("imageDirection")ImageDirection direction
        ) {
        QuestionS3Response response = questionS3Service.uploadToS3(PathName.QUESTION, file, questionId, category, direction);
        return ResponseEntity.ok(BaseResponse.success("질문에 대한 이미지 업로드 성공", response));

    }

}
