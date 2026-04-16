package com.unasp.comandadigital.dto.pedido;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record PedidoRequest(
    @NotEmpty @Valid List<PedidoItemRequest> itens,
    String enderecoEntrega,
    String observacoes
) {}
