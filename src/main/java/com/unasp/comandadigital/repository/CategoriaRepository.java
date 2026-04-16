package com.unasp.comandadigital.repository;

import com.unasp.comandadigital.entity.Categoria;
import com.unasp.comandadigital.entity.enums.StatusGeral;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    List<Categoria> findByStatusOrderByOrdemAsc(StatusGeral status);
    Page<Categoria> findAll(Pageable pageable);
}
