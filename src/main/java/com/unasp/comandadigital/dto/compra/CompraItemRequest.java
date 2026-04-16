package com.unasp.comandadigital.dto.compra;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CompraItemRequest(
    @NotNull Long ingredienteId,
    @NotNull @DecimalMin("0.0001") BigDecimal quantidade,
    @NotNull @DecimalMin("0.0001") BigDecimal precoUnitario
) {}
