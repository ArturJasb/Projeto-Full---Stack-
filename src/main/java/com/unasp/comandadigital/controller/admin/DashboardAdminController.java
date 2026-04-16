package com.unasp.comandadigital.controller.admin;

import com.unasp.comandadigital.dto.dashboard.DashboardResponse;
import com.unasp.comandadigital.dto.dashboard.TopPratoResponse;
import com.unasp.comandadigital.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','GERENTE')")
@Tag(name = "Admin — Dashboard")
public class DashboardAdminController {

    private final DashboardService dashboardService;

    @GetMapping("/resumo")
    @Operation(summary = "KPIs do dia: faturamento, pedidos, ticket médio, alertas de estoque")
    public ResponseEntity<DashboardResponse> resumo() {
        return ResponseEntity.ok(dashboardService.getResumo());
    }

    @GetMapping("/top-pratos")
    @Operation(summary = "Top 5 pratos mais vendidos")
    public ResponseEntity<List<TopPratoResponse>> topPratos(
            @RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(dashboardService.getTopPratos(limit));
    }
}
