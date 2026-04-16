package com.unasp.comandadigital.repository;

import com.unasp.comandadigital.entity.EstoqueMovimentacao;
import com.unasp.comandadigital.entity.enums.TipoMovimentacao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface EstoqueMovimentacaoRepository extends JpaRepository<EstoqueMovimentacao, Long> {

    Page<EstoqueMovimentacao> findByIngredienteIdOrderByCreatedAtDesc(Long ingredienteId, Pageable pageable);

    @Query("SELECT COALESCE(SUM(CASE WHEN m.tipo = 'ENTRADA' THEN m.quantidade " +
           "WHEN m.tipo IN ('SAIDA', 'ESTORNO') THEN -m.quantidade ELSE 0 END), 0) " +
           "FROM EstoqueMovimentacao m WHERE m.ingrediente.id = :ingredienteId")
    BigDecimal calcularSaldo(@Param("ingredienteId") Long ingredienteId);

    @Query("SELECT pi.prato.id, pi.prato.nome, SUM(pi.quantidade) as total " +
           "FROM PedidoItem pi " +
           "WHERE pi.pedido.status != com.unasp.comandadigital.entity.enums.StatusPedido.CANCELADO " +
           "GROUP BY pi.prato.id, pi.prato.nome " +
           "ORDER BY total DESC")
    List<Object[]> findTopPratosMaisVendidos(Pageable pageable);
}
