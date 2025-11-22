package com.be.pbl.domain.exhibition.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum Category {
    ERA("시대감", Tag.MODERN, Tag.TRADITIONAL),
    EXPRESSION("표현방식", Tag.ABSTRACT, Tag.REALISTIC),
    MOOD("분위기", Tag.FANCY, Tag.UNDERSTATED),
    COLOR("색감", Tag.BRIGHT, Tag.DARK),
    NATURE("자연성", Tag.NATURAL, Tag.ARTIFICIAL);
    // SOURCE
    private final String description;
    private final Tag left;
    private final Tag right;
}
