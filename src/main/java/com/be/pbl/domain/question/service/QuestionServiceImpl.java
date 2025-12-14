package com.be.pbl.domain.question.service;

import com.be.pbl.domain.exhibition.entity.Category;
import com.be.pbl.domain.question.dto.request.QuestionCreateRequest;
import com.be.pbl.domain.question.dto.response.QuestionResponse;
import com.be.pbl.domain.question.entity.Question;
import com.be.pbl.domain.question.exception.QuestionErrorCode;
import com.be.pbl.domain.question.mapper.QuestionMapper;
import com.be.pbl.domain.question.repository.QuestionRepository;
import com.be.pbl.global.exception.CustomException;
import com.be.pbl.global.s3.PathName;
import com.be.pbl.global.s3.service.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;
    private final S3Service s3Service;

    @Override
    public List<QuestionResponse> getFiveRandomQuestions() {
        List<Question> questions = questionRepository.findAll(); // 혹은 카테고리별 조회

        if (questions.size() < 5) {
            throw new CustomException(QuestionErrorCode.QUESTION_NOT_ENOUGH_FOR_RANDOM);
        }

        // 랜덤으로 5개 뽑는 로직...
        List<Question> randomFive = pickFiveRandom(questions);

        return randomFive.stream()
                .map(questionMapper::toResponse)
                .toList();
    }

    // getFiveRandomQuestions를 위한 내부 비즈니스 로직 메서드
    private List<Question> pickFiveRandom(List<Question> questions) {

        // 전체 질문을 카테고리 기준으로 그룹핑
        Map<Category, List<Question>> grouped = questions.stream()
                .collect(Collectors.groupingBy(Question::getCategory));

        List<Question> result = new ArrayList<>();

        // 모든 카테고리에 대해 1개씩 선택
        for (Category category : Category.values()) {
            List<Question> categoryQuestions = grouped.get(category);

            // 해당 카테고리에 질문이 없다면 에러 처리
            if (categoryQuestions == null || categoryQuestions.isEmpty()) {
                throw new CustomException(QuestionErrorCode.QUESTION_NOT_ENOUGH_FOR_RANDOM);
            }

            // 해당 카테고리에서 랜덤으로 하나 선택
            Question random = categoryQuestions.get(
                    ThreadLocalRandom.current().nextInt(categoryQuestions.size())
            );

            result.add(random);
        }

        return result;
    }

    @Override
    public QuestionResponse getQuestion(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new CustomException(QuestionErrorCode.QUESTION_NOT_FOUND));

        return questionMapper.toResponse(question);
    }


    @Override
    @Transactional
    public QuestionResponse createQuestion(QuestionCreateRequest request) {
        try {
            log.info("질문 생성 요청: {}", request.getContent());

            // 1) 요청 이미지 URL을 S3에 업로드하고 S3 URL을 얻는다
            String leftS3Url = s3Service.uploadImageFromUrl(PathName.QUESTION, request.getLeftImageUrl());
            String rightS3Url = s3Service.uploadImageFromUrl(PathName.QUESTION, request.getRightImageUrl());

            // 2) entity 생성
            Question question = questionMapper.toEntity(request);

            // 3) entity에 S3 URL 반영
            question.updateImages(leftS3Url, rightS3Url);

            // 4) 저장
            Question saved = questionRepository.save(question);
            return questionMapper.toResponse(saved);

        } catch (Exception e) {
            log.error("질문 생성 실패", e);
            // ❗ 지금 NOT_FOUND는 의미가 안 맞음. CREATE 실패용 코드가 있으면 그걸로.
            throw new CustomException(QuestionErrorCode.QUESTION_CREATE_FAILED);
        }
    }

    @Override
    public List<QuestionResponse> getAllQuestions() {
        log.info("전체 질문 조회 요청");

        List<Question> questions = questionRepository.findAll();

        if (questions.isEmpty()) {
            log.warn("조회된 질문이 없습니다.");
        }

        log.info("조회된 질문 수: {}", questions.size());

        return questions.stream()
                .map(questionMapper::toResponse)
                .toList();
    }
}
