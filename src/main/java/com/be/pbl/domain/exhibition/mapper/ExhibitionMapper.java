package com.be.pbl.domain.exhibition.mapper;

import com.be.pbl.domain.exhibition.dto.response.ExhibitionInfoResponse;
import com.be.pbl.domain.exhibition.dto.response.ExhibitionTag;
import com.be.pbl.domain.exhibition.entity.Exhibition;
import com.be.pbl.domain.exhibition.entity.Tag;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ExhibitionMapper {

    public ExhibitionInfoResponse toExhibitionResponse(Exhibition exhibition){
        return ExhibitionInfoResponse.builder()
            .id(exhibition.getId())
            .title(exhibition.getTitle())
            .address(exhibition.getAddress())
            .author(exhibition.getAuthor())
            .startDate(exhibition.getStartDate())
            .endDate(exhibition.getEndDate())
            .openTime(exhibition.getOpenTime())
            .closeTime(exhibition.getCloseTime())
            .tags(toExhibitionTags(exhibition.getTags()))
            .build();
    }

    private List<ExhibitionTag> toExhibitionTags(List<Tag> tags){
        return tags.stream()
            .map(tag -> ExhibitionTag.builder()
                .tagName(tag.name())
                .tagDescription(tag.getDescription())
                .build())
            .collect(Collectors.toList());
    }
    public List<ExhibitionInfoResponse> toRecommendResponses(List<Exhibition> exhibitions) {
        return exhibitions.stream()
                .map(this::toExhibitionResponse)
                .toList();
    }
}
