package com.unasp.comandadigital.dto.fornecedor;

import com.unasp.comandadigital.entity.enums.UnidadeMedida;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record FornecedorProdutoRequest(
    @NotNull Long ingredienteId,
    @NotNull @DecimalMin("0.0001") BigDecimal preco,
    @NotNull UnidadeMedida unidadeVenda
) {}
