package com.be.pbl.domain.exhibition.entity;

import com.be.pbl.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "exhibition")
public class Exhibition extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title; // 전시회 제목

    @Column(name = "description", columnDefinition = "TEXT")
    private String description; // 전시회 설명

    @Column(name = "address")
    private String address; // 전시회 주소

    @Column(name = "author")
    private String author; // 작가

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "open_time")
    private LocalTime openTime;

    @Column(name = "close_time")
    private LocalTime closeTime;

    // 컬렉션 어노태이션으로 tag 테이블을 만들고 매핑함 (필드로 존재x)
    @ElementCollection(targetClass = Tag.class) // 컬렉션(List, Set 등)을 별도의 테이블에 저장하기 위한 어노테이션
    @CollectionTable(name = "tag", joinColumns = @JoinColumn(name = "exhibition_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "tags")
    private List<Tag> tags; // 작품 태그

    @Column(name = "views")
    private int views; // 조회수

        @ElementCollection
    @CollectionTable(
            name = "exhibition_images",
            joinColumns = @JoinColumn(name = "exhibition_id")
    )
    @Column(name = "image_url")
    private List<String> imageUrls; // 이미지 URL 리스트

//    INSERT INTO exhibition
//            (title, operating_day, address, gallery_name, operating_hour, image_urls, artist, description)
    @Column(name = "galleryName")
    private String galleryName; // 갤러리 명

    @Column(name = "phoneNum")
    private String phoneNum; // 갤러리 전회번호
}
