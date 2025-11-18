package com.be.pbl.domain.exhibition.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
    ERA(
        "시대감",
        "어떤 시대감의 작품을 선호하시나요?"
    ),
    EXPRESSION(
        "표현방식",
        "어떤 표현방식의 작품을 선호하시나요?"
    ),
    MOOD(
        "분위기",
        "어떤 분위기의 작품을 선호하시나요?"
    ),
    COLOR(
        "색감",
        "어떤 색감의 작품을 선호하시나요?"
    ),
    NATURE(
        "자연성",
        "어떤 자연성의 작품을 선호하시나요?"
    );

    private final String description;
    private final String question;
}
