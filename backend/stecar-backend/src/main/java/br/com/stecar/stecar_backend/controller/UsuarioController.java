package br.com.stecar.stecar_backend.controller;

import br.com.stecar.stecar_backend.dto.UsuarioRequestDTO;
import br.com.stecar.stecar_backend.dto.UsuarioResponseDTO;
import br.com.stecar.stecar_backend.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    // Lista todos os usuários
    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos() {

        return ResponseEntity.ok(
                usuarioService.listarTodos()
        );
    }

    // Busca um usuário pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                usuarioService.buscarPorId(id)
        );
    }

    // Cadastra um novo usuário
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> cadastrar(
            @Valid @RequestBody UsuarioRequestDTO dados) {

        return ResponseEntity.ok(
                usuarioService.cadastrar(dados)
        );
    }

    // Atualiza os dados de um usuário
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody UsuarioRequestDTO dados) {

        return ResponseEntity.ok(
                usuarioService.atualizar(id, dados)
        );
    }

    // Altera somente o status do usuário
    @PatchMapping("/{id}/status")
    public ResponseEntity<UsuarioResponseDTO> alterarStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        return ResponseEntity.ok(
                usuarioService.alterarStatus(id, status)
        );
    }

    // Exclui um usuário
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id) {

        usuarioService.excluir(id);

        return ResponseEntity.noContent().build();
    }
}