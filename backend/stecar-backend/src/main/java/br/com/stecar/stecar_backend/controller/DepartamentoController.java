package br.com.stecar.stecar_backend.controller;

import br.com.stecar.stecar_backend.dto.DepartamentoRequestDTO;
import br.com.stecar.stecar_backend.dto.DepartamentoResponseDTO;
import br.com.stecar.stecar_backend.dto.ItemResponseDTO;
import br.com.stecar.stecar_backend.service.DepartamentoService;
import br.com.stecar.stecar_backend.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/departamentos")
public class DepartamentoController {
    private final DepartamentoService service;
    private final ItemService itemService;

    public DepartamentoController(DepartamentoService service, ItemService itemService) {
        this.service = service;
        this.itemService = itemService;
    }

    @GetMapping
    public ResponseEntity<List<DepartamentoResponseDTO>> listar(
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long escritorioId) {
        return ResponseEntity.ok(service.listar(busca, status, escritorioId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartamentoResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscar(id));
    }

    @GetMapping("/{id}/bens")
    public ResponseEntity<List<ItemResponseDTO>> listarBens(@PathVariable Long id) {
        service.buscar(id);
        return ResponseEntity.ok(itemService.listar(null, null, null, id));
    }

    @PostMapping
    public ResponseEntity<DepartamentoResponseDTO> criar(@Valid @RequestBody DepartamentoRequestDTO dados) {
        DepartamentoResponseDTO criado = service.criar(dados);
        return ResponseEntity.created(URI.create("/api/departamentos/" + criado.id())).body(criado);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepartamentoResponseDTO> atualizar(
            @PathVariable Long id, @Valid @RequestBody DepartamentoRequestDTO dados) {
        return ResponseEntity.ok(service.atualizar(id, dados));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<DepartamentoResponseDTO> alterarStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(service.alterarStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
