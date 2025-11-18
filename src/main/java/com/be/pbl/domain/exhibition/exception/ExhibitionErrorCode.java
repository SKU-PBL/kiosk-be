package com.be.pbl.domain.exhibition.exception;

import com.be.pbl.global.exception.model.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExhibitionErrorCode implements BaseErrorCode {

    EXHIBITION_NOT_FOUND("EXHIBITION_001", "전시회를 찾을 수 없습니다.", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus status;}
