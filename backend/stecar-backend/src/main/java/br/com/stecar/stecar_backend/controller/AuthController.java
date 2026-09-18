package br.com.stecar.stecar_backend.controller;

import br.com.stecar.stecar_backend.dto.LoginRequestDTO;
import br.com.stecar.stecar_backend.dto.LoginResponseDTO;
import br.com.stecar.stecar_backend.dto.UsuarioResponseDTO;
import br.com.stecar.stecar_backend.entity.Usuario;
import br.com.stecar.stecar_backend.repository.UsuarioRepository;
import br.com.stecar.stecar_backend.service.AuthService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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