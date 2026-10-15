package br.com.stecar.stecar_backend.service;

import br.com.stecar.stecar_backend.dto.LoginRequestDTO;
import br.com.stecar.stecar_backend.dto.LoginResponseDTO;
import br.com.stecar.stecar_backend.dto.UsuarioResponseDTO;
import br.com.stecar.stecar_backend.entity.Usuario;
import br.com.stecar.stecar_backend.entity.TokenRecuperacaoSenha;
import br.com.stecar.stecar_backend.repository.TokenRecuperacaoSenhaRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import br.com.stecar.stecar_backend.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
        private final TokenRecuperacaoSenhaRepository tokenRepository;
        private final PasswordEncoder passwordEncoder;
        private final JavaMailSender mailSender;
        private final String frontendUrl;
        private final String mailFrom;

    public AuthService(
            AuthenticationManager authenticationManager,
            UsuarioRepository usuarioRepository,
            JwtService jwtService,
            TokenRecuperacaoSenhaRepository tokenRepository,
            PasswordEncoder passwordEncoder,
            JavaMailSender mailSender,
            @Value("${stecar.frontend-url}") String frontendUrl,
            @Value("${stecar.mail.from}") String mailFrom) {

        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.frontendUrl = frontendUrl;
        this.mailFrom = mailFrom;
    }

    public LoginResponseDTO login(LoginRequestDTO dados) {
        String email = normalizarEmail(dados.getEmail());

                if (!usuarioRepository.existsByEmail(email)) {
                        throw new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "Usuário não cadastrado."
                        );
                }

        Authentication autenticacao =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                email,
                                dados.getSenha()
                        )
                );

        UserDetails usuarioAutenticado =
                (UserDetails) autenticacao.getPrincipal();

        String token =
                jwtService.gerarToken(usuarioAutenticado);

        Usuario usuario = usuarioRepository
                .findByEmail(email)
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

        @Transactional
        public void solicitarRecuperacao(String email) {
                email = normalizarEmail(email);
                usuarioRepository.findByEmail(email).ifPresent(usuario -> {
                        tokenRepository.findByUsuarioIdAndUsadoFalse(usuario.getId())
                                        .forEach(token -> token.setUsado(true));

                        TokenRecuperacaoSenha recuperacao = new TokenRecuperacaoSenha();
                        recuperacao.setToken(UUID.randomUUID().toString());
                        recuperacao.setUsuario(usuario);
                        recuperacao.setExpiraEm(Instant.now().plus(30, ChronoUnit.MINUTES));
                        recuperacao.setUsado(false);
                        tokenRepository.save(recuperacao);

                        enviarEmailRecuperacao(usuario.getEmail(), recuperacao.getToken());
                });
        }

        @Transactional
        public void redefinirSenha(String token, String novaSenha) {
                TokenRecuperacaoSenha recuperacao = tokenRepository.findByTokenAndUsadoFalse(token)
                                .filter(item -> item.getExpiraEm().isAfter(Instant.now()))
                                .orElseThrow(() -> new ResponseStatusException(
                                                HttpStatus.BAD_REQUEST, "O link de redefinição é inválido ou expirou."));

                Usuario usuario = recuperacao.getUsuario();
                usuario.setSenha(passwordEncoder.encode(novaSenha));
                usuarioRepository.save(usuario);
                recuperacao.setUsado(true);
                tokenRepository.save(recuperacao);
        }

        private void enviarEmailRecuperacao(String email, String token) {
                if (mailFrom == null || mailFrom.isBlank()) {
                        throw new ResponseStatusException(
                                        HttpStatus.SERVICE_UNAVAILABLE,
                                        "O serviço de e-mail ainda não está configurado.");
                }

                SimpleMailMessage mensagem = new SimpleMailMessage();
                mensagem.setFrom(mailFrom);
                mensagem.setTo(email);
                mensagem.setSubject("Redefinição de senha - STECAR");
                mensagem.setText("Recebemos uma solicitação para redefinir sua senha.\n\n"
                                + "Acesse este link em até 30 minutos:\n"
                                + frontendUrl + "/#/redefinir-senha?token=" + token + "\n\n"
                                + "Se você não solicitou esta alteração, ignore este e-mail.");
                mailSender.send(mensagem);
        }

        private String normalizarEmail(String email) {
                return email.trim().toLowerCase(Locale.ROOT);
        }
}