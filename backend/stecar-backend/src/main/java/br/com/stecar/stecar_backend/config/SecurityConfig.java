package br.com.stecar.stecar_backend.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import io.jsonwebtoken.security.Keys;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
public class SecurityConfig {

    @Value("${stecar.jwt.secret}")
    private String secret;

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            // Como a API utiliza JWT, não precisamos de sessão tradicional.
            .csrf(csrf -> csrf.disable())

            // Configura a aplicação para trabalhar de forma stateless.
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

            // Define quais endpoints precisam de autenticação.
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )

            // Configura o Spring Security para validar os JWTs recebidos.
            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt -> {})
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public JwtDecoder jwtDecoder() {

        byte[] chaveBytes =
                secret.getBytes(StandardCharsets.UTF_8);

        SecretKey chave =
                Keys.hmacShaKeyFor(chaveBytes);

        return NimbusJwtDecoder
                .withSecretKey(chave)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }
}