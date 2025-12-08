package com.be.pbl.domain.admin.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(title = "Usage DTO", description = "GPT API 토큰 사용량 정보")
public class UsageToken {

    @JsonProperty("prompt_tokens")
    @Schema(description = "입력(프롬프트) 토큰 수")
    private int promptTokens;

    @JsonProperty("completion_tokens")
    @Schema(description = "출력(완성) 토큰 수")
    private int completionTokens;

    @JsonProperty("total_tokens")
    @Schema(description = "총 토큰 수 (입력 + 출력)")
    private int totalTokens;
}