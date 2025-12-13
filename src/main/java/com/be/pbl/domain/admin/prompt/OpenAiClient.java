package com.be.pbl.domain.admin.prompt;

import com.be.pbl.domain.admin.dto.request.GptRequest;
import com.be.pbl.domain.admin.dto.request.Message;
import com.be.pbl.domain.admin.dto.response.GptResponse;
import com.be.pbl.domain.admin.exception.OpenAiErrorCode;
import com.be.pbl.global.exception.CustomException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OpenAiClient {

    private final RestTemplate restTemplate;

    @Value("${openai.api.model}")
    private String model;

    @Value("${openai.api.url}")
    private String url;

    @Value("${openai.api.secret-key}")
    private String secretKey;

    // 사용자 질문을 모델에 전달하고 응답 받기
    public GptResponse getChatCompletion(String prompt) {

        // 요청 구성
        GptRequest request = getGptRequest(prompt);

        /*// 디버깅: 요청 내용 로그
        log.info(
            "OpenAI 요청 - model: {}, messages size: {}",
            request.getModel(),
            request.getMessages().size());
        log.info("OpenAI URL: {}", url);*/

        /*// JSON 직렬화 확인을 위한 로그
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonRequest = mapper.writeValueAsString(request);
            log.info("실제 전송되는 JSON: {}", jsonRequest);
        } catch (Exception e) {
            log.error("JSON 직렬화 실패", e);
        }*/

        // HttpHeaders 명시적 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + secretKey);

        // HttpEntity로 요청 래핑
        HttpEntity<GptRequest> entity = new HttpEntity<>(request, headers);

        // RestTemplate을 통해 OpenAI API POST 요청 전송
        ResponseEntity<GptResponse> response =
            restTemplate.postForEntity(url, entity, GptResponse.class);

        // 응답 실패 처리
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            log.error("OpenAI API 요청 실패");
            throw new CustomException(OpenAiErrorCode.OPENAI_API_FAILED);
        }

        // 성공 시 응답 본문 반환
        return response.getBody();
    }

    // OPENAI 요청 구성 : 전시회 설명 기반 태그 3개 생성
    private GptRequest getGptRequest(String prompt) {

        // system 메세지 작성 : AI 역할 지시
        Message systemMessage =
            new Message(
                "system", "너는 전시회 설명을 분석해 아래에 나열된 태그 목록 중에서 \n" +
                "해당 전시에 가장 잘 어울리는 태그를 최대 3개까지 선택하는 전문 분류 모델이다.\n" +
                "\n" +
                "반드시 아래 태그 목록 중에서만 선택하고, 새 태그를 생성하지 않는다.\n" +
                "선택 가능한 태그 목록:\n" +
                "- MODERN(현대적) \n" +
                "- TRADITIONAL(전통적)\n" +
                "- ABSTRACT(추상적)\n" +
                "- REALISTIC(사실적)\n" +
                "- FANCY(화려한)\n" +
                "- UNDERSTATED(절제된)\n" +
                "- BRIGHT(밝은)\n" +
                "- DARK(어두운)\n" +
                "- NATURAL(자연적)\n" +
                "- ARTIFICIAL(인공적)\n" +
                "\n" +
                "선택 기준:\n" +
                "- 설명의 분위기, 색감, 표현 방식, 시대감 등을 종합적으로 판단한다.\n" +
                "- 반드시 1~3개의 태그만 선택한다.\n" +
                "- 태그 이름은 ENUM 키 값 그대로 반환한다. (예: BRIGHT, MODERN, ARTIFICIAL)" +
                "- 출력 형식은 한 줄로 태그만 출력하고 태그는 쉼표(,)로 구분한다.");

        // user 메세지 작성 : 사용자 질문
        Message userMessage = new Message("user", prompt);

        // 메세 리스트에 system -> user 순으로 담기
        List<Message> messages = List.of(systemMessage, userMessage);

        // 모델 이름과 메세지를 포함한 요청 객체 생성
        return new GptRequest(model, messages);
    }

}
