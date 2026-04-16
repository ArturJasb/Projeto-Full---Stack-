package com.unasp.comandadigital.repository;

import com.unasp.comandadigital.entity.FornecedorProduto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FornecedorProdutoRepository extends JpaRepository<FornecedorProduto, Long> {
    List<FornecedorProduto> findByFornecedorId(Long fornecedorId);

    @Query("SELECT fp FROM FornecedorProduto fp JOIN FETCH fp.fornecedor " +
           "WHERE fp.ingrediente.id = :ingredienteId " +
           "AND fp.fornecedor.status = com.unasp.comandadigital.entity.enums.StatusGeral.ATIVO " +
           "ORDER BY fp.preco ASC")
    List<FornecedorProduto> findCotacaoPorIngrediente(@Param("ingredienteId") Long ingredienteId);

    Optional<FornecedorProduto> findByFornecedorIdAndIngredienteId(Long fornecedorId, Long ingredienteId);
}
