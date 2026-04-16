package com.unasp.comandadigital.dto.estoque;

import com.unasp.comandadigital.entity.enums.MotivoMovimentacao;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record MovimentacaoRequest(
    @NotNull Long ingredienteId,
    @NotNull @DecimalMin("0.0001") BigDecimal quantidade,
    @NotNull MotivoMovimentacao motivo,
    String lote,
    LocalDate validade
) {}
