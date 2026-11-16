package br.com.stecar.stecar_backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;

import br.com.stecar.stecar_backend.config.SecurityConfig;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

class JwtServiceTest {

    private SecretKey chave;
    private JwtService jwtService;
    private JwtDecoder jwtDecoder;

    @BeforeEach
    void configurar() {
        chave = Keys.hmacShaKeyFor(new byte[64]);
        jwtService = new JwtService(chave, 30);
        jwtDecoder = new SecurityConfig().jwtDecoder(chave);
    }

    @Test
    void tokenEmitidoUsaHs512TemExpiracaoEAssinaturaValida() {
        Instant inicioSessao = Instant.now().minusSeconds(10);

        Jwt token = jwtDecoder.decode(jwtService.gerarToken("admin@teste.local", inicioSessao));

        assertEquals("HS512", token.getHeaders().get("alg"));
        assertEquals("admin@teste.local", token.getSubject());
        assertEquals(inicioSessao.getEpochSecond(),
                ((Number) token.getClaims().get(JwtService.CLAIM_INICIO_SESSAO)).longValue());
        assertNotNull(token.getExpiresAt());
        assertEquals(30 * 60, token.getExpiresAt().getEpochSecond() - token.getIssuedAt().getEpochSecond());
    }

    @Test
    void decoderRecusaAssinaturaAdulterada() {
        String[] partes = jwtService.gerarToken("admin@teste.local", Instant.now()).split("\\.");
        String assinaturaAdulterada = (partes[2].startsWith("a") ? "b" : "a") + partes[2].substring(1);

        assertThrows(JwtException.class,
                () -> jwtDecoder.decode(partes[0] + "." + partes[1] + "." + assinaturaAdulterada));
    }

    @Test
    void decoderRecusaTokenExpirado() {
        Date expiracao = Date.from(Instant.now().minusSeconds(60));
        String tokenExpirado = Jwts.builder()
                .subject("admin@teste.local")
                .issuedAt(Date.from(Instant.now().minusSeconds(120)))
                .expiration(expiracao)
                .signWith(chave, Jwts.SIG.HS512)
                .compact();

        assertThrows(JwtException.class, () -> jwtDecoder.decode(tokenExpirado));
    }

    @Test
    void decoderRecusaAlgoritmoDiferenteDeHs512() {
        String tokenHs256 = Jwts.builder()
                .subject("admin@teste.local")
                .expiration(Date.from(Instant.now().plusSeconds(60)))
                .signWith(chave, Jwts.SIG.HS256)
                .compact();

        assertThrows(JwtException.class, () -> jwtDecoder.decode(tokenHs256));
    }
}
