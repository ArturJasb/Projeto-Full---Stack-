package com.unasp.comandadigital.service;

import com.unasp.comandadigital.dto.pedido.*;
import com.unasp.comandadigital.entity.*;
import com.unasp.comandadigital.entity.enums.Perfil;
import com.unasp.comandadigital.entity.enums.StatusPedido;
import com.unasp.comandadigital.entity.enums.StatusPrato;
import com.unasp.comandadigital.exception.BusinessException;
import com.unasp.comandadigital.exception.EstoqueInsuficienteException;
import com.unasp.comandadigital.exception.ResourceNotFoundException;
import com.unasp.comandadigital.repository.FichaTecnicaRepository;
import com.unasp.comandadigital.repository.PedidoRepository;
import com.unasp.comandadigital.repository.PratoRepository;
import com.unasp.comandadigital.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final PratoRepository pratoRepository;
    private final FichaTecnicaRepository fichaTecnicaRepository;
    private final UsuarioRepository usuarioRepository;
    private final EstoqueService estoqueService;
    private final IngredienteService ingredienteService;

    @Transactional
    public PedidoResponse criar(PedidoRequest request, String emailCliente) {
        Usuario cliente = usuarioRepository.findByEmail(emailCliente)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        List<PedidoItem> itens = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (PedidoItemRequest itemReq : request.itens()) {
            Prato prato = pratoRepository.findById(itemReq.pratoId())
                    .orElseThrow(() -> new ResourceNotFoundException("Prato", itemReq.pratoId()));

            // RN09: apenas pratos ativos
            if (prato.getStatus() != StatusPrato.ATIVO) {
                throw new BusinessException("Prato indisponível: " + prato.getNome());
            }

            // RN03: verificar estoque antes de criar o pedido
            validarEstoque(prato, itemReq.quantidade());

            PedidoItem item = PedidoItem.builder()
                    .prato(prato)
                    .quantidade(itemReq.quantidade())
                    .precoUnitario(prato.getPrecoVenda())
                    .observacoes(itemReq.observacoes())
                    .build();

            itens.add(item);
            total = total.add(prato.getPrecoVenda().multiply(BigDecimal.valueOf(itemReq.quantidade())));
        }

        Pedido pedido = Pedido.builder()
                .cliente(cliente)
                .status(StatusPedido.RECEBIDO)
                .valorTotal(total)
                .enderecoEntrega(request.enderecoEntrega() != null
                        ? request.enderecoEntrega() : cliente.getEndereco())
                .observacoes(request.observacoes())
                .itens(new ArrayList<>())
                .build();

        Pedido saved = pedidoRepository.save(pedido);

        for (PedidoItem item : itens) {
            item.setPedido(saved);
            saved.getItens().add(item);
        }

        return PedidoResponse.from(pedidoRepository.save(saved));
    }

    public Page<PedidoResponse> listarMeusPedidos(String emailCliente, Pageable pageable) {
        Usuario cliente = usuarioRepository.findByEmail(emailCliente)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        return pedidoRepository.findByClienteIdOrderByCreatedAtDesc(cliente.getId(), pageable)
                .map(PedidoResponse::from);
    }

    public PedidoResponse buscarStatus(Long pedidoId, String emailCliente) {
        Pedido pedido = buscar(pedidoId);
        if (!pedido.getCliente().getEmail().equals(emailCliente)) {
            throw new AccessDeniedException("Acesso negado a este pedido");
        }
        return PedidoResponse.from(pedido);
    }

    public Page<PedidoResponse> listarTodos(StatusPedido status, LocalDateTime dataInicio,
                                             LocalDateTime dataFim, Pageable pageable) {
        return pedidoRepository.findWithFilters(status, dataInicio, dataFim, pageable)
                .map(PedidoResponse::from);
    }

    public PedidoResponse buscarPorId(Long id) {
        return PedidoResponse.from(buscar(id));
    }

    @Transactional
    public PedidoResponse alterarStatus(Long pedidoId, StatusUpdateRequest request, String emailUsuario) {
        Pedido pedido = buscar(pedidoId);
        StatusPedido novoStatus = request.status();
        StatusPedido statusAtual = pedido.getStatus();

        validarTransicaoStatus(statusAtual, novoStatus);

        // RF-17 / RN: baixa automática ao CONFIRMAR
        if (novoStatus == StatusPedido.CONFIRMADO && statusAtual == StatusPedido.RECEBIDO) {
            Usuario usuario = usuarioRepository.findByEmail(emailUsuario).orElseThrow();
            darBaixaEstoque(pedido, usuario);
        }

        pedido.setStatus(novoStatus);
        return PedidoResponse.from(pedidoRepository.save(pedido));
    }

    @Transactional
    public PedidoResponse cancelar(Long pedidoId, CancelamentoRequest request, String emailUsuario) {
        Pedido pedido = buscar(pedidoId);
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario).orElseThrow();

        // RN04: após EM_PREPARO, só GERENTE/ADMIN pode cancelar
        if (pedido.getStatus() == StatusPedido.EM_PREPARO ||
            pedido.getStatus() == StatusPedido.PRONTO ||
            pedido.getStatus() == StatusPedido.SAIU_ENTREGA) {

            if (usuario.getPerfil() != Perfil.ADMIN && usuario.getPerfil() != Perfil.GERENTE) {
                throw new AccessDeniedException(
                        "Após EM_PREPARO apenas ADMIN ou GERENTE pode cancelar o pedido");
            }
        }

        if (pedido.getStatus() == StatusPedido.CANCELADO
                || pedido.getStatus() == StatusPedido.FINALIZADO) {
            throw new BusinessException("Pedido já está " + pedido.getStatus());
        }

        // Se o pedido foi confirmado (estoque já foi baixado), fazer estorno
        boolean estoqueJaBaixado = pedido.getStatus() == StatusPedido.CONFIRMADO ||
                                   pedido.getStatus() == StatusPedido.EM_PREPARO ||
                                   pedido.getStatus() == StatusPedido.PRONTO ||
                                   pedido.getStatus() == StatusPedido.SAIU_ENTREGA;

        if (estoqueJaBaixado) {
            estornarEstoque(pedido, usuario);
        }

        pedido.setStatus(StatusPedido.CANCELADO);
        pedido.setMotivoCancelamento(request.motivo());

        return PedidoResponse.from(pedidoRepository.save(pedido));
    }

    // RF-17: baixa automática no estoque com base na ficha técnica × quantidade
    private void darBaixaEstoque(Pedido pedido, Usuario usuario) {
        for (PedidoItem item : pedido.getItens()) {
            FichaTecnica ficha = fichaTecnicaRepository.findByPratoId(item.getPrato().getId())
                    .orElseThrow(() -> new BusinessException(
                            "Prato sem ficha técnica: " + item.getPrato().getNome()));

            for (FichaTecnicaItem fichaItem : ficha.getItens()) {
                BigDecimal qtdNecessaria = fichaItem.getQuantidade()
                        .multiply(fichaItem.getFatorCorrecao())
                        .multiply(BigDecimal.valueOf(item.getQuantidade()));

                estoqueService.registrarBaixaPorPedido(
                        fichaItem.getIngrediente(), qtdNecessaria, pedido, usuario);
            }
        }
    }

    // RF-18: estorno de estoque ao cancelar
    private void estornarEstoque(Pedido pedido, Usuario usuario) {
        for (PedidoItem item : pedido.getItens()) {
            fichaTecnicaRepository.findByPratoId(item.getPrato().getId())
                    .ifPresent(ficha -> {
                        for (FichaTecnicaItem fichaItem : ficha.getItens()) {
                            BigDecimal qtd = fichaItem.getQuantidade()
                                    .multiply(fichaItem.getFatorCorrecao())
                                    .multiply(BigDecimal.valueOf(item.getQuantidade()));

                            estoqueService.registrarEstornoPorPedido(
                                    fichaItem.getIngrediente(), qtd, pedido, usuario);
                        }
                    });
        }
    }

    // RN03: verificar estoque antes de criar pedido
    private void validarEstoque(Prato prato, int quantidade) {
        FichaTecnica ficha = fichaTecnicaRepository.findByPratoId(prato.getId()).orElse(null);
        if (ficha == null) return;

        for (FichaTecnicaItem fichaItem : ficha.getItens()) {
            BigDecimal qtdNecessaria = fichaItem.getQuantidade()
                    .multiply(fichaItem.getFatorCorrecao())
                    .multiply(BigDecimal.valueOf(quantidade));

            BigDecimal saldo = ingredienteService.getSaldo(fichaItem.getIngrediente().getId());

            if (saldo.compareTo(qtdNecessaria) < 0) {
                throw new EstoqueInsuficienteException(
                        fichaItem.getIngrediente().getNome(),
                        saldo.doubleValue(),
                        qtdNecessaria.doubleValue()
                );
            }
        }
    }

    private void validarTransicaoStatus(StatusPedido atual, StatusPedido novo) {
        boolean valido = switch (atual) {
            case RECEBIDO -> novo == StatusPedido.CONFIRMADO || novo == StatusPedido.CANCELADO;
            case CONFIRMADO -> novo == StatusPedido.EM_PREPARO || novo == StatusPedido.CANCELADO;
            case EM_PREPARO -> novo == StatusPedido.PRONTO || novo == StatusPedido.CANCELADO;
            case PRONTO -> novo == StatusPedido.SAIU_ENTREGA || novo == StatusPedido.CANCELADO;
            case SAIU_ENTREGA -> novo == StatusPedido.FINALIZADO || novo == StatusPedido.CANCELADO;
            default -> false;
        };
        if (!valido) {
            throw new BusinessException(
                    "Transição inválida de status: " + atual + " → " + novo);
        }
    }

    private Pedido buscar(Long id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", id));
    }
}
