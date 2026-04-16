package com.unasp.comandadigital.dto.prato;

import com.unasp.comandadigital.entity.enums.UnidadeMedida;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record FichaTecnicaItemRequest(
    @NotNull Long ingredienteId,
    @NotNull @DecimalMin("0.0001") BigDecimal quantidade,
    @NotNull UnidadeMedida unidade,
    @NotNull @DecimalMin(value = "1.0", message = "Fator de correção deve ser >= 1.0") BigDecimal fatorCorrecao
) {}
