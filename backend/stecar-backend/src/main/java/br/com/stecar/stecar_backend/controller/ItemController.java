package br.com.stecar.stecar_backend.controller;

import br.com.stecar.stecar_backend.dto.ItemOpcoesResponseDTO;
import br.com.stecar.stecar_backend.dto.ItemRequestDTO;
import br.com.stecar.stecar_backend.dto.ItemResponseDTO;
import br.com.stecar.stecar_backend.dto.MovimentacaoItemRequestDTO;
import br.com.stecar.stecar_backend.dto.MovimentacaoItemResponseDTO;
import br.com.stecar.stecar_backend.dto.ConsertoItemResponseDTO;
import br.com.stecar.stecar_backend.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bens")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public ResponseEntity<List<ItemResponseDTO>> listar(
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long departamentoId,
            @RequestParam(required = false) String departamento) {
        return ResponseEntity.ok(itemService.listar(busca, categoria, status, departamentoId, departamento));
    }

    @GetMapping("/opcoes")
    public ResponseEntity<ItemOpcoesResponseDTO> opcoes() {
        return ResponseEntity.ok(itemService.buscarOpcoes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ItemResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.buscarPorId(id));
    }

    @GetMapping("/{id}/movimentacoes")
    public ResponseEntity<List<MovimentacaoItemResponseDTO>> movimentacoes(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.listarMovimentacoes(id));
    }

    @GetMapping("/{id}/manutencoes")
    public ResponseEntity<List<ConsertoItemResponseDTO>> manutencoes(@PathVariable Long id) {
        return ResponseEntity.ok(itemService.listarConsertos(id));
    }

    @PostMapping
    public ResponseEntity<ItemResponseDTO> criar(@Valid @RequestBody ItemRequestDTO dados) {
        return ResponseEntity.status(201).body(itemService.criar(dados));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ItemResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ItemRequestDTO dados) {
        return ResponseEntity.ok(itemService.atualizar(id, dados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        itemService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/movimentacoes")
    public ResponseEntity<ItemResponseDTO> movimentar(
            @PathVariable Long id,
            @Valid @RequestBody MovimentacaoItemRequestDTO dados,
            Authentication authentication) {
        return ResponseEntity.ok(itemService.movimentar(id, dados, authentication.getName()));
    }
}