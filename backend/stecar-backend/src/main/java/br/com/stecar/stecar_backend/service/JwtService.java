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

    public String gerarToken(String email) {
        Instant agora = Instant.now();

        // O algoritmo é fixado para coincidir com o JwtDecoder (HS512).
        return Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(agora))
                .expiration(Date.from(agora.plus(validade)))
                .signWith(chave, Jwts.SIG.HS512)
                .compact();
    }
}
