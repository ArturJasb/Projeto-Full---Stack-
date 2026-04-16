package com.unasp.comandadigital.repository;

import com.unasp.comandadigital.entity.Usuario;
import com.unasp.comandadigital.entity.enums.StatusGeral;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
    Page<Usuario> findByStatus(StatusGeral status, Pageable pageable);
}
