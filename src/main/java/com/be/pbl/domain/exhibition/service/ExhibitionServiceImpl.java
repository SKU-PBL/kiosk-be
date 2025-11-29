package com.be.pbl.domain.exhibition.service;

import com.be.pbl.domain.exhibition.dto.response.ExhibitionInfoResponse;
import com.be.pbl.domain.exhibition.dto.response.ExhibitionRecommendResponse;
import com.be.pbl.domain.exhibition.dto.response.TopTagResponse;
import com.be.pbl.domain.exhibition.entity.Exhibition;
import com.be.pbl.domain.exhibition.entity.Tag;
import com.be.pbl.domain.exhibition.exception.ExhibitionErrorCode;
import com.be.pbl.domain.exhibition.mapper.ExhibitionMapper;
import com.be.pbl.domain.exhibition.repository.ExhibitionRepository;
import com.be.pbl.domain.question.dto.request.QuestionAnswerListRequest;
import com.be.pbl.domain.question.entity.Question;
import com.be.pbl.domain.question.repository.QuestionRepository;
import com.be.pbl.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import com.be.pbl.global.exception.CustomException;
import com.be.pbl.domain.exhibition.exception.ExhibitionErrorCode;
import com.be.pbl.domain.question.exception.QuestionErrorCode;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExhibitionServiceImpl implements ExhibitionService {

    private final QuestionRepository questionRepository;
    private final ExhibitionRepository exhibitionRepository;
    private final ExhibitionMapper exhibitionMapper;

    @Override
    @Transactional(readOnly = true)
    public ExhibitionInfoResponse getExhibition(Long id) {

        try {
            log.info("전시회 정보 조회 {} ", id);
            Exhibition exhibition = exhibitionRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));
            return exhibitionMapper.toExhibitionResponse(exhibition);
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

            if (exhibitions.isEmpty()) {
                log.info("전시회 테이블이 비어있습니다(저장된 전시회 없음).");
            }

            return exhibitions.stream()
                    .map(exhibitionMapper::toExhibitionResponse)
                    .toList();
        } catch (Exception e) {
            log.error("전시회 정보 전체 조회 실패");
            throw new CustomException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND);
        }
    }

    // 이상형 월드컵
    @Override
    @Transactional(readOnly = true)
    public ExhibitionRecommendResponse recommendExhibitions(QuestionAnswerListRequest request) {

        // 1. 태그별 점수 계산
        Map<Tag, Integer> tagScore = new EnumMap<>(Tag.class);

        request.getAnswers().forEach(answer -> {
            Question question = questionRepository.findById(answer.getQuestionId())
                    .orElseThrow(() -> new CustomException(QuestionErrorCode.QUESTION_NOT_FOUND));

            Tag left = question.getCategory().getLeft();
            Tag right = question.getCategory().getRight();
            int score = answer.getScore();

            switch (score) {
                case 0 -> tagScore.merge(left, 2, Integer::sum);   // 왼쪽 완전 선호
                case 1 -> tagScore.merge(left, 1, Integer::sum);   // 왼쪽 약간 선호
                case 2 -> { /* 중립 (점수 없음) */ }
                case 3 -> tagScore.merge(right, 1, Integer::sum);  // 오른쪽 약간 선호
                case 4 -> tagScore.merge(right, 2, Integer::sum);  // 오른쪽 완전 선호
            }
        });

        // max 점수 (전부 0인지 확인용)
        int max = tagScore.values().stream().max(Integer::compareTo).orElse(0);

        // 2. 상위 태그(응답 + 추천 둘 다에 사용할 기반)
        //    - 점수 0 초과만
        //    - 점수 내림차순
        //    - 최대 3개
        List<Map.Entry<Tag, Integer>> topTagEntries = tagScore.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(3)
                .toList();

        // 응답용 DTO
        List<TopTagResponse> topTags = topTagEntries.stream()
                .map(entry -> TopTagResponse.builder()
                        .tagName(entry.getKey().name())
                        .tagDescription(entry.getKey().getDescription())
                        .score(entry.getValue())
                        .build())
                .toList();

        // 추천 계산용 Set<Tag>
        Set<Tag> topTagSet = topTagEntries.stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        // 3. 전시 목록 조회
        List<Exhibition> allExhibitions = exhibitionRepository.findAll();
        if (allExhibitions.isEmpty()) {
            throw new CustomException(ExhibitionErrorCode.EXHIBITION_LIST_EMPTY);
        }

        // 4. 모든 점수가 0이면 → 랜덤 3개 추천
        if (max == 0 || topTagSet.isEmpty()) {
            Collections.shuffle(allExhibitions);
            List<Exhibition> random3 = allExhibitions.stream()
                    .limit(3)
                    .toList();

            return ExhibitionRecommendResponse.builder()
                    .topTags(topTags)  // 이 경우 거의 빈 리스트일 가능성이 큼
                    .exhibitions(exhibitionMapper.toRecommendResponses(random3))
                    .build();
        }

        // 5. 전시회별 점수 계산 (topTags에 포함된 태그만 사용!!)
        Map<Exhibition, Integer> exhibitionScore = new HashMap<>();

        for (Exhibition exhibition : allExhibitions) {
            int score = exhibition.getTags().stream()
                    .filter(topTagSet::contains) // ← 핵심: topTags 안에 있는 태그만 점수로 사용
                    .mapToInt(tag -> tagScore.getOrDefault(tag, 0))
                    .sum();
            exhibitionScore.put(exhibition, score);
        }

        // 6. 점수 높은 순으로 정렬 후 상위 3개 전시회 선택
        List<Exhibition> top3Exhibitions = exhibitionScore.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue())) // 내림차순
                .limit(3)
                .map(Map.Entry::getKey)
                .toList();

        // 7. DTO 변환 후 반환
        return ExhibitionRecommendResponse.builder()
                .topTags(topTags) // 사용자가 어떤 태그에 반응했는지
                .exhibitions(exhibitionMapper.toRecommendResponses(top3Exhibitions)) // 그 태그 기반 추천 전시
                .build();
    }
}