package com.be.pbl.domain.question.service;

import com.be.pbl.domain.question.dto.request.QuestionCreateRequest;
import com.be.pbl.domain.question.dto.response.QuestionResponse;

import java.util.List;

public interface QuestionService {

    // 질문 전체 조회 (1~5번 모두)
    List<QuestionResponse> getAllQuestions();

    // 질문 단건(개별) 조회
    QuestionResponse getQuestion(Long id);
    QuestionResponse createQuestion(QuestionCreateRequest request);
}
