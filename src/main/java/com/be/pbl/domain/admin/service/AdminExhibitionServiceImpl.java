package com.be.pbl.domain.admin.service;

import com.be.pbl.domain.admin.dto.request.ExhibitionCreateRequest;
import com.be.pbl.domain.exhibition.dto.response.ExhibitionInfoResponse;
import com.be.pbl.domain.exhibition.entity.Exhibition;
import com.be.pbl.domain.exhibition.exception.ExhibitionErrorCode;
import com.be.pbl.domain.exhibition.mapper.ExhibitionMapper;
import com.be.pbl.domain.exhibition.repository.ExhibitionRepository;
import com.be.pbl.global.exception.CustomException;
import com.be.pbl.global.s3.PathName;
import com.be.pbl.global.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminExhibitionServiceImpl implements AdminExhibitionService {

    private final ExhibitionRepository exhibitionRepository;
    private final ExhibitionMapper exhibitionMapper;
    private final S3Service s3Service;

    @Override
    @Transactional
    public ExhibitionInfoResponse createExhibition(ExhibitionCreateRequest request) {
        log.info("전시회 생성 시작: {}", request.getTitle());

        try {
            // Exhibition 엔티티 생성
            Exhibition exhibition = Exhibition.builder()
                    .title(request.getTitle())
                    .description(request.getDescription())
                    .address(request.getAddress())
                    .author(request.getAuthor())
                    .startDate(request.getStartDate())
                    .endDate(request.getEndDate())
                    .openTime(request.getOpenTime())
                    .closeTime(request.getCloseTime())
                    .tags(request.getTags())
                    .views(request.getViews() != null ? request.getViews() : "0")
                    .imageUrls(request.getImageUrls())
                    .galleryName(request.getGalleryName())
                    .phoneNum(request.getPhoneNum())
                    .build();

            // DB 저장
            Exhibition savedExhibition = exhibitionRepository.save(exhibition);

            log.info("전시회 생성 완료: ID={}, 제목={}", savedExhibition.getId(), savedExhibition.getTitle());

            // Response 변환 후 반환
            return exhibitionMapper.toExhibitionResponse(savedExhibition);

        } catch (Exception e) {
            log.error("전시회 생성 실패: {}", request.getTitle(), e);
            throw new CustomException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND);
        }
    }
}