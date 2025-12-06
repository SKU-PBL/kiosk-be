package com.be.pbl.domain.admin.service;

import com.be.pbl.domain.admin.dto.request.ExhibitionCreateRequest;
import com.be.pbl.domain.exhibition.dto.response.ExhibitionInfoResponse;

public interface AdminExhibitionService {

    // 전시회 생성
    ExhibitionInfoResponse createExhibition(ExhibitionCreateRequest request);
}