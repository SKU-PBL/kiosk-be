package com.be.pbl.domain.question.service;

import com.be.pbl.domain.question.dto.request.QuestionCreateRequest;
import com.be.pbl.domain.question.dto.response.QuestionResponse;

import java.util.List;

public interface QuestionService {

    // 질문 전체 조회 (1~5번 모두)
    List<QuestionResponse> getFiveRandomQuestions();

    // 질문 단건(개별) 조회
    QuestionResponse getQuestion(Long id);

    // 질문 생성
    QuestionResponse createQuestion(QuestionCreateRequest request);

    // 모든 질문 조회
    List<QuestionResponse> getAllQuestions();
}
