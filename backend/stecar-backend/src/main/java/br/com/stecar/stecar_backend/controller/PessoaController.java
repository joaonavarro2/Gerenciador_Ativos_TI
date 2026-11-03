package br.com.stecar.stecar_backend.controller;

import br.com.stecar.stecar_backend.dto.PessoaRequestDTO;
import br.com.stecar.stecar_backend.dto.PessoaResponseDTO;
import br.com.stecar.stecar_backend.dto.ItemResponseDTO;
import br.com.stecar.stecar_backend.service.PessoaService;
import br.com.stecar.stecar_backend.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/pessoas")
public class PessoaController {
    private final PessoaService service;
    private final ItemService itemService;

    public PessoaController(PessoaService service, ItemService itemService) {
        this.service = service;
        this.itemService = itemService;
    }

    @GetMapping
    public ResponseEntity<List<PessoaResponseDTO>> listar(
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long escritorioId,
            @RequestParam(required = false) Long departamentoId) {
        return ResponseEntity.ok(service.listar(busca, status, escritorioId, departamentoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PessoaResponseDTO> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscar(id));
    }

    @GetMapping("/{id}/bens")
    public ResponseEntity<List<ItemResponseDTO>> listarBens(@PathVariable Long id) {
        service.buscar(id);
        List<ItemResponseDTO> bens = itemService.listar(null, null, null, null).stream()
                .filter(item -> id.equals(item.pessoaId()))
                .toList();
        return ResponseEntity.ok(bens);
    }

    @PostMapping
    public ResponseEntity<PessoaResponseDTO> criar(@Valid @RequestBody PessoaRequestDTO dados) {
        PessoaResponseDTO criada = service.criar(dados);
        return ResponseEntity.created(URI.create("/api/pessoas/" + criada.id())).body(criada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PessoaResponseDTO> atualizar(
            @PathVariable Long id, @Valid @RequestBody PessoaRequestDTO dados) {
        return ResponseEntity.ok(service.atualizar(id, dados));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PessoaResponseDTO> alterarStatus(@PathVariable Long id, @RequestParam String status) {
        return ResponseEntity.ok(service.alterarStatus(id, status));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
