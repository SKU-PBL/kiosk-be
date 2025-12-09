package com.be.pbl.domain.exhibition.mapper;

import com.be.pbl.domain.exhibition.dto.request.ExhibitionPatchRequest;
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
            .description(exhibition.getDescription())
            .address(exhibition.getAddress())
            .author(exhibition.getAuthor())
            .startDate(exhibition.getStartDate())
            .endDate(exhibition.getEndDate())
            .openTime(exhibition.getOpenTime())
            .closeTime(exhibition.getCloseTime())
            //.operatingNotice(exhibition.getOperatingNotice()
            .tags(toExhibitionTags(exhibition.getTags()))
            .views(exhibition.getViews())
            .imagesUrls(exhibition.getImageUrls())
            .galleryName(exhibition.getGalleryName())
            .phoneNum(exhibition.getPhoneNum())
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
    public void updateFromPatch(Exhibition exhibition, ExhibitionPatchRequest request) {
        if (request.getTitle() != null) exhibition.setTitle(request.getTitle());
        if (request.getDescription() != null) exhibition.setDescription(request.getDescription());
        if (request.getAuthor() != null) exhibition.setAuthor(request.getAuthor());
        if (request.getStartDate() != null) exhibition.setStartDate(request.getStartDate());
        if (request.getEndDate() != null) exhibition.setEndDate(request.getEndDate());
        if (request.getOpenTime() != null) exhibition.setOpenTime(request.getOpenTime());
        if (request.getCloseTime() != null) exhibition.setCloseTime(request.getCloseTime());
        if (request.getTags() != null) exhibition.setTags(request.getTags());
        if (request.getImageUrls() != null) exhibition.setImageUrls(request.getImageUrls());
        if (request.getGenre() != null) exhibition.setGenre(request.getGenre());
        if (request.getAddress() != null) exhibition.setAddress(request.getAddress());
        if (request.getGalleryName() != null) exhibition.setGalleryName(request.getGalleryName());
        if (request.getPhoneNum() != null) exhibition.setPhoneNum(request.getPhoneNum());
    }

}
