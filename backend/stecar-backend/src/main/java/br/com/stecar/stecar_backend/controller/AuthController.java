package br.com.stecar.stecar_backend.controller;

import br.com.stecar.stecar_backend.dto.LoginRequestDTO;
import br.com.stecar.stecar_backend.dto.LoginResponseDTO;
import br.com.stecar.stecar_backend.dto.UsuarioResponseDTO;
import br.com.stecar.stecar_backend.dto.UsuarioCadastroRequestDTO;
import br.com.stecar.stecar_backend.dto.RecuperarSenhaRequestDTO;
import br.com.stecar.stecar_backend.dto.RedefinirSenhaRequestDTO;
import br.com.stecar.stecar_backend.entity.Usuario;
import br.com.stecar.stecar_backend.repository.UsuarioRepository;
import br.com.stecar.stecar_backend.service.AuthService;
import br.com.stecar.stecar_backend.service.UsuarioService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;

    public AuthController(
            AuthService authService,
            UsuarioRepository usuarioRepository,
            UsuarioService usuarioService) {

        this.authService = authService;
        this.usuarioRepository = usuarioRepository;
        this.usuarioService = usuarioService;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<UsuarioResponseDTO> cadastrar(
            @Valid @RequestBody UsuarioCadastroRequestDTO dados) {
        return ResponseEntity.status(201).body(usuarioService.cadastrarPublico(dados));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO dados) {

        return ResponseEntity.ok(authService.login(dados));
    }

    @PostMapping("/recuperar-senha")
    public ResponseEntity<Void> recuperarSenha(
            @Valid @RequestBody RecuperarSenhaRequestDTO dados) {
        authService.solicitarRecuperacao(dados.getEmail());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/redefinir-senha")
    public ResponseEntity<Void> redefinirSenha(
            @Valid @RequestBody RedefinirSenhaRequestDTO dados) {
        authService.redefinirSenha(dados.getToken(), dados.getNovaSenha());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> me(
            Authentication authentication) {

        // O Spring Security identifica o usuário através do JWT.
        String email = authentication.getName();

        // Busca os dados atuais do usuário no banco.
        Usuario usuario = usuarioRepository
                .findByEmail(email)
                .orElseThrow();

        UsuarioResponseDTO resposta =
                new UsuarioResponseDTO(
                        usuario.getId(),
                        usuario.getNomeCompleto(),
                        usuario.getEmail(),
                        usuario.getFuncao(),
                        usuario.getStatus()
                );

        return ResponseEntity.ok(resposta);
    }
}
