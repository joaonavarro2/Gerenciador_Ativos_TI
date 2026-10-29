package br.com.stecar.stecar_backend.controller;

import br.com.stecar.stecar_backend.dto.OutroItemRequestDTO;
import br.com.stecar.stecar_backend.dto.OutroItemResponseDTO;
import br.com.stecar.stecar_backend.service.OutroItemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/outros-itens")
public class OutroItemController {
    private final OutroItemService service;

    public OutroItemController(OutroItemService service) { this.service = service; }

    @GetMapping
    public ResponseEntity<List<OutroItemResponseDTO>> listar(
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) Long escritorioId,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(service.listar(busca, categoria, escritorioId, status));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OutroItemResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscar(id));
    }

    @PostMapping
    public ResponseEntity<OutroItemResponseDTO> criar(@Valid @RequestBody OutroItemRequestDTO dados) {
        OutroItemResponseDTO criado = service.criar(dados);
        return ResponseEntity.created(URI.create("/api/outros-itens/" + criado.id())).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OutroItemResponseDTO> atualizar(
            @PathVariable Long id, @Valid @RequestBody OutroItemRequestDTO dados) {
        return ResponseEntity.ok(service.atualizar(id, dados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
