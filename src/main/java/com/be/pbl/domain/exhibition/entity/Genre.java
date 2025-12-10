package com.be.pbl.domain.exhibition.entity;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Genre {

    ART("미술"),
    CRAFT("공예"),
    MEDIA("사진·미디어아트"),
    TRADITION("전통문화");

    private final String description;
}
