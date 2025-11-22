package com.be.pbl.domain.question.mapper;

import com.be.pbl.domain.question.dto.request.QuestionCreateRequest;
import com.be.pbl.domain.question.dto.response.QuestionResponse;
import com.be.pbl.domain.question.entity.Question;
import org.springframework.stereotype.Component;

@Component
public class QuestionMapper {

    public QuestionResponse toResponse(Question question) {
        return QuestionResponse.builder()
                .id(question.getId())
                .category(question.getCategory())
                .content(question.getContent())
                .leftTag(question.getCategory().getLeft().getDescription())
                .rightTag(question.getCategory().getRight().getDescription())
                .leftImageUrl(question.getLeftImageUrl())
                .rightImageUrl(question.getRightImageUrl())
                .build();
    }
    public Question toEntity(QuestionCreateRequest request) {
        return Question.builder()
                .content(request.getContent())
                .category(request.getCategory())
                .leftImageUrl(request.getLeftImageUrl())
                .rightImageUrl(request.getRightImageUrl())
                .build();
    }

}

