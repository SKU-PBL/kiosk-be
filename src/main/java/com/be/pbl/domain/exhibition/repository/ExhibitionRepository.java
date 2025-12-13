package com.be.pbl.domain.exhibition.repository;

import com.be.pbl.domain.exhibition.entity.Exhibition;
import com.be.pbl.domain.exhibition.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExhibitionRepository extends JpaRepository<Exhibition, Long> {
    List<Exhibition> findByGenre(Genre genre);
    List<Exhibition> findAllByOrderByNaverCountDesc();
}
