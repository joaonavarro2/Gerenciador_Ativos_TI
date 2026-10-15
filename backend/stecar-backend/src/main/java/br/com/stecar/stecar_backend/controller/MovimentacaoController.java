
package br.com.stecar.stecar_backend.controller;

import br.com.stecar.stecar_backend.dto.MovimentacaoRequestDTO;
import br.com.stecar.stecar_backend.dto.MovimentacaoResponseDTO;
import br.com.stecar.stecar_backend.entity.Usuario;
import br.com.stecar.stecar_backend.repository.UsuarioRepository;
import br.com.stecar.stecar_backend.service.MovimentacaoService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/movimentacoes")
public class MovimentacaoController {

    private final MovimentacaoService movimentacaoService;
    private final UsuarioRepository usuarioRepository;

    public MovimentacaoController(
            MovimentacaoService movimentacaoService,
            UsuarioRepository usuarioRepository) {

        this.movimentacaoService = movimentacaoService;
        this.usuarioRepository = usuarioRepository;
    }

    // Lista todas as movimentações, com filtros opcionais.
    @GetMapping
    public ResponseEntity<List<MovimentacaoResponseDTO>> listar(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long itemId) {

        if (itemId != null) {
            return ResponseEntity.ok(
                    movimentacaoService.listarPorItem(itemId)
            );
        }

        if (status != null && !status.isBlank()) {
            return ResponseEntity.ok(
                    movimentacaoService.listarPorStatus(status)
            );
        }

        return ResponseEntity.ok(
                movimentacaoService.listarTodas()
        );
    }

    // Busca uma movimentação pelo ID.
    @GetMapping("/{id}")
    public ResponseEntity<MovimentacaoResponseDTO> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                movimentacaoService.buscarPorId(id)
        );
    }

    // Registra uma movimentação usando o administrador autenticado.
    @PostMapping
    public ResponseEntity<MovimentacaoResponseDTO> cadastrar(
            @Valid @RequestBody MovimentacaoRequestDTO dados,
            Authentication authentication) {

        Usuario usuario = usuarioRepository
                .findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED,
                        "Usuário autenticado não encontrado."
                ));

        MovimentacaoResponseDTO resposta =
                movimentacaoService.cadastrar(dados, usuario.getId());

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(resposta);
    }

    // Atualiza uma movimentação existente.
    @PutMapping("/{id}")
    public ResponseEntity<MovimentacaoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody MovimentacaoRequestDTO dados) {

        return ResponseEntity.ok(
                movimentacaoService.atualizar(id, dados)
        );
    }
}
