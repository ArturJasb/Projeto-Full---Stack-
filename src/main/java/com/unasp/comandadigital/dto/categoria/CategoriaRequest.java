package com.unasp.comandadigital.dto.categoria;

import jakarta.validation.constraints.NotBlank;

public record CategoriaRequest(
    @NotBlank String nome,
    String descricao,
    Integer ordem
) {}
