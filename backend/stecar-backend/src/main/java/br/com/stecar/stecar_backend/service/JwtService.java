package br.com.stecar.stecar_backend.service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;

@Service
public class JwtService {

    private final SecretKey chave;
    private final Duration validade;

    public JwtService(
            SecretKey jwtSecretKey,
            @Value("${stecar.jwt.expiracao-minutos:30}") long expiracaoMinutos) {
        this.chave = jwtSecretKey;
        this.validade = Duration.ofMinutes(expiracaoMinutos);
    }

    public static final String CLAIM_INICIO_SESSAO = "auth_time";
    public static final String CLAIM_VERSAO_AUTENTICACAO = "auth_ver";

    /** Gera o token; {@code inicioSessao} é o instante do login com senha e é mantido nas renovações. */
    public String gerarToken(String email, Instant inicioSessao) {
        return gerarToken(email, inicioSessao, null);
    }

    public String gerarToken(String email, Instant inicioSessao, Instant senhaAlteradaEm) {
        Instant agora = Instant.now();

        // O algoritmo é fixado para coincidir com o JwtDecoder (HS512).
        var builder = Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(agora))
                .claim(CLAIM_INICIO_SESSAO, inicioSessao.getEpochSecond())
                .expiration(Date.from(agora.plus(validade)));
        if (senhaAlteradaEm != null) {
            builder.claim(CLAIM_VERSAO_AUTENTICACAO, senhaAlteradaEm.toString());
        }
        return builder.signWith(chave, Jwts.SIG.HS512).compact();
    }
}
