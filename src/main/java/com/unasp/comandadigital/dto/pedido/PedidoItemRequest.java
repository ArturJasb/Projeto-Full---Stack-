package com.unasp.comandadigital.dto.pedido;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record PedidoItemRequest(
    @NotNull Long pratoId,
    @NotNull @Min(1) Integer quantidade,
    String observacoes
) {}
