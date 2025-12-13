package com.be.pbl.domain.exhibition.exception;

import com.be.pbl.global.exception.model.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExhibitionErrorCode implements BaseErrorCode {

    EXHIBITION_LIST_EMPTY("EXHIBITION_002", "전시회 목록이 비어있습니다.", HttpStatus.NOT_FOUND),
    EXHIBITION_NOT_FOUND("EXHIBITION_001", "전시회를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    EXHIBITION_BY_GENRE_EMPTY("EXHIBITION_003", "해당 장르에 대한 전시회가 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    EXHIBITION_FOR_TAG_EMPTY("EXHIBITION_004", "태그를 생성할 전시회가 존재하지 않습니다. 모든 전시회에 태그가 이미 존재하거나 전시회 설명이 없습니다.", HttpStatus.NOT_FOUND);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
