package com.be.pbl.domain.exhibition.exception;

import com.be.pbl.global.exception.model.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ExhibitionErrorCode implements BaseErrorCode {

    EXHIBITION_LIST_EMPTY("EXHIBITION_002", "전시회 목록이 비어있습니다.", HttpStatus.NO_CONTENT),
    EXHIBITION_NOT_FOUND("EXHIBITION_001", "전시회를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    EXHIBITION_BY_GENRE_EMPTY("EXHIBITION_003", "해당 장르에 대한 전시회가 존재하지 않습니다.", HttpStatus.NO_CONTENT);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
