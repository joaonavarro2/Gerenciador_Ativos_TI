package br.com.stecar.stecar_backend.controller;

import br.com.stecar.stecar_backend.dto.ImportacaoExcelRequestDTO;
import br.com.stecar.stecar_backend.dto.ImportacaoExcelResultadoDTO;
import br.com.stecar.stecar_backend.service.ImportacaoExcelService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/importacao-excel")
public class ImportacaoExcelController {
    private final ImportacaoExcelService service;

    public ImportacaoExcelController(ImportacaoExcelService service) {
        this.service = service;
    }

    @PostMapping("/{tipo}/validar")
    public ResponseEntity<ImportacaoExcelResultadoDTO> validar(
            @PathVariable String tipo, @RequestBody ImportacaoExcelRequestDTO request) {
        return ResponseEntity.ok(service.validar(tipo, request));
    }

    @PostMapping("/{tipo}/confirmar")
    public ResponseEntity<ImportacaoExcelResultadoDTO> confirmar(
            @PathVariable String tipo,
            @RequestBody ImportacaoExcelRequestDTO request,
            Authentication authentication) {
        return ResponseEntity.ok(service.confirmar(tipo, request, authentication.getName()));
    }
}
