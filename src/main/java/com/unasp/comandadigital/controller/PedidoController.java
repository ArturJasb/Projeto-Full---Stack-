package com.unasp.comandadigital.controller;

import com.unasp.comandadigital.dto.pedido.PedidoRequest;
import com.unasp.comandadigital.dto.pedido.PedidoResponse;
import com.unasp.comandadigital.service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos")
@RequiredArgsConstructor
@Tag(name = "Pedidos — Cliente", description = "Criar pedido e acompanhar status")
public class PedidoController {

    private final PedidoService pedidoService;

    @PostMapping
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Criar pedido (checkout)")
    public ResponseEntity<PedidoResponse> criar(
            @Valid @RequestBody PedidoRequest request,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(pedidoService.criar(request, user.getUsername()));
    }

    @GetMapping("/meus")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Histórico de pedidos do cliente logado")
    public ResponseEntity<Page<PedidoResponse>> meusPedidos(
            @AuthenticationPrincipal UserDetails user,
            @PageableDefault(size = 10, sort = "createdAt") Pageable pageable) {
        return ResponseEntity.ok(pedidoService.listarMeusPedidos(user.getUsername(), pageable));
    }

    @GetMapping("/{id}/status")
    @PreAuthorize("hasRole('CLIENTE')")
    @Operation(summary = "Status atual do pedido")
    public ResponseEntity<PedidoResponse> status(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(pedidoService.buscarStatus(id, user.getUsername()));
    }
}
