package com.be.pbl.domain.admin.exception;

import com.be.pbl.global.exception.model.BaseErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum OpenAiErrorCode implements BaseErrorCode {

    OPENAI_API_FAILED("OPENAI_001","OpenAI API 요청에 실패했습니다.",HttpStatus.BAD_REQUEST),
    ENUM_NAME_MISMATCH("OPENAI_002", "Enum과 응답 값의 이름이 일치하지 않습니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;
}
