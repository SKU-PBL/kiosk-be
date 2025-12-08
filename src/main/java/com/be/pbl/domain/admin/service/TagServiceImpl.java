package com.be.pbl.domain.admin.service;

import com.be.pbl.domain.admin.dto.response.GptResponse;
import com.be.pbl.domain.admin.dto.response.TagResponse;
import com.be.pbl.domain.admin.prompt.OpenAiClient;
import com.be.pbl.domain.exhibition.entity.Exhibition;
import com.be.pbl.domain.exhibition.repository.ExhibitionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TagServiceImpl {

    private final OpenAiClient openAiClient;
    private final ExhibitionRepository exhibitionRepository;

    // 전시회 설명 기반 태그 생성
    public TagResponse createTag() {
        // Tag필드가 비어있거나 null인 데이터를 List로 저장
        List<Exhibition> exhibitions = exhibitionRepository.findAll().stream()
            .filter(e -> e.getTags() == null || e.getTags().isEmpty())
            .toList();

        // 위에 저장된 exhibition 리스트 각 요소의 전시회 설명을 openAi 요청에 넣어서 보냄
        for(Exhibition exhibition : exhibitions){
            String description = exhibition.getDescription();

            // openAI로 전달하여 tag 생성
            GptResponse response = openAiClient.getChatCompletion(description);

            // 응답 중 첫번 째 메세지의 content 추출
            String content = response.getChoices().get(0).getMessage().getContent();

            // 응답 파싱

            // Exhibition 엔티티의 Tag필드 업데이트

        }
    }
}
