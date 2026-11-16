package br.com.stecar.stecar_backend.controller;

import br.com.stecar.stecar_backend.dto.LoginRequestDTO;
import br.com.stecar.stecar_backend.dto.LoginResponseDTO;
import br.com.stecar.stecar_backend.dto.UsuarioResponseDTO;
import br.com.stecar.stecar_backend.dto.AlterarSenhaRequestDTO;
import br.com.stecar.stecar_backend.dto.RecuperarSenhaRequestDTO;
import br.com.stecar.stecar_backend.dto.RedefinirSenhaRequestDTO;
import br.com.stecar.stecar_backend.entity.Usuario;
import br.com.stecar.stecar_backend.repository.UsuarioRepository;
import br.com.stecar.stecar_backend.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import br.com.stecar.stecar_backend.service.JwtService;
import java.time.Instant;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UsuarioRepository usuarioRepository;

    public AuthController(
            AuthService authService,
            UsuarioRepository usuarioRepository) {

        this.authService = authService;
        this.usuarioRepository = usuarioRepository;
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

    @PutMapping("/alterar-senha")
    public ResponseEntity<Void> alterarSenha(
            @Valid @RequestBody AlterarSenhaRequestDTO dados, Authentication authentication) {
        authService.alterarSenha(authentication.getName(), dados);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/renovar")
    public ResponseEntity<LoginResponseDTO> renovar(JwtAuthenticationToken authentication) {
        Object inicio = authentication.getToken().getClaims().get(JwtService.CLAIM_INICIO_SESSAO);
        Instant inicioSessao = inicio instanceof Number n ? Instant.ofEpochSecond(n.longValue()) : null;
        return ResponseEntity.ok(authService.renovar(authentication.getName(), inicioSessao));
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
