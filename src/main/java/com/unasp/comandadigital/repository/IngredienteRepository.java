package com.unasp.comandadigital.repository;

import com.unasp.comandadigital.entity.Ingrediente;
import com.unasp.comandadigital.entity.enums.StatusGeral;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface IngredienteRepository extends JpaRepository<Ingrediente, Long> {
    Page<Ingrediente> findByStatus(StatusGeral status, Pageable pageable);
    Optional<Ingrediente> findBySku(String sku);
    List<Ingrediente> findByStatus(StatusGeral status);
}
