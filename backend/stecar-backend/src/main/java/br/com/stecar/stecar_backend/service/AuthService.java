package br.com.stecar.stecar_backend.service;

import br.com.stecar.stecar_backend.dto.LoginRequestDTO;
import br.com.stecar.stecar_backend.dto.LoginResponseDTO;
import br.com.stecar.stecar_backend.dto.UsuarioResponseDTO;
import br.com.stecar.stecar_backend.entity.Usuario;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import br.com.stecar.stecar_backend.repository.UsuarioRepository;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioDetailsService usuarioDetailsService;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            UsuarioDetailsService usuarioDetailsService,
            UsuarioRepository usuarioRepository,
            JwtService jwtService) {

        this.authenticationManager = authenticationManager;
        this.usuarioDetailsService = usuarioDetailsService;
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
    }

    public LoginResponseDTO login(LoginRequestDTO dados) {

        Authentication autenticacao =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                dados.getEmail(),
                                dados.getSenha()
                        )
                );

        UserDetails usuarioAutenticado =
                (UserDetails) autenticacao.getPrincipal();

        String token =
                jwtService.gerarToken(usuarioAutenticado);

        Usuario usuario = usuarioRepository
                .findByEmail(dados.getEmail())
                .orElseThrow();

        UsuarioResponseDTO usuarioResponse =
                new UsuarioResponseDTO(
                        usuario.getId(),
                        usuario.getNomeCompleto(),
                        usuario.getEmail(),
                        usuario.getFuncao(),
                        usuario.getStatus()
                );

        return new LoginResponseDTO(
                token,
                "Bearer",
                usuarioResponse
        );
    }
}