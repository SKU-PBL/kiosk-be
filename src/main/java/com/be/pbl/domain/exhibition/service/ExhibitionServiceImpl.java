package com.be.pbl.domain.exhibition.service;

import com.be.pbl.domain.exhibition.dto.response.ExhibitionInfoResponse;
import com.be.pbl.domain.exhibition.entity.Exhibition;
import com.be.pbl.domain.exhibition.exception.ExhibitionErrorCode;
import com.be.pbl.domain.exhibition.mapper.ExhibitionMapper;
import com.be.pbl.domain.exhibition.repository.ExhibitionRepository;
import com.be.pbl.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExhibitionServiceImpl implements ExhibitionService {

    private final ExhibitionRepository exhibitionRepository;
    private final ExhibitionMapper exhibitionMapper;

    @Override
    @Transactional(readOnly = true)
    public ExhibitionInfoResponse getExhibition(Long id) {

        try {
            log.info("전시회 정보 조회 {} ", id);
            Exhibition exhibition = exhibitionRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));
        } catch (Exception e) {
            log.error("전시회 정보 조회 실패");
            throw new CustomException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND);
        }

    }

    @Override
    @Transactional(readOnly = true)
    public List<ExhibitionInfoResponse> getAllExhibition() {
        try {
            log.info("전시회 정보 전체 조회");
            List<Exhibition> exhibitions = exhibitionRepository.findAll();
            return exhibitions.stream()
                .map(exhibitionMapper::toExhibitionResponse)
                .toList();
        } catch (Exception e) {
            log.error("전시회 정보 전체 조회 실패");
            throw new CustomException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND);
        }
    }
}
