package com.unasp.comandadigital.dto.estoque;

import com.unasp.comandadigital.entity.enums.UnidadeMedida;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record IngredienteRequest(
    @NotBlank String nome,
    String sku,
    @NotNull UnidadeMedida unidadePadrao,
    @DecimalMin("0.0") BigDecimal estoqueMinimo,
    @DecimalMin("0.0") BigDecimal custoUnitario
) {}
