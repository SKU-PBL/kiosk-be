package com.be.pbl.domain.exhibition.service;

import com.be.pbl.domain.exhibition.dto.response.ExhibitionInfoResponse;
import com.be.pbl.domain.exhibition.dto.response.ExhibitionRecommendResponse;
import com.be.pbl.domain.exhibition.dto.response.TopTagResponse;
import com.be.pbl.domain.question.dto.request.QuestionAnswerListRequest;

import java.util.List;

public interface ExhibitionService {
    ExhibitionInfoResponse getExhibition(Long id); // 전시회 단일 조회
    List<ExhibitionInfoResponse> getAllExhibition(); // 전시회 정보 전체 조회
    ExhibitionRecommendResponse recommendExhibitions(QuestionAnswerListRequest request); // 이상형 월드컵 응답 기반 전시회 3개 추천 메서드
}
