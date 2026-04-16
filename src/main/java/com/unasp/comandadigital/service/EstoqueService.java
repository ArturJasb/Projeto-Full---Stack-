package com.unasp.comandadigital.service;

import com.unasp.comandadigital.dto.estoque.MovimentacaoRequest;
import com.unasp.comandadigital.dto.estoque.MovimentacaoResponse;
import com.unasp.comandadigital.entity.*;
import com.unasp.comandadigital.entity.enums.MotivoMovimentacao;
import com.unasp.comandadigital.entity.enums.TipoMovimentacao;
import com.unasp.comandadigital.exception.BusinessException;
import com.unasp.comandadigital.exception.EstoqueInsuficienteException;
import com.unasp.comandadigital.exception.ResourceNotFoundException;
import com.unasp.comandadigital.repository.EstoqueMovimentacaoRepository;
import com.unasp.comandadigital.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class EstoqueService {

    private final EstoqueMovimentacaoRepository movimentacaoRepository;
    private final IngredienteService ingredienteService;
    private final UsuarioRepository usuarioRepository;

    // Conveniente para controllers que só têm o email do principal
    @Transactional
    public MovimentacaoResponse registrarSaidaManualPorEmail(MovimentacaoRequest request, String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        return registrarSaidaManual(request, usuario);
    }

    public Page<MovimentacaoResponse> listarPorIngrediente(Long ingredienteId, Pageable pageable) {
        return movimentacaoRepository
                .findByIngredienteIdOrderByCreatedAtDesc(ingredienteId, pageable)
                .map(MovimentacaoResponse::from);
    }

    @Transactional
    public MovimentacaoResponse registrarSaidaManual(MovimentacaoRequest request, Usuario usuario) {
        MotivoMovimentacao motivo = request.motivo();
        if (motivo == MotivoMovimentacao.COMPRA || motivo == MotivoMovimentacao.VENDA
                || motivo == MotivoMovimentacao.ESTORNO) {
            throw new BusinessException("Motivo inválido para saída manual. Use: DESPERDICIO, VENCIMENTO, QUEBRA ou USO_INTERNO");
        }

        Ingrediente ingrediente = ingredienteService.buscar(request.ingredienteId());
        BigDecimal saldo = ingredienteService.getSaldo(ingrediente.getId());

        if (saldo.compareTo(request.quantidade()) < 0) {
            throw new EstoqueInsuficienteException(ingrediente.getNome(),
                    saldo.doubleValue(), request.quantidade().doubleValue());
        }

        EstoqueMovimentacao mov = EstoqueMovimentacao.builder()
                .ingrediente(ingrediente)
                .tipo(TipoMovimentacao.SAIDA)
                .quantidade(request.quantidade())
                .motivo(motivo)
                .lote(request.lote())
                .validade(request.validade())
                .usuario(usuario)
                .build();

        return MovimentacaoResponse.from(movimentacaoRepository.save(mov));
    }

    // Usado internamente para baixa automática ao confirmar pedido
    @Transactional
    public void registrarBaixaPorPedido(Ingrediente ingrediente, BigDecimal quantidade,
                                         Pedido pedido, Usuario usuario) {
        BigDecimal saldo = ingredienteService.getSaldo(ingrediente.getId());

        if (saldo.compareTo(quantidade) < 0) {
            throw new EstoqueInsuficienteException(ingrediente.getNome(),
                    saldo.doubleValue(), quantidade.doubleValue());
        }

        EstoqueMovimentacao mov = EstoqueMovimentacao.builder()
                .ingrediente(ingrediente)
                .tipo(TipoMovimentacao.SAIDA)
                .quantidade(quantidade)
                .motivo(MotivoMovimentacao.VENDA)
                .pedido(pedido)
                .usuario(usuario)
                .custoUnitario(ingrediente.getCustoUnitario())
                .build();

        movimentacaoRepository.save(mov);
    }

    // Estorno ao cancelar pedido (RN04)
    @Transactional
    public void registrarEstornoPorPedido(Ingrediente ingrediente, BigDecimal quantidade,
                                           Pedido pedido, Usuario usuario) {
        EstoqueMovimentacao mov = EstoqueMovimentacao.builder()
                .ingrediente(ingrediente)
                .tipo(TipoMovimentacao.ESTORNO)
                .quantidade(quantidade)
                .motivo(MotivoMovimentacao.ESTORNO)
                .pedido(pedido)
                .usuario(usuario)
                .custoUnitario(ingrediente.getCustoUnitario())
                .build();

        movimentacaoRepository.save(mov);
    }

    // Entrada ao receber compra
    @Transactional
    public void registrarEntradaPorCompra(Ingrediente ingrediente, BigDecimal quantidade,
                                           BigDecimal custoUnitario, PedidoCompra compra,
                                           String lote, Usuario usuario) {
        EstoqueMovimentacao mov = EstoqueMovimentacao.builder()
                .ingrediente(ingrediente)
                .tipo(TipoMovimentacao.ENTRADA)
                .quantidade(quantidade)
                .motivo(MotivoMovimentacao.COMPRA)
                .pedidoCompra(compra)
                .custoUnitario(custoUnitario)
                .lote(lote)
                .usuario(usuario)
                .build();

        movimentacaoRepository.save(mov);
    }
}
