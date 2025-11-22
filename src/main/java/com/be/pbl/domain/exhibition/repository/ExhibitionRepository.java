package com.be.pbl.domain.exhibition.repository;

import com.be.pbl.domain.exhibition.entity.Exhibition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExhibitionRepository extends JpaRepository<Exhibition, Long> {

}
