package com.be.pbl.domain.admin.service;

import com.be.pbl.domain.admin.dto.response.GptResponse;
import com.be.pbl.domain.admin.dto.response.TagResponse;
import com.be.pbl.domain.admin.exception.OpenAiErrorCode;
import com.be.pbl.domain.admin.prompt.OpenAiClient;
import com.be.pbl.domain.exhibition.entity.Exhibition;
import com.be.pbl.domain.exhibition.entity.Tag;
import com.be.pbl.domain.exhibition.repository.ExhibitionRepository;
import com.be.pbl.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TagService {

    private final OpenAiClient openAiClient;
    private final ExhibitionRepository exhibitionRepository;

    // 전시회 설명 기반 태그 생성하여 리스트로 변환
    @Transactional
    public List<TagResponse> createTag() {
        log.info("=== 태그 생성 API 시작 ===");

        // 전체 전시회 수 조회
        List<Exhibition> allExhibitions = exhibitionRepository.findAll();
        log.info("전체 전시회 수: {}", allExhibitions.size());

        // Tag필드가 비어있거나 null인 데이터를 List로 저장
        List<Exhibition> exhibitions = allExhibitions.stream()
            .filter(e -> e.getTags() == null || e.getTags().isEmpty())
            .toList();

        log.info("태그가 없는 전시회 수: {}", exhibitions.size());

        if (exhibitions.isEmpty()) {
            log.warn("태그 생성이 필요한 전시회가 없습니다. 모든 전시회에 태그가 이미 존재합니다.");
            return new ArrayList<>();
        }

        // 응답 리스트
        List<TagResponse> responseList = new ArrayList<>();

        // 위에 저장된 exhibition 리스트 각 요소의 전시회 설명을 openAi 요청에 넣어서 보냄
        for(Exhibition exhibition : exhibitions){
            log.info("전시회 ID {} 처리 시작 (제목: {})", exhibition.getId(), exhibition.getTitle());
            String description = exhibition.getDescription();

            // openAI로 전달하여 tag 생성
            GptResponse response = openAiClient.getChatCompletion(description);

            // GPT API 응답 로깅
            log.info("=== GPT API 응답 시작 (전시회 ID: {}) ===", exhibition.getId());
            if (response.getUsage() != null) {
                log.info("토큰 사용량 - 입력: {}, 출력: {}, 총합: {}",
                    response.getUsage().getPromptTokens(),
                    response.getUsage().getCompletionTokens(),
                    response.getUsage().getTotalTokens());
            }

            // 응답 중 첫번 째 메세지의 content 추출
            String content = response.getChoices().get(0).getMessage().getContent();
            log.info("생성된 태그: {}", content);
            log.info("=== GPT API 응답 종료 ===");

            // content 파싱하여 TagResponse형식의 리스트로 반환
            List<Tag> tags = parseContent(content);

            // Exhibition 엔티티의 Tag필드 업데이트
            exhibition.updateTag(tags);

            // 응답으로 반환할 리스트에 추가
            for (Tag tag : tags) {
                responseList.add(new TagResponse(exhibition.getId(), tag, tag.getDescription()));
            }

            log.info("전시회 ID {} 처리 완료 (생성된 태그 수: {})", exhibition.getId(), tags.size());
        }

        log.info("=== 태그 생성 API 완료 (처리된 전시회: {}, 생성된 태그 응답: {}) ===",
            exhibitions.size(), responseList.size());

        return responseList;
    }

    // Enum 타입 tag로 변환하여 리스트로 반환
    private List<Tag> parseContent(String content){
        List<Tag> tags = new ArrayList<>();

        // 쉼표로 분리
        String[] resultTags = content.split(",");

        for (String tag : resultTags) {
            tag = tag.trim(); // 공백 제거

            // 쉼표 사이에 있는 빈 문자열은 로직을 거치지 않고 그냥 넘어감
            if(tag.isEmpty()){
                continue; // 그냥 넘어감
            }

            try {
                // Enum으로 타입 변경 후 응답 리스트에 추가
                Tag enumTag = Tag.valueOf(tag);
                tags.add(enumTag);
            } catch (Exception e) {
                throw new CustomException(OpenAiErrorCode.ENUM_NAME_MISMATCH);
            }
        }

        return tags;
    }
}
