package br.com.stecar.stecar_backend.service;

import br.com.stecar.stecar_backend.dto.LoginRequestDTO;
import br.com.stecar.stecar_backend.dto.LoginResponseDTO;
import br.com.stecar.stecar_backend.dto.UsuarioResponseDTO;
import br.com.stecar.stecar_backend.entity.TokenRecuperacaoSenha;
import br.com.stecar.stecar_backend.entity.Usuario;
import br.com.stecar.stecar_backend.repository.TokenRecuperacaoSenhaRepository;
import br.com.stecar.stecar_backend.repository.UsuarioRepository;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final TokenRecuperacaoSenhaRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JavaMailSender mailSender;
    private final String frontendUrl;
    private final String mailFrom;
    private final Duration sessaoMaxima;

    public AuthService(
            AuthenticationManager authenticationManager,
            UsuarioRepository usuarioRepository,
            JwtService jwtService,
            TokenRecuperacaoSenhaRepository tokenRepository,
            PasswordEncoder passwordEncoder,
            JavaMailSender mailSender,
            @Value("${stecar.frontend-url}") String frontendUrl,
            @Value("${stecar.mail.from}") String mailFrom,
            @Value("${stecar.jwt.sessao-maxima-horas:12}") long sessaoMaximaHoras) {

        this.authenticationManager = authenticationManager;
        this.usuarioRepository = usuarioRepository;
        this.jwtService = jwtService;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailSender = mailSender;
        this.frontendUrl = frontendUrl;
        this.mailFrom = mailFrom;
        this.sessaoMaxima = Duration.ofHours(sessaoMaximaHoras);
    }

    public LoginResponseDTO login(LoginRequestDTO dados) {
        String email = normalizarEmail(dados.getEmail());

        // E-mail inexistente, senha errada e conta inativa geram a mesma resposta
        // (ver ApiExceptionHandler) para não revelar quais contas existem.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, dados.getSenha()));

        return emitirToken(email, Instant.now());
    }

    /**
     * Emite um novo token mantendo o início da sessão. Depois de
     * {@code sessaoMaxima} desde o login com senha, é preciso entrar de novo.
     */
    public LoginResponseDTO renovar(String email, Instant inicioSessao) {
        if (inicioSessao == null || inicioSessao.plus(sessaoMaxima).isBefore(Instant.now())) {
            throw new CredentialsExpiredException("Sessão expirada. Faça login novamente.");
        }
        return emitirToken(email, inicioSessao);
    }

    private LoginResponseDTO emitirToken(String email, Instant inicioSessao) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Usuário não encontrado."));

        return new LoginResponseDTO(
                jwtService.gerarToken(usuario.getEmail(), inicioSessao),
                "Bearer",
                new UsuarioResponseDTO(
                        usuario.getId(),
                        usuario.getNomeCompleto(),
                        usuario.getEmail(),
                        usuario.getFuncao(),
                        usuario.getStatus()));
    }

    @Transactional
    public void solicitarRecuperacao(String email) {
        String emailNormalizado = normalizarEmail(email);
        usuarioRepository.findByEmail(emailNormalizado).ifPresent(usuario -> {
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

    /**
     * Falhas de envio são apenas registradas em log: a resposta ao cliente é
     * sempre a mesma, exista ou não a conta.
     */
    private void enviarEmailRecuperacao(String email, String token) {
        if (mailFrom == null || mailFrom.isBlank()) {
            log.warn("Recuperação de senha solicitada, mas o SMTP não está configurado (MAIL_FROM vazio).");
            return;
        }

        SimpleMailMessage mensagem = new SimpleMailMessage();
        mensagem.setFrom(mailFrom);
        mensagem.setTo(email);
        mensagem.setSubject("Redefinição de senha - STECAR");
        mensagem.setText("Recebemos uma solicitação para redefinir sua senha.\n\n"
                + "Acesse este link em até 30 minutos:\n"
                + frontendUrl + "/#/redefinir-senha?token=" + token + "\n\n"
                + "Se você não solicitou esta alteração, ignore este e-mail.");
        try {
            mailSender.send(mensagem);
        } catch (MailException e) {
            log.error("Falha ao enviar e-mail de recuperação de senha.", e);
        }
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }
}
