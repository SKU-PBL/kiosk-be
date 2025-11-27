package com.be.pbl.domain.question.exception;

import com.be.pbl.global.exception.model.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum QuestionErrorCode implements BaseErrorCode {

    QUESTION_NOT_FOUND("QUESTION_001", "질문을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    QUESTION_NOT_ENOUGH_FOR_RANDOM("QUESTION_002", "랜덤 추출에 필요한 질문이 부족합니다.", HttpStatus.BAD_REQUEST),
    QUESTION_CREATE_FAILED("QUESTION_004", "질문 생성에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);


    private final String code;
    private final String message;
    private final HttpStatus status;
}
