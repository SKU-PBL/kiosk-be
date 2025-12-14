// src/main/java/com/be/pbl/domain/question/entity/Question.java
package com.be.pbl.domain.question.entity;

import com.be.pbl.domain.exhibition.entity.Category;
import com.be.pbl.domain.exhibition.entity.Tag;
import com.be.pbl.global.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "question")
public class Question extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ERA, EXPRESSION, ... (Category enum 그대로 사용)
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Category category;

    // 화면 상단에 나오는 질문 문구
    @Column(name = "content", nullable = false, length = 50)
    private String content;

    // 왼쪽 이미지 URL
    @Column(name = "left_image_url")
    private String leftImageUrl;

    // 오른쪽 이미지 URL
    @Column(name = "right_image_url")
    private String rightImageUrl;

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setRightImageUrl(String rightImageUrl) {
        this.rightImageUrl = rightImageUrl;
    }

    public void setLeftImageUrl(String leftImageUrl) {
        this.leftImageUrl = leftImageUrl;
    }
}
