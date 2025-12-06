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

    @Override
    @Transactional
    public void migrateAllExhibitionImagesToS3() {
        log.info("전시회 이미지 S3 마이그레이션 시작");

        List<Exhibition> exhibitions = exhibitionRepository.findAll();

        if (exhibitions.isEmpty()) {
            log.warn("마이그레이션할 전시회가 없습니다.");
            return;
        }

        int totalCount = 0;
        int successCount = 0;
        int failCount = 0;

        for (Exhibition exhibition : exhibitions) {
            List<String> imageUrls = exhibition.getImageUrls();

            if (imageUrls == null || imageUrls.isEmpty()) {
                log.info("전시회 ID {}: 이미지가 없습니다.", exhibition.getId());
                continue;
            }

            log.info("전시회 ID {}: {} 개의 이미지 마이그레이션 시작", exhibition.getId(), imageUrls.size());

            // 외부 URL인지 확인 (S3 URL이 아닌 것만 마이그레이션)
            List<String> externalUrls = imageUrls.stream()
                .filter(url -> !url.contains("amazonaws.com") && !url.contains("s3"))
                .toList();

            if (externalUrls.isEmpty()) {
                log.info("전시회 ID {}: 이미 S3 URL입니다. 스킵합니다.", exhibition.getId());
                continue;
            }

            try {
                // S3로 마이그레이션
                List<String> migratedUrls = s3Service.migrateImageUrls(PathName.EXHIBITION, externalUrls);

                // 기존 이미지 URL 제거 후 새로운 S3 URL 추가
                imageUrls.clear();
                imageUrls.addAll(migratedUrls);

                exhibitionRepository.save(exhibition);

                totalCount += migratedUrls.size();
                successCount += migratedUrls.stream()
                    .filter(url -> url.contains("amazonaws.com") || url.contains("s3"))
                    .count();
                failCount += migratedUrls.size() - successCount;

                log.info("전시회 ID {}: 마이그레이션 완료", exhibition.getId());

            } catch (Exception e) {
                log.error("전시회 ID {}: 마이그레이션 실패", exhibition.getId(), e);
                failCount += externalUrls.size();
            }
        }

        log.info("전시회 이미지 S3 마이그레이션 완료. 총 {}개, 성공 {}개, 실패 {}개",
                 totalCount, successCount, failCount);
    }
}