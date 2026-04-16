package com.unasp.comandadigital.controller.admin;

import com.unasp.comandadigital.dto.fornecedor.*;
import com.unasp.comandadigital.service.FornecedorService;
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
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/fornecedores")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
@Tag(name = "Admin — Fornecedores")
public class FornecedorAdminController {

    private final FornecedorService fornecedorService;

    @GetMapping
    public ResponseEntity<Page<FornecedorResponse>> listar(
            @PageableDefault(size = 20, sort = "razaoSocial") Pageable pageable) {
        return ResponseEntity.ok(fornecedorService.listar(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FornecedorResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(fornecedorService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<FornecedorResponse> criar(@Valid @RequestBody FornecedorRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fornecedorService.criar(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FornecedorResponse> atualizar(
            @PathVariable Long id, @Valid @RequestBody FornecedorRequest request) {
        return ResponseEntity.ok(fornecedorService.atualizar(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        fornecedorService.desativar(id);
        return ResponseEntity.noContent().build();
    }

    // Catálogo de produtos do fornecedor
    @GetMapping("/{id}/produtos")
    @Operation(summary = "Listar ingredientes vinculados ao fornecedor")
    public ResponseEntity<List<FornecedorProdutoResponse>> listarProdutos(@PathVariable Long id) {
        return ResponseEntity.ok(fornecedorService.listarProdutos(id));
    }

    @PostMapping("/{id}/produtos")
    @Operation(summary = "Vincular ingrediente ao fornecedor com preço")
    public ResponseEntity<FornecedorProdutoResponse> adicionarProduto(
            @PathVariable Long id, @Valid @RequestBody FornecedorProdutoRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(fornecedorService.adicionarProduto(id, request));
    }

    @PutMapping("/{id}/produtos/{produtoId}")
    @Operation(summary = "Atualizar preço do ingrediente no fornecedor")
    public ResponseEntity<FornecedorProdutoResponse> atualizarProduto(
            @PathVariable Long id,
            @PathVariable Long produtoId,
            @Valid @RequestBody FornecedorProdutoRequest request) {
        return ResponseEntity.ok(fornecedorService.atualizarProduto(id, produtoId, request));
    }

    @GetMapping("/cotacao/{ingredienteId}")
    @Operation(summary = "Cotação comparativa: todos os fornecedores para um ingrediente, ordenados por preço")
    public ResponseEntity<List<FornecedorProdutoResponse>> cotacao(@PathVariable Long ingredienteId) {
        return ResponseEntity.ok(fornecedorService.cotacao(ingredienteId));
    }
}
