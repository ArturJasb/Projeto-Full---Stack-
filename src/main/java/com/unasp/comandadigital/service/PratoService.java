package com.unasp.comandadigital.service;
import org.springframework.transaction.annotation.Transactional;
import com.unasp.comandadigital.dto.prato.PratoRequest;
import com.unasp.comandadigital.dto.prato.PratoResponse;
import com.unasp.comandadigital.entity.Categoria;
import com.unasp.comandadigital.entity.Prato;
import com.unasp.comandadigital.entity.enums.StatusPrato;
import com.unasp.comandadigital.exception.BusinessException;
import com.unasp.comandadigital.exception.ResourceNotFoundException;
import com.unasp.comandadigital.repository.FichaTecnicaRepository;
import com.unasp.comandadigital.repository.PratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PratoService {

    private final PratoRepository pratoRepository;
    private final CategoriaService categoriaService;
    private final FichaTecnicaRepository fichaTecnicaRepository;

    // RN09: Cardápio público só exibe pratos ATIVOS

@Transactional(readOnly = true)
    public List<PratoResponse> listarAtivosParaCardapio(Long categoriaId) {
        return pratoRepository.findAtivosComFiltro(StatusPrato.ATIVO, categoriaId)
                .stream().map(PratoResponse::from).toList();
    }

    public Page<PratoResponse> listarAdmin(Pageable pageable) {
        return pratoRepository.findAll(pageable).map(PratoResponse::from);
    }

    public PratoResponse buscarPorId(Long id) {
        return PratoResponse.from(buscar(id));
    }

    @Transactional
    public PratoResponse criar(PratoRequest request) {
        Categoria categoria = categoriaService.buscar(request.categoriaId());

        Prato prato = Prato.builder()
                .nome(request.nome())
                .descricao(request.descricao())
                .fotoUrl(request.fotoUrl())
                .precoVenda(request.precoVenda())
                .tempoPreparoMin(request.tempoPreparoMin())
                .categoria(categoria)
                .status(StatusPrato.INATIVO)  // Inativo até ter ficha técnica (RN01)
                .build();

        return PratoResponse.from(pratoRepository.save(prato));
    }

    @Transactional
    public PratoResponse atualizar(Long id, PratoRequest request) {
        Prato prato = buscar(id);
        Categoria categoria = categoriaService.buscar(request.categoriaId());

        prato.setNome(request.nome());
        prato.setDescricao(request.descricao());
        prato.setFotoUrl(request.fotoUrl());
        prato.setPrecoVenda(request.precoVenda());
        prato.setTempoPreparoMin(request.tempoPreparoMin());
        prato.setCategoria(categoria);

        return PratoResponse.from(pratoRepository.save(prato));
    }

    @Transactional
    public PratoResponse alterarStatus(Long id, StatusPrato novoStatus) {
        Prato prato = buscar(id);

        // RN01: prato só pode ser ATIVO se tiver ficha técnica
        if (novoStatus == StatusPrato.ATIVO) {
            boolean temFicha = fichaTecnicaRepository.findByPratoId(id)
                    .map(f -> !f.getItens().isEmpty())
                    .orElse(false);
            if (!temFicha) {
                throw new BusinessException(
                        "Prato não pode ser ativado sem ficha técnica. Cadastre pelo menos 1 ingrediente.");
            }
        }

        prato.setStatus(novoStatus);
        return PratoResponse.from(pratoRepository.save(prato));
    }

    @Transactional
    public void desativar(Long id) {
        Prato prato = buscar(id);
        prato.setStatus(StatusPrato.INATIVO);
        pratoRepository.save(prato);
    }

    public Prato buscar(Long id) {
        return pratoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prato", id));
    }
}
