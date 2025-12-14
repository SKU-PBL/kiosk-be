package com.be.pbl.domain.exhibition.naver;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import java.util.List;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class NaverSearchResponse {

    private int total;
    private int start;
    private int display;
    private List<Item> items;

    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Item {
        private String title;
        private String link;
        private String description;
        private String postdate; // yyyyMMdd
    }
}
