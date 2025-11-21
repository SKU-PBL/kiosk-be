package com.be.pbl.domain.question.service;

import com.be.pbl.domain.exhibition.entity.Category;
import com.be.pbl.domain.question.dto.request.QuestionCreateRequest;
import com.be.pbl.domain.question.dto.response.QuestionResponse;
import com.be.pbl.domain.question.entity.Question;
import com.be.pbl.domain.question.exception.QuestionErrorCode;
import com.be.pbl.domain.question.mapper.QuestionMapper;
import com.be.pbl.domain.question.repository.QuestionRepository;
import com.be.pbl.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;

    @Override
    public List<QuestionResponse> getAllQuestions() {
        log.info("질문 전체 조회 요청 - 카테고리별 랜덤 1개씩");

        List<QuestionResponse> result = new ArrayList<>();

        for (Category category : Category.values()) {
            List<Question> questions = questionRepository.findByCategory(category);

            if (questions.isEmpty()) {
                log.warn("카테고리 {} 에 해당하는 질문이 없습니다.", category);
                continue; // 없으면 스킵 (원하면 여기서 예외 던져도 됨)
            }

            // 랜덤 1개 선택
            Question picked = questions.get(
                    ThreadLocalRandom.current().nextInt(questions.size())
            );

            result.add(questionMapper.toResponse(picked));
        }

        if (result.isEmpty()) {
            log.error("모든 카테고리에서 질문을 찾지 못했습니다.");
            throw new CustomException(QuestionErrorCode.QUESTION_NOT_FOUND);
        }

        return result;
    }

    @Override
    public QuestionResponse getQuestion(Long id) {
        try {
            log.info("질문 단일 조회 요청 id={}", id);

            Question question = questionRepository.findById(id)
                    .orElseThrow(() -> new CustomException(QuestionErrorCode.QUESTION_NOT_FOUND));

            return questionMapper.toResponse(question);

        } catch (CustomException e) {
            throw e; // 이미 도메인 예외이므로 그대로 던짐
        } catch (Exception e) {
            log.error("질문 단일 조회 실패 id={}", id, e);
            throw new CustomException(QuestionErrorCode.QUESTION_NOT_FOUND);
        }
    }

    @Override
    @Transactional
    public QuestionResponse createQuestion(QuestionCreateRequest request) {
        try {
            log.info("질문 생성 요청: {}", request.getContent());

            Question question = questionMapper.toEntity(request);
            Question saved = questionRepository.save(question);

            return questionMapper.toResponse(saved);

        } catch (Exception e) {
            log.error("질문 생성 실패", e);
            throw new CustomException(QuestionErrorCode.QUESTION_NOT_FOUND);
        }
    }
}
