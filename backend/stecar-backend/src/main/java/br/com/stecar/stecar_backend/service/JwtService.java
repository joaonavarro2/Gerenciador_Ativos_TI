package br.com.stecar.stecar_backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.nio.charset.StandardCharsets;
import java.util.Date;

@Service
public class JwtService {

    @Value("${stecar.jwt.secret}")
    private String secret;

    // Tempo de validade do token: 15 minutos
    private final long tempoExpiracao = 15 * 60 * 1000;

    /**
     * Gera o JWT para o usuário autenticado.
     */
    public String gerarToken(UserDetails usuario) {

        Date agora = new Date();

        Date expiracao = new Date(
                agora.getTime() + tempoExpiracao
        );

        return Jwts.builder()
                .subject(usuario.getUsername())
                .issuedAt(agora)
                .expiration(expiracao)
                .signWith(getChave())
                .compact();
    }

    /**
     * Cria a chave utilizada para assinar o JWT.
     */
    private SecretKey getChave() {

        byte[] chaveBytes = secret.getBytes(
                StandardCharsets.UTF_8
        );

        return Keys.hmacShaKeyFor(chaveBytes);
    }
}