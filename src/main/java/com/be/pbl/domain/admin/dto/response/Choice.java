package com.be.pbl.domain.admin.dto.response;

import com.be.pbl.domain.admin.dto.request.Message;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Choice {

    // 선택지 인덱스
    private int index;

    // AI가 생성한 메시지
    private Message message;

    // 응답 종료 이유 (예: "stop", "length")
    @JsonProperty("finish_reason")
    private String finishReason;
}
