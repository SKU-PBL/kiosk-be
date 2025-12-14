package com.be.pbl.domain.exhibition.naver;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@Component
public class NaverSearch {

    @Value("${naver.search.blog.url}")
    private String blogSearchUrl;

    @Value("${naver.search.blog.client-id}")
    private String clientId;

    @Value("${naver.search.blog.client-secret}")
    private String clientSecret;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * title + galleryName으로 검색 (sort 기본: date)
     */
    public NaverSearchResponse searchByTitleAndGallery(String title, String galleryName, int start, int display) {
        return searchByTitleAndGallery(title, galleryName, start, display, "date");
    }

    /**
     * 네이버 블로그 검색 API 호출
     * - query = "title" galleryName
     * - title은 메인이므로 큰따옴표로 감싸서 검색 정확도 향상
     */
    public NaverSearchResponse searchByTitleAndGallery(
            String title, String galleryName, int start, int display, String sort) {

        String query = buildQuery(title, galleryName);

        URI uri = UriComponentsBuilder.fromHttpUrl(blogSearchUrl)
                .queryParam("query", query)
                .queryParam("sort", sort)
                .queryParam("start", start)
                .queryParam("display", display)
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", clientId);
        headers.set("X-Naver-Client-Secret", clientSecret);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        int retry = 0;
        int MAX_RETRY = 2;

        while (retry <= MAX_RETRY) {
            try {
                log.info("[NAVER] REQUEST query={}, start={}", query, start);

                ResponseEntity<String> response = restTemplate.exchange(
                        uri,
                        HttpMethod.GET,
                        new HttpEntity<>(headers),
                        String.class
                );

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    return objectMapper.readValue(response.getBody(), NaverSearchResponse.class);
                }

                return new NaverSearchResponse();

            } catch (HttpStatusCodeException e) {
                // ✅ 429면 재시도
                if (e.getStatusCode().value() == 429 && retry < MAX_RETRY) {
                    retry++;
                    log.warn("[NAVER] 429 Too Many Requests - retry {}/{}", retry, MAX_RETRY);
                   sleepSafely(500L * retry);
                    continue;
                }

                log.error("[NAVER] HTTP ERROR status={}, body={}",
                        e.getStatusCode(), truncate(e.getResponseBodyAsString(), 500));
                return new NaverSearchResponse();

            } catch (Exception e) {
                log.error("[NAVER] EXCEPTION query={}", query, e);
                return new NaverSearchResponse();
            }
        }

        return new NaverSearchResponse();
    }
    /**
     * query 규칙:
     *  - title은 메인이므로 "..."로 감싼다
     *  - galleryName은 추가 키워드로 붙인다
     *  - title 안에 큰따옴표가 이미 들어있으면 깨질 수 있으니 제거/치환
     */
    private void sleepSafely(long ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    private String buildQuery(String title, String galleryName) {
        String safeTitle = title.replace("\"", "").trim();
        String safeGallery = galleryName.trim();

        // ✅ 예:  "너의 빛, 우리의 무지개" 인사아트센터
        return "\"" + safeTitle + "\" " + safeGallery;
    }

    private String truncate(String s, int max) {
        if (s == null) return "null";
        if (s.length() <= max) return s;
        return s.substring(0, max) + "...(truncated)";
    }
}
