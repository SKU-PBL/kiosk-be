package com.be.pbl.domain.admin.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(title = "Message DTO", description = "role과 content 요청 DTO")
public class Message {

    @JsonProperty("role")
    @Schema(description = "메세지 역할") // user, assistant, system
    private String role;

    @JsonProperty("content")
    @Schema(description = "메세지 내용")
    private String content;
}
