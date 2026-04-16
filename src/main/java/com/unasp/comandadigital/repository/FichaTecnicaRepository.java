package com.unasp.comandadigital.repository;

import com.unasp.comandadigital.entity.FichaTecnica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FichaTecnicaRepository extends JpaRepository<FichaTecnica, Long> {
    Optional<FichaTecnica> findByPratoId(Long pratoId);
    boolean existsByPratoId(Long pratoId);
}
