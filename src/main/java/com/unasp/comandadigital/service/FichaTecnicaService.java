package com.unasp.comandadigital.service;

import com.unasp.comandadigital.dto.prato.*;
import com.unasp.comandadigital.entity.*;
import com.unasp.comandadigital.entity.enums.StatusPrato;
import com.unasp.comandadigital.exception.BusinessException;
import com.unasp.comandadigital.exception.ResourceNotFoundException;
import com.unasp.comandadigital.repository.FichaTecnicaRepository;
import com.unasp.comandadigital.repository.IngredienteRepository;
import com.unasp.comandadigital.repository.PratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FichaTecnicaService {

    private final FichaTecnicaRepository fichaTecnicaRepository;
    private final PratoRepository pratoRepository;
    private final IngredienteRepository ingredienteRepository;

    public FichaTecnicaResponse buscarPorPrato(Long pratoId) {
        FichaTecnica ficha = fichaTecnicaRepository.findByPratoId(pratoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ficha técnica não encontrada para o prato " + pratoId));
        return toResponse(ficha);
    }

    public CustoResponse calcularCusto(Long pratoId) {
        Prato prato = pratoRepository.findById(pratoId)
                .orElseThrow(() -> new ResourceNotFoundException("Prato", pratoId));

        FichaTecnica ficha = fichaTecnicaRepository.findByPratoId(pratoId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Ficha técnica não encontrada para o prato " + pratoId));

        BigDecimal custo = calcularCustoTotal(ficha);
        BigDecimal foodCost = custo.divide(prato.getPrecoVenda(), 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));

        String classificacao = classificarFoodCost(foodCost);

        return new CustoResponse(prato.getId(), prato.getNome(),
                prato.getPrecoVenda(), custo, foodCost, classificacao);
    }

    @Transactional
    public FichaTecnicaResponse salvar(Long pratoId, FichaTecnicaRequest request) {
        Prato prato = pratoRepository.findById(pratoId)
                .orElseThrow(() -> new ResourceNotFoundException("Prato", pratoId));

        FichaTecnica ficha = fichaTecnicaRepository.findByPratoId(pratoId)
                .orElse(FichaTecnica.builder().prato(prato).itens(new ArrayList<>()).build());

        ficha.setRendimento(request.rendimento());
        ficha.setModoPreparo(request.modoPreparo());
        ficha.getItens().clear();

        for (FichaTecnicaItemRequest itemReq : request.itens()) {
            Ingrediente ingrediente = ingredienteRepository.findById(itemReq.ingredienteId())
                    .orElseThrow(() -> new ResourceNotFoundException("Ingrediente", itemReq.ingredienteId()));

            // RN08: fator de correção >= 1.0 (validado no DTO, mas garantia extra)
            if (itemReq.fatorCorrecao().compareTo(BigDecimal.ONE) < 0) {
                throw new BusinessException("Fator de correção deve ser >= 1.0 para: " + ingrediente.getNome());
            }

            FichaTecnicaItem item = FichaTecnicaItem.builder()
                    .fichaTecnica(ficha)
                    .ingrediente(ingrediente)
                    .quantidade(itemReq.quantidade())
                    .unidade(itemReq.unidade())
                    .fatorCorrecao(itemReq.fatorCorrecao())
                    .build();

            ficha.getItens().add(item);
        }

        FichaTecnica saved = fichaTecnicaRepository.save(ficha);

        // RN01: ativa o prato automaticamente se tinha ficha
        if (prato.getStatus() == StatusPrato.INATIVO && !saved.getItens().isEmpty()) {
            prato.setStatus(StatusPrato.ATIVO);
            pratoRepository.save(prato);
        }

        return toResponse(saved);
    }

    // Fórmula: custo = SUM(qtd * fator_correcao * custo_unitario) / rendimento
    public BigDecimal calcularCustoTotal(FichaTecnica ficha) {
        BigDecimal soma = ficha.getItens().stream()
                .map(item -> item.getQuantidade()
                        .multiply(item.getFatorCorrecao())
                        .multiply(item.getIngrediente().getCustoUnitario()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return soma.divide(BigDecimal.valueOf(ficha.getRendimento()), 4, RoundingMode.HALF_UP);
    }

    private String classificarFoodCost(BigDecimal foodCost) {
        if (foodCost.compareTo(BigDecimal.valueOf(30)) <= 0) return "VERDE";
        if (foodCost.compareTo(BigDecimal.valueOf(35)) <= 0) return "AMARELO";
        return "VERMELHO";
    }

    private FichaTecnicaResponse toResponse(FichaTecnica ficha) {
        List<FichaTecnicaItemResponse> itensResp = ficha.getItens().stream()
                .map(FichaTecnicaItemResponse::from).toList();

        BigDecimal custo = calcularCustoTotal(ficha);
        BigDecimal foodCost = BigDecimal.ZERO;

        if (ficha.getPrato().getPrecoVenda().compareTo(BigDecimal.ZERO) > 0) {
            foodCost = custo.divide(ficha.getPrato().getPrecoVenda(), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        String alerta = classificarFoodCost(foodCost);

        // RN02: food cost > 35% retorna aviso
        String mensagemAlerta = foodCost.compareTo(BigDecimal.valueOf(35)) > 0
                ? "⚠️ Food cost acima de 35%! Revise os custos ou o preço de venda."
                : null;

        return new FichaTecnicaResponse(
                ficha.getId(),
                ficha.getPrato().getId(),
                ficha.getPrato().getNome(),
                ficha.getRendimento(),
                ficha.getModoPreparo(),
                itensResp,
                custo,
                foodCost.setScale(2, RoundingMode.HALF_UP),
                alerta + (mensagemAlerta != null ? " | " + mensagemAlerta : "")
        );
    }
}
