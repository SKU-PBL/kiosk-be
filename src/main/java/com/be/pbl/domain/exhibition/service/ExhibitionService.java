package com.be.pbl.domain.exhibition.service;

import com.be.pbl.domain.exhibition.dto.response.ExhibitionInfoResponse;

import java.util.List;

public interface ExhibitionService {
    ExhibitionInfoResponse getExhibition(Long id); // 전시회 단일 조회
    List<ExhibitionInfoResponse> getAllExhibition(); // 전시회 정보 전체 조회
}
