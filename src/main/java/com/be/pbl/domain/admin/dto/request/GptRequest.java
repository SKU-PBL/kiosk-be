package com.be.pbl.domain.admin.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(title = "GPTRequest DTO", description = "GPT model로 보내는 요청 DTO")
public class GptRequest {

    @JsonProperty("model")
    @Schema(description = "사용할 모델명")
    private String model;

    @JsonProperty("messages")
    @Schema(description = "메세지 리스트")
    private List<Message> messages;
}
