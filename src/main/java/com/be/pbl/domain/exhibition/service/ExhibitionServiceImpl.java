package com.be.pbl.domain.exhibition.service;

import com.be.pbl.domain.exhibition.dto.request.ExhibitionPatchRequest;
import com.be.pbl.domain.exhibition.dto.response.ExhibitionInfoResponse;
import com.be.pbl.domain.exhibition.dto.response.ExhibitionRecommendResponse;
import com.be.pbl.domain.exhibition.dto.response.TopTagResponse;
import com.be.pbl.domain.exhibition.entity.Exhibition;
import com.be.pbl.domain.exhibition.entity.Genre;
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
    @Transactional
    public ExhibitionInfoResponse getExhibition(Long id) {

        try {
            log.info("전시회 정보 조회 id = {} ", id);
            Exhibition exhibition = exhibitionRepository.findById(id)
                    .orElseThrow(() -> new CustomException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));

            // 조회수 증가
            int increaseViews = exhibition.getViews() + 1;
            exhibition.updateViews(increaseViews);
            log.info("전시회 ID {} 조회수 증가: {} -> {}", id, exhibition.getViews() - 1, exhibition.getViews());

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
    @Override
    @Transactional(readOnly = true)
    public List<ExhibitionInfoResponse> getExhibitionsByGenre(Genre genre) {
        log.info("장르별 전시회 조회: {}", genre);
        List<Exhibition> exhibitions = exhibitionRepository.findByGenre(genre);

        if (exhibitions.isEmpty()) {
            log.warn("해당 장르에 대한 전시회 없음: {}", genre);
            throw new CustomException(ExhibitionErrorCode.EXHIBITION_BY_GENRE_EMPTY);
        }

        return exhibitions.stream()
                .map(exhibitionMapper::toExhibitionResponse)
                .toList();
    }

    @Override
    @Transactional
    public void updateExhibition(Long id, ExhibitionPatchRequest request) {
        Exhibition exhibition = exhibitionRepository.findById(id)
                .orElseThrow(() -> new CustomException(ExhibitionErrorCode.EXHIBITION_NOT_FOUND));

        exhibitionMapper.updateFromPatch(exhibition, request);
    }


    // 이상형 월드컵
    @Override
    @Transactional(readOnly = true)
    public ExhibitionRecommendResponse recommendExhibitions(QuestionAnswerListRequest request) {

        if (request == null || request.getAnswers() == null || request.getAnswers().isEmpty()) {
            throw new CustomException(ExhibitionErrorCode.EXHIBITION_LIST_EMPTY);
        }

        // 1. 태그별 점수 계산
        Map<Tag, Double> tagScore = calculateTagScores(request);

        double totalTagScore = tagScore.values().stream()
                .mapToDouble(Double::doubleValue)
                .sum();
        // 2. 전시 전체 조회 (여기서 직접 예외 처리)
        List<Exhibition> allExhibitions = exhibitionRepository.findAll();
        if (allExhibitions.isEmpty()) {
            throw new CustomException(ExhibitionErrorCode.EXHIBITION_LIST_EMPTY);
        }
        // 3. 취향이 거의 없는 경우 → 랜덤 추천
        if (isNoPreference(totalTagScore, tagScore)) {
            return buildRandomRecommendation(allExhibitions);
        }

        // 4. 태그 Top3 계산
        List<Map.Entry<Tag, Double>> topTagEntries = pickTopTagEntries(tagScore);

        List<TopTagResponse> topTags = toTopTagResponses(topTagEntries);

        // 5. 전시별 점수 계산
        Map<Exhibition, Double> exhibitionScore = scoreExhibitions(allExhibitions, topTagEntries, tagScore);

        List<Exhibition> top3Exhibitions = pickTopExhibitions(exhibitionScore, 3);
        return ExhibitionRecommendResponse.builder()
                .topTags(topTags)
                .exhibitions(exhibitionMapper.toRecommendResponses(top3Exhibitions))
                .build();
    }
    // 0~4 -> left/right weight
    private static final double[][] SCORE_WEIGHTS = {
            {1.0, 0.0},
            {0.75, 0.25},
            {0.5, 0.5},
            {0.25, 0.75},
            {0.0, 1.0}
    };
    // private 메서드: 사용자 답변을 기반으로 태그별 가중치 점수를 누적 계산
    private Map<Tag, Double> calculateTagScores(QuestionAnswerListRequest request) {
        Map<Tag, Double> tagScore = new EnumMap<>(Tag.class);

        request.getAnswers().forEach(answer -> {
            Question question = questionRepository.findById(answer.getQuestionId())
                    .orElseThrow(() -> new CustomException(QuestionErrorCode.QUESTION_NOT_FOUND));

            Tag left = question.getCategory().getLeft();
            Tag right = question.getCategory().getRight();

            int rawScore = answer.getScore();
            int score = Math.max(0, Math.min(4, rawScore));

            double[] weights = SCORE_WEIGHTS[score];
            double leftWeight = weights[0];
            double rightWeight = weights[1];

            tagScore.merge(left, leftWeight, Double::sum);
            tagScore.merge(right, rightWeight, Double::sum);
        });

        return tagScore;
    }


    // “취향 없음” 판단 로직
    private boolean isNoPreference(double totalTagScore, Map<Tag, Double> tagScore) {
        if (totalTagScore == 0) {
            return true;
        }

        double max = tagScore.values().stream()
                .mapToDouble(Double::doubleValue)
                .max()
                .orElse(0.0);

        double min = tagScore.values().stream()
                .mapToDouble(Double::doubleValue)
                .min()
                .orElse(0.0);

        // max와 min 차이가 너무 작으면 → 뚜렷한 취향이 없다고 판단
        double diff = max - min;
        return diff < 0.05;
    }
    private ExhibitionRecommendResponse buildRandomRecommendation(List<Exhibition> allExhibitions) {
        Collections.shuffle(allExhibitions);
        List<Exhibition> random3 = allExhibitions.stream()
                .limit(3)
                .toList();

        return ExhibitionRecommendResponse.builder()
                .topTags(Collections.emptyList())
                .exhibitions(exhibitionMapper.toRecommendResponses(random3))
                .build();
    }
    private List<Map.Entry<Tag, Double>> pickTopTagEntries(Map<Tag, Double> tagScore) {
        return tagScore.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(3)
                .toList();
    }
    private List<TopTagResponse> toTopTagResponses(List<Map.Entry<Tag, Double>> entries) {
        return entries.stream()
                .map(entry -> {
                    double value = entry.getValue(); // 0.0 ~ 1.0
                    int percent = (int) Math.round(value * 100); // 0 ~ 100

                    return TopTagResponse.builder()
                            .tagName(entry.getKey().name())
                            .tagDescription(entry.getKey().getDescription())
                            .score(percent)  // "이 태그 선호도 0~100"
                            .build();
                })
                .toList();
    }
    private Map<Exhibition, Double> scoreExhibitions(
            List<Exhibition> exhibitions,
            List<Map.Entry<Tag, Double>> topTagEntries,
            Map<Tag, Double> tagScore
    ) {
        Set<Tag> topTagSet = topTagEntries.stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        Map<Exhibition, Double> exhibitionScore = new HashMap<>();

        for (Exhibition exhibition : exhibitions) {
            List<Tag> tags = exhibition.getTags();

            if (tags == null || tags.isEmpty()) {
                exhibitionScore.put(exhibition, 0.0);
                continue;
            }

            double sum = tags.stream()
                    .filter(topTagSet::contains)
                    .mapToDouble(tag -> tagScore.getOrDefault(tag, 0.0))
                    .sum();

            double avg = sum / tags.size();
            exhibitionScore.put(exhibition, avg);
        }

        return exhibitionScore;
    }
    private List<Exhibition> pickTopExhibitions(Map<Exhibition, Double> exhibitionScore, int limit) {
        return exhibitionScore.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .toList();
    }

}


