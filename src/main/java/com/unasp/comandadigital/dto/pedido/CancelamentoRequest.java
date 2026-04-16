package com.unasp.comandadigital.dto.pedido;

import jakarta.validation.constraints.NotBlank;

public record CancelamentoRequest(@NotBlank String motivo) {}
