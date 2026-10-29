package br.com.stecar.stecar_backend.controller;

import br.com.stecar.stecar_backend.dto.EscritorioRequestDTO;
import br.com.stecar.stecar_backend.dto.EscritorioResponseDTO;
import br.com.stecar.stecar_backend.service.EscritorioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/escritorios")
public class EscritorioController {
    private final EscritorioService service;

    public EscritorioController(EscritorioService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<List<EscritorioResponseDTO>> listar(
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String cidade) {
        return ResponseEntity.ok(service.listar(busca, status, cidade));
    }

    @GetMapping("/{id}")
    public ResponseEntity<EscritorioResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscar(id));
    }

    @PostMapping
    public ResponseEntity<EscritorioResponseDTO> criar(@Valid @RequestBody EscritorioRequestDTO dados) {
        EscritorioResponseDTO criado = service.criar(dados);
        return ResponseEntity.created(URI.create("/api/escritorios/" + criado.id())).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EscritorioResponseDTO> atualizar(
            @PathVariable Long id, @Valid @RequestBody EscritorioRequestDTO dados) {
        return ResponseEntity.ok(service.atualizar(id, dados));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<EscritorioResponseDTO> status(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(service.alterarStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
