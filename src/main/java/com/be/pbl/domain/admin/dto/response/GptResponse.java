package com.be.pbl.domain.admin.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(title = "GptResponse DTO", description = "GPT model로부터 받는 응답 DTO")
public class GptResponse {

    // gpt가 생성한 응답 선택지 리스트
    private List<Choice> choices;
}
