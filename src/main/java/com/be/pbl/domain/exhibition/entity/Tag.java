package com.be.pbl.domain.exhibition.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Tag {

    // 시대감
    MODERN("현대적"),
    TRADITIONAL("전통적"),

    // 표현 방식
    ABSTRACT("추상적"),
    REALISTIC("사실적"),

    // 분위기
    FANCY("화려한"),
    UNDERSTATED("절제된"),

    // 색감
    BRIGHT("밝은"),
    DARK("어두운"),

    // 자연성
    NATURAL("자연적"),
    ARTIFICIAL("인공적");

    private final String description;
}
