package br.com.stecar.stecar_backend.config;

import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import io.jsonwebtoken.security.Keys;

@Configuration
public class SecurityConfig {

    /** HS512 exige chave de pelo menos 512 bits. */
    static final int TAMANHO_MINIMO_CHAVE_BYTES = 64;

    static final String[] PAPEIS_ESCRITA = {"ADMINISTRADOR", "ADMINISTRADOR_GERAL"};

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            UserDetailsService usuarioDetailsService) throws Exception {

        http
            // API stateless com JWT: não há sessão nem cookie para proteger com CSRF.
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/api/auth/login",
                    "/api/auth/cadastro",
                    "/api/auth/recuperar-senha",
                    "/api/auth/redefinir-senha",
                    "/error"
                ).permitAll()
                .requestMatchers("/api/usuarios/**").hasAuthority("ADMINISTRADOR_GERAL")
                .requestMatchers("/api/auth/**").authenticated()
                // Excluir histórico de movimentação é restrito ao administrador geral.
                .requestMatchers(HttpMethod.DELETE, "/api/movimentacoes/**").hasAuthority("ADMINISTRADOR_GERAL")
                .requestMatchers(HttpMethod.GET, "/api/**").authenticated()
                .requestMatchers("/api/**").hasAnyAuthority(PAPEIS_ESCRITA)
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt ->
                jwt.jwtAuthenticationConverter(token -> {
                    // Papel e status vêm do banco a cada requisição: alterações e
                    // desativações têm efeito imediato, sem esperar o token expirar.
                    UserDetails usuario = usuarioDetailsService.loadUserByUsername(token.getSubject());
                    if (!usuario.isEnabled()) {
                        throw new DisabledException("Usuário inativo.");
                    }
                    return new JwtAuthenticationToken(token, usuario.getAuthorities(), usuario.getUsername());
                })
            ));

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("http://localhost:*", "http://127.0.0.1:*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
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
    public SecretKey jwtSecretKey(@Value("${stecar.jwt.secret}") String secret) {
        byte[] chaveBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (chaveBytes.length < TAMANHO_MINIMO_CHAVE_BYTES) {
            throw new IllegalStateException(
                    "STECAR_JWT_SECRET deve ter pelo menos " + TAMANHO_MINIMO_CHAVE_BYTES
                    + " bytes (atual: " + chaveBytes.length + ").");
        }
        return Keys.hmacShaKeyFor(chaveBytes);
    }

    @Bean
    public JwtDecoder jwtDecoder(SecretKey jwtSecretKey) {
        return NimbusJwtDecoder
                .withSecretKey(jwtSecretKey)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }
}
