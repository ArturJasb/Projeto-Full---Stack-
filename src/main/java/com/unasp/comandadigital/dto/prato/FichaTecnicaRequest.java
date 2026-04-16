package com.unasp.comandadigital.dto.prato;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record FichaTecnicaRequest(
    @Min(1) int rendimento,
    String modoPreparo,
    @NotEmpty @Valid List<FichaTecnicaItemRequest> itens
) {}
