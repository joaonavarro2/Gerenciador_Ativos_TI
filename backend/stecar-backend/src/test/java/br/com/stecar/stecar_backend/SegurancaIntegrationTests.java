package br.com.stecar.stecar_backend;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import br.com.stecar.stecar_backend.entity.Usuario;
import br.com.stecar.stecar_backend.repository.UsuarioRepository;
import br.com.stecar.stecar_backend.service.JwtService;
import io.jsonwebtoken.Jwts;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SegurancaIntegrationTests {

    private static final String SENHA = "SenhaForte123";

    @Autowired private MockMvc mockMvc;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtService jwtService;
    @Autowired private SecretKey jwtSecretKey;

    private String usuarioComum;
    private String administrador;
    private String administradorGeral;
    private String inativo;

    @BeforeEach
    void criarUsuarios() {
        usuarioComum = criarUsuario("USUARIO", "ATIVO");
        administrador = criarUsuario("ADMINISTRADOR", "ATIVO");
        administradorGeral = criarUsuario("ADMINISTRADOR GERAL", "ATIVO");
        inativo = criarUsuario("ADMINISTRADOR", "INATIVO");
    }

    private String criarUsuario(String funcao, String status) {
        Usuario usuario = new Usuario();
        usuario.setNomeCompleto("Teste " + funcao);
        usuario.setEmail("seg-" + System.nanoTime() + "@exemplo.com");
        usuario.setSenha(passwordEncoder.encode(SENHA));
        usuario.setFuncao(funcao);
        usuario.setStatus(status);
        return usuarioRepository.save(usuario).getEmail();
    }

    private String bearer(String email) {
        return "Bearer " + jwtService.gerarToken(email);
    }

    @Test
    void tokenEmitidoUsaHs512EEhAceitoPelaApi() throws Exception {
        String token = jwtService.gerarToken(usuarioComum);
        String cabecalho = new String(Base64.getUrlDecoder().decode(token.split("\\.")[0]), StandardCharsets.UTF_8);
        Assertions.assertTrue(cabecalho.contains("\"HS512\""), cabecalho);

        mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void tokenAssinadoComOutroAlgoritmoEhRecusado() throws Exception {
        String token = Jwts.builder()
                .subject(administradorGeral)
                .expiration(new Date(System.currentTimeMillis() + 60_000))
                .signWith(jwtSecretKey, Jwts.SIG.HS256)
                .compact();

        mockMvc.perform(get("/api/dashboard").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void requisicaoSemTokenEhRecusada() throws Exception {
        mockMvc.perform(get("/api/pessoas")).andExpect(status().isUnauthorized());
    }

    @Test
    void usuarioComumTemSomenteLeitura() throws Exception {
        mockMvc.perform(get("/api/pessoas").header("Authorization", bearer(usuarioComum)))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/pessoas").header("Authorization", bearer(usuarioComum))
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(post("/api/bens").header("Authorization", bearer(usuarioComum))
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void administradorPodeEscrever() throws Exception {
        // Corpo vazio: passa pela autorização e cai na validação (400), não em 403.
        mockMvc.perform(post("/api/pessoas").header("Authorization", bearer(administrador))
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void gestaoDeUsuariosEhExclusivaDoAdministradorGeral() throws Exception {
        mockMvc.perform(get("/api/usuarios").header("Authorization", bearer(administrador)))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/usuarios").header("Authorization", bearer(administradorGeral)))
                .andExpect(status().isOk());
    }

    @Test
    void usuarioInativoNaoAcessaMesmoComTokenValido() throws Exception {
        mockMvc.perform(get("/api/dashboard").header("Authorization", bearer(inativo)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void cadastroPublicoCriaUsuarioSomenteLeitura() throws Exception {
        String email = "publico-" + System.nanoTime() + "@exemplo.com";
        mockMvc.perform(post("/api/auth/cadastro").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomeCompleto\":\"Público\",\"email\":\"" + email + "\",\"senha\":\"" + SENHA + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.funcao", equalTo("USUARIO")));

        mockMvc.perform(post("/api/escritorios").header("Authorization", bearer(email))
                        .contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void loginNaoRevelaSeOEmailExiste() throws Exception {
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"nao-existe@exemplo.com\",\"senha\":\"qualquer123\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensagem", equalTo("E-mail ou senha inválidos.")));

        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + usuarioComum + "\",\"senha\":\"senhaErrada123\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensagem", equalTo("E-mail ou senha inválidos.")));

        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + usuarioComum + "\",\"senha\":\"" + SENHA + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void recuperacaoDeSenhaRespondeIgualParaQualquerEmail() throws Exception {
        mockMvc.perform(post("/api/auth/recuperar-senha").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"nao-existe@exemplo.com\"}"))
                .andExpect(status().isNoContent());
        mockMvc.perform(post("/api/auth/recuperar-senha").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + usuarioComum + "\"}"))
                .andExpect(status().isNoContent());
    }

    @Test
    void tokenPodeSerRenovado() throws Exception {
        mockMvc.perform(post("/api/auth/renovar").header("Authorization", bearer(usuarioComum)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }
}
