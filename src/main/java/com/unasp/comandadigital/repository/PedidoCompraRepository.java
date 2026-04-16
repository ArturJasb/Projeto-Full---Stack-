package com.unasp.comandadigital.repository;

import com.unasp.comandadigital.entity.PedidoCompra;
import com.unasp.comandadigital.entity.enums.StatusCompra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PedidoCompraRepository extends JpaRepository<PedidoCompra, Long> {
    Page<PedidoCompra> findByStatusOrderByCreatedAtDesc(StatusCompra status, Pageable pageable);
    Page<PedidoCompra> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
