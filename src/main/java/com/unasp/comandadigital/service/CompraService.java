package com.unasp.comandadigital.service;

import com.unasp.comandadigital.dto.compra.*;
import com.unasp.comandadigital.entity.*;
import com.unasp.comandadigital.entity.enums.StatusCompra;
import com.unasp.comandadigital.exception.BusinessException;
import com.unasp.comandadigital.exception.ResourceNotFoundException;
import com.unasp.comandadigital.repository.IngredienteRepository;
import com.unasp.comandadigital.repository.PedidoCompraRepository;
import com.unasp.comandadigital.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CompraService {

    private final PedidoCompraRepository compraRepository;
    private final FornecedorService fornecedorService;
    private final IngredienteService ingredienteService;
    private final IngredienteRepository ingredienteRepository;
    private final EstoqueService estoqueService;
    private final UsuarioRepository usuarioRepository;

    public Page<CompraResponse> listar(Pageable pageable) {
        return compraRepository.findAllByOrderByCreatedAtDesc(pageable).map(CompraResponse::from);
    }

    public CompraResponse buscarPorId(Long id) {
        return CompraResponse.from(buscar(id));
    }

    @Transactional
    public CompraResponse criar(CompraRequest request, String emailUsuario) {
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario).orElseThrow();
        Fornecedor fornecedor = fornecedorService.buscar(request.fornecedorId());

        List<PedidoCompraItem> itens = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        PedidoCompra compra = PedidoCompra.builder()
                .fornecedor(fornecedor)
                .usuario(usuario)
                .status(StatusCompra.RASCUNHO)
                .itens(new ArrayList<>())
                .build();

        PedidoCompra saved = compraRepository.save(compra);

        for (CompraItemRequest itemReq : request.itens()) {
            Ingrediente ingrediente = ingredienteService.buscar(itemReq.ingredienteId());

            BigDecimal subtotal = itemReq.quantidade().multiply(itemReq.precoUnitario());

            PedidoCompraItem item = PedidoCompraItem.builder()
                    .pedidoCompra(saved)
                    .ingrediente(ingrediente)
                    .quantidade(itemReq.quantidade())
                    .precoUnitario(itemReq.precoUnitario())
                    .subtotal(subtotal)
                    .build();

            saved.getItens().add(item);
            total = total.add(subtotal);
        }

        saved.setValorTotal(total);
        return CompraResponse.from(compraRepository.save(saved));
    }

    @Transactional
    public CompraResponse alterarStatus(Long id, StatusCompra novoStatus, String emailUsuario) {
        PedidoCompra compra = buscar(id);

        validarTransicao(compra.getStatus(), novoStatus);
        compra.setStatus(novoStatus);

        return CompraResponse.from(compraRepository.save(compra));
    }

    @Transactional
    public CompraResponse receberMercadoria(Long id, String emailUsuario) {
        PedidoCompra compra = buscar(id);
        Usuario usuario = usuarioRepository.findByEmail(emailUsuario).orElseThrow();

        if (compra.getStatus() != StatusCompra.ENVIADO) {
            throw new BusinessException("Só é possível receber compras com status ENVIADO. Status atual: " + compra.getStatus());
        }

        // Para cada item: entrada no estoque + atualiza custo (RN05)
        for (PedidoCompraItem item : compra.getItens()) {
            Ingrediente ingrediente = item.getIngrediente();

            // RN05: atualizar custo unitário do ingrediente com o preço da compra
            ingrediente.setCustoUnitario(item.getPrecoUnitario());
            ingredienteRepository.save(ingrediente);

            // Registrar entrada no estoque
            estoqueService.registrarEntradaPorCompra(
                    ingrediente,
                    item.getQuantidade(),
                    item.getPrecoUnitario(),
                    compra,
                    null,
                    usuario
            );
        }

        compra.setStatus(StatusCompra.RECEBIDO);
        return CompraResponse.from(compraRepository.save(compra));
    }

    @Transactional
    public CompraResponse atualizar(Long id, CompraRequest request, String emailUsuario) {
        PedidoCompra compra = buscar(id);

        if (compra.getStatus() != StatusCompra.RASCUNHO) {
            throw new BusinessException("Apenas compras em RASCUNHO podem ser editadas");
        }

        Fornecedor fornecedor = fornecedorService.buscar(request.fornecedorId());
        compra.setFornecedor(fornecedor);
        compra.getItens().clear();

        BigDecimal total = BigDecimal.ZERO;
        for (CompraItemRequest itemReq : request.itens()) {
            Ingrediente ingrediente = ingredienteService.buscar(itemReq.ingredienteId());
            BigDecimal subtotal = itemReq.quantidade().multiply(itemReq.precoUnitario());

            PedidoCompraItem item = PedidoCompraItem.builder()
                    .pedidoCompra(compra)
                    .ingrediente(ingrediente)
                    .quantidade(itemReq.quantidade())
                    .precoUnitario(itemReq.precoUnitario())
                    .subtotal(subtotal)
                    .build();

            compra.getItens().add(item);
            total = total.add(subtotal);
        }

        compra.setValorTotal(total);
        return CompraResponse.from(compraRepository.save(compra));
    }

    private void validarTransicao(StatusCompra atual, StatusCompra novo) {
        boolean valido = switch (atual) {
            case RASCUNHO -> novo == StatusCompra.ENVIADO || novo == StatusCompra.CANCELADO;
            case ENVIADO -> novo == StatusCompra.RECEBIDO || novo == StatusCompra.CANCELADO;
            default -> false;
        };
        if (!valido) {
            throw new BusinessException("Transição inválida: " + atual + " → " + novo);
        }
    }

    private PedidoCompra buscar(Long id) {
        return compraRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido de compra", id));
    }
}
