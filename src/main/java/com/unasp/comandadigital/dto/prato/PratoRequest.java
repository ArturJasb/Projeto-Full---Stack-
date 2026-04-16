package com.unasp.comandadigital.dto.prato;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record PratoRequest(
    @NotBlank String nome,
    String descricao,
    String fotoUrl,
    @NotNull @DecimalMin("0.01") BigDecimal precoVenda,
    Integer tempoPreparoMin,
    @NotNull Long categoriaId
) {}
