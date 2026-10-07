package br.com.stecar.stecar_backend.controller;

import br.com.stecar.stecar_backend.dto.DashboardResponseDTO;
import br.com.stecar.stecar_backend.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    public ResponseEntity<DashboardResponseDTO> buscarDashboard() {
        return ResponseEntity.ok(
                dashboardService.buscarDashboard()
        );
    }
}