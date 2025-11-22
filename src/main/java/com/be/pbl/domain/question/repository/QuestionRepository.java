package com.be.pbl.domain.question.repository;

import com.be.pbl.domain.exhibition.entity.Category;
import com.be.pbl.domain.question.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    // 질문 순서대로 정렬해서 전부 가져오기
    List<Question> findByCategory(Category category);
}
