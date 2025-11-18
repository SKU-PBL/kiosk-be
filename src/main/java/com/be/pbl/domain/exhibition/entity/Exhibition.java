package com.be.pbl.domain.exhibition.entity;

import com.be.pbl.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    @Column(name = "description")
    private String description; // 전시회 설명

    @Column(name = "address")
    private String address; // 전시회 주소

    @Column(name = "author")
    private String author; // 작가

    @Column(name = "operatingHours")
    private String operatingHours; // 전시회 운영 시간

    // 컬렉션 어노태이션으로 tag 테이블을 만들고 매핑함 (필드로 존재x)
    @ElementCollection(targetClass = Tag.class) // 컬렉션(List, Set 등)을 별도의 테이블에 저장하기 위한 어노테이션
    @CollectionTable(name = "tag", joinColumns = @JoinColumn(name = "exhibition_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "tags")
    private List<Tag> tags; // 작품 태그

    @Column(name = "views")
    private String views; // 조회수

    @Column(name = "imgUrl")
    private String imgUrl; // 작품 이미지 url

    @Column(name = "galleryName")
    private String galleryName; // 갤러리 명

    @Column(name = "phoneNum")
    private String phoneNum; // 갤러리 전회번호
}
