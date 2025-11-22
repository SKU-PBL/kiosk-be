package com.be.pbl.domain.question.controller;

import com.be.pbl.domain.question.dto.request.QuestionCreateRequest;
import com.be.pbl.domain.question.dto.response.QuestionResponse;
import com.be.pbl.domain.question.service.QuestionService;
import com.be.pbl.global.response.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/questions")
@Tag(name = "Question API", description = "이상형 월드컵 질문 관련 API")
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping
    @Operation(summary = "질문 전체 조회", description = "이상형 월드컵에 사용되는 모든 질문을 순서대로 조회합니다.")
    public ResponseEntity<BaseResponse<List<QuestionResponse>>> getAllQuestions() {
        List<QuestionResponse> responses = questionService.getAllQuestions();
        return ResponseEntity.ok(
                BaseResponse.success("질문 전체 조회에 성공했습니다.", responses)
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

}
