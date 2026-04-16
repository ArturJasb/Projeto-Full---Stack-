package com.unasp.comandadigital.dto.prato;

import com.unasp.comandadigital.entity.FichaTecnica;

import java.math.BigDecimal;
import java.util.List;

public record FichaTecnicaResponse(
    Long id,
    Long pratoId,
    String pratoNome,
    int rendimento,
    String modoPreparo,
    List<FichaTecnicaItemResponse> itens,
    BigDecimal custoTotal,
    BigDecimal foodCostPct,
    String alertaFoodCost
) {}
