package com.unasp.comandadigital.controller;

import com.unasp.comandadigital.dto.prato.PratoResponse;
import com.unasp.comandadigital.service.CategoriaService;
import com.unasp.comandadigital.service.PratoService;
import com.unasp.comandadigital.dto.categoria.CategoriaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cardapio")
@RequiredArgsConstructor
@Tag(name = "Cardápio Público", description = "Endpoints públicos — sem autenticação")
public class CardapioController {

    private final PratoService pratoService;
    private final CategoriaService categoriaService;

    @GetMapping
    @Operation(summary = "Lista pratos ativos com filtro opcional por categoria")
    public ResponseEntity<List<PratoResponse>> listar(
            @RequestParam(required = false) Long categoriaId) {
        return ResponseEntity.ok(pratoService.listarAtivosParaCardapio(categoriaId));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detalhe de um prato")
    public ResponseEntity<PratoResponse> detalhe(@PathVariable Long id) {
        return ResponseEntity.ok(pratoService.buscarPorId(id));
    }

    @GetMapping("/categorias")
    @Operation(summary = "Lista categorias ativas")
    public ResponseEntity<List<CategoriaResponse>> categorias() {
        return ResponseEntity.ok(categoriaService.listarAtivas());
    }
}
