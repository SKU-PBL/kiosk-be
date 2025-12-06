package com.be.pbl.domain.admin.service;

import com.be.pbl.domain.admin.dto.request.ExhibitionCreateRequest;
import com.be.pbl.domain.exhibition.dto.response.ExhibitionInfoResponse;

public interface AdminExhibitionService {

    // 전시회 생성
    ExhibitionInfoResponse createExhibition(ExhibitionCreateRequest request);

    // 모든 전시회 이미지를 외부 URL에서 S3로 마이그레이션
    void migrateAllExhibitionImagesToS3();
}