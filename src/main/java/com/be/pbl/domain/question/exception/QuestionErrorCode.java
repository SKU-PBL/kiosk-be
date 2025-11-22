package com.be.pbl.domain.question.exception;

import com.be.pbl.global.exception.model.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum QuestionErrorCode implements BaseErrorCode {

    QUESTION_NOT_FOUND("QUESTION_001", "질문을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);


    private final String code;
    private final String message;
    private final HttpStatus status;
}
