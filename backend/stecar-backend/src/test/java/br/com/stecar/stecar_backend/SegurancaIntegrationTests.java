package br.com.stecar.stecar_backend;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

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
import br.com.stecar.stecar_backend.entity.Movimentacao;
import br.com.stecar.stecar_backend.entity.Conserto;
import br.com.stecar.stecar_backend.entity.TokenRecuperacaoSenha;
import br.com.stecar.stecar_backend.dto.ItemRequestDTO;
import br.com.stecar.stecar_backend.dto.UsuarioAtualizarRequestDTO;
import br.com.stecar.stecar_backend.service.ItemService;
import br.com.stecar.stecar_backend.repository.MovimentacaoRepository;
import br.com.stecar.stecar_backend.repository.ConsertoRepository;
import br.com.stecar.stecar_backend.repository.TokenRecuperacaoSenhaRepository;
import br.com.stecar.stecar_backend.repository.UsuarioRepository;
import br.com.stecar.stecar_backend.service.AuthService;
import br.com.stecar.stecar_backend.service.JwtService;
import br.com.stecar.stecar_backend.service.UsuarioService;
import io.jsonwebtoken.Jwts;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class SegurancaIntegrationTests extends PostgresIntegrationTest {

    private static final String SENHA = "SenhaForte123";

    @Autowired private MockMvc mockMvc;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private TokenRecuperacaoSenhaRepository tokenRecuperacaoSenhaRepository;
    @Autowired private MovimentacaoRepository movimentacaoRepository;
    @Autowired private ConsertoRepository consertoRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtService jwtService;
    @Autowired private SecretKey jwtSecretKey;
    @Autowired private UsuarioService usuarioService;
    @Autowired private AuthService authService;
    @Autowired private ItemService itemService;

    private String adminParaTestes;
    private String administrador;
    private String administradorGeral;
    private String inativo;

    @BeforeEach
    void criarUsuarios() {
        administrador = criarUsuario("ADMINISTRADOR", "ATIVO");
        adminParaTestes = administrador;
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
        Instant versaoSenha = usuarioRepository.findByEmail(email).orElseThrow().getSenhaAlteradaEm();
        return "Bearer " + jwtService.gerarToken(email, Instant.now(), versaoSenha);
    }

    private String tokenEmitidoHa(String email, Duration idade, Instant inicioSessao) {
        Instant emissao = Instant.now().minus(idade);
        return Jwts.builder()
                .subject(email)
                .issuedAt(Date.from(emissao))
                .expiration(Date.from(Instant.now().plusSeconds(600)))
                .claim(JwtService.CLAIM_INICIO_SESSAO, inicioSessao.getEpochSecond())
                .claim(JwtService.CLAIM_VERSAO_AUTENTICACAO,
                        usuarioRepository.findByEmail(email).orElseThrow().getSenhaAlteradaEm().toString())
                .signWith(jwtSecretKey, Jwts.SIG.HS512)
                .compact();
    }

    @Test
    void tokenEmitidoAntesDaTrocaDeSenhaEhRecusado() throws Exception {
        String token = tokenEmitidoHa(adminParaTestes, Duration.ofMinutes(1), Instant.now());
        Usuario usuario = usuarioRepository.findByEmail(adminParaTestes).orElseThrow();
        usuario.setSenha(passwordEncoder.encode("NovaSenha123"));
        usuarioRepository.save(usuario);

        mockMvc.perform(get("/api/dashboard").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/auth/renovar").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void renovacaoExigeNovoLoginAposSessaoMaxima() throws Exception {
        String token = tokenEmitidoHa(adminParaTestes, Duration.ZERO, Instant.now().minus(Duration.ofHours(13)));
        mockMvc.perform(get("/api/dashboard").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/auth/renovar").header("Authorization", "Bearer " + token))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokenEmitidoUsaHs512EEhAceitoPelaApi() throws Exception {
        String token = jwtService.gerarToken(adminParaTestes, Instant.now(),
                usuarioRepository.findByEmail(adminParaTestes).orElseThrow().getSenhaAlteradaEm());
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
    void endpointProtegidoRecusaTokenMalformadoAdulteradoEExpirado() throws Exception {
        mockMvc.perform(get("/api/dashboard").header("Authorization", "Bearer invalido"))
                .andExpect(status().isUnauthorized());

        String[] partes = bearer(administrador).substring("Bearer ".length()).split("\\.");
        String assinaturaAdulterada = (partes[2].startsWith("a") ? "b" : "a") + partes[2].substring(1);
        mockMvc.perform(get("/api/dashboard")
                        .header("Authorization", "Bearer " + partes[0] + "." + partes[1] + "." + assinaturaAdulterada))
                .andExpect(status().isUnauthorized());

        Usuario usuario = usuarioRepository.findByEmail(administrador).orElseThrow();
        Instant agora = Instant.now();
        String tokenExpirado = Jwts.builder()
                .subject(administrador)
                .issuedAt(Date.from(agora.minus(Duration.ofMinutes(2))))
                .expiration(Date.from(agora.minus(Duration.ofMinutes(1))))
                .claim(JwtService.CLAIM_INICIO_SESSAO, agora.minus(Duration.ofMinutes(2)).getEpochSecond())
                .claim(JwtService.CLAIM_VERSAO_AUTENTICACAO, usuario.getSenhaAlteradaEm().toString())
                .signWith(jwtSecretKey, Jwts.SIG.HS512)
                .compact();
        mockMvc.perform(get("/api/dashboard").header("Authorization", "Bearer " + tokenExpirado))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void requisicaoSemTokenEhRecusada() throws Exception {
        mockMvc.perform(get("/api/pessoas")).andExpect(status().isUnauthorized());
    }

    @Test
    void ambosOsPerfisAcessamOperacoesPatrimoniaisEPodemConsultarUsuarios() throws Exception {
        for (String token : new String[] {bearer(administrador), bearer(administradorGeral)}) {
            mockMvc.perform(get("/api/dashboard").header("Authorization", token))
                    .andExpect(status().isOk());
            mockMvc.perform(get("/api/pessoas").header("Authorization", token))
                    .andExpect(status().isOk());
            mockMvc.perform(post("/api/pessoas").header("Authorization", token)
                            .contentType(MediaType.APPLICATION_JSON).content("{}"))
                    .andExpect(status().isBadRequest());
            for (String recurso : new String[] {
                    "bens", "outros-itens", "consertos", "escritorios", "departamentos"
            }) {
                mockMvc.perform(get("/api/" + recurso).header("Authorization", token))
                        .andExpect(status().isOk());
                mockMvc.perform(post("/api/" + recurso).header("Authorization", token)
                                .contentType(MediaType.APPLICATION_JSON).content("{}"))
                        .andExpect(status().isBadRequest());
            }
            mockMvc.perform(get("/api/movimentacoes").header("Authorization", token))
                    .andExpect(status().isOk());
            mockMvc.perform(delete("/api/movimentacoes/999999").header("Authorization", token))
                    .andExpect(status().isNotFound());
            mockMvc.perform(get("/api/usuarios").header("Authorization", token))
                    .andExpect(status().isOk());
        }
    }

    @Test
    void administradorNaoPodeCadastrarEditarDesativarOuExcluirUsuarios() throws Exception {
        Long alvoId = usuarioRepository.findByEmail(administradorGeral).orElseThrow().getId();
        String payload = "{\"nomeCompleto\":\"Alterado\",\"email\":\"" + administradorGeral
                + "\",\"funcao\":\"ADMINISTRADOR\"}";

        mockMvc.perform(post("/api/usuarios").header("Authorization", bearer(administrador))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomeCompleto\":\"Novo\",\"email\":\"novo-" + System.nanoTime()
                                + "@exemplo.com\",\"senha\":\"SenhaForte123\",\"funcao\":\"ADMINISTRADOR\"}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(put("/api/usuarios/" + alvoId).header("Authorization", bearer(administrador))
                        .contentType(MediaType.APPLICATION_JSON).content(payload))
                .andExpect(status().isForbidden());
        mockMvc.perform(patch("/api/usuarios/" + alvoId + "/status?status=INATIVO")
                        .header("Authorization", bearer(administrador)))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/usuarios/" + alvoId).header("Authorization", bearer(administrador)))
                .andExpect(status().isForbidden());
    }

    @Test
    void administradorGeralPodeCadastrarUsuarioComUmaDasDuasFuncoes() throws Exception {
        String email = "novo-" + System.nanoTime() + "@exemplo.com";
        mockMvc.perform(post("/api/usuarios").header("Authorization", bearer(administradorGeral))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomeCompleto\":\"Novo Admin\",\"email\":\"" + email
                                + "\",\"senha\":\"SenhaForte123\",\"funcao\":\"ADMINISTRADOR\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.funcao", equalTo("ADMINISTRADOR")))
                .andExpect(jsonPath("$.senha").doesNotExist());
        Usuario criado = usuarioRepository.findByEmail(email).orElseThrow();
        Assertions.assertNotEquals(SENHA, criado.getSenha());
        Assertions.assertTrue(passwordEncoder.matches(SENHA, criado.getSenha()));
    }

    @Test
    void payloadNaoPodeAtribuirFuncaoLegadaEConsultaDeUsuarioNaoExibeHash() throws Exception {
        String email = "funcao-invalida-" + System.nanoTime() + "@exemplo.com";
        mockMvc.perform(post("/api/usuarios").header("Authorization", bearer(administradorGeral))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomeCompleto\":\"Usuário inválido\",\"email\":\"" + email
                                + "\",\"senha\":\"SenhaForte123\",\"funcao\":\"USUARIO\"}"))
                .andExpect(status().isBadRequest());
        Assertions.assertFalse(usuarioRepository.existsByEmail(email));

        Long id = usuarioRepository.findByEmail(administrador).orElseThrow().getId();
        mockMvc.perform(get("/api/usuarios/" + id).header("Authorization", bearer(administradorGeral)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.senha").doesNotExist())
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void alteracaoDeFuncaoRevogaTokenAnteriorDoUsuario() throws Exception {
        Long id = usuarioRepository.findByEmail(adminParaTestes).orElseThrow().getId();
        String tokenAnterior = bearer(adminParaTestes);
        mockMvc.perform(put("/api/usuarios/" + id).header("Authorization", bearer(administradorGeral))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomeCompleto\":\"Admin promovido\",\"email\":\"" + adminParaTestes
                                + "\",\"funcao\":\"ADMINISTRADOR GERAL\"}"))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/dashboard").header("Authorization", tokenAnterior))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void administradorGeralPodeEditarAlterarFuncaoEDesativarOutrosUsuarios() throws Exception {
        Long id = usuarioRepository.findByEmail(administrador).orElseThrow().getId();
        mockMvc.perform(put("/api/usuarios/" + id).header("Authorization", bearer(administradorGeral))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomeCompleto\":\"Nome atualizado\",\"email\":\"" + administrador
                                + "\",\"funcao\":\"ADMINISTRADOR GERAL\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.funcao", equalTo("ADMINISTRADOR GERAL")));
        mockMvc.perform(patch("/api/usuarios/" + id + "/status?status=INATIVO")
                        .header("Authorization", bearer(administradorGeral)))
                .andExpect(status().isOk());
    }

    @Test
    void administradorGeralPodeExcluirUsuarioSemHistorico() throws Exception {
        String email = criarUsuario("ADMINISTRADOR", "ATIVO");
        Long id = usuarioRepository.findByEmail(email).orElseThrow().getId();
        mockMvc.perform(delete("/api/usuarios/" + id).header("Authorization", bearer(administradorGeral)))
                .andExpect(status().isNoContent());
        Assertions.assertFalse(usuarioRepository.existsById(id));
    }

    @Test
    void exclusaoDeUsuarioComHistoricoPreservaRegistrosEOrientarDesativacao() throws Exception {
        Usuario alvo = usuarioRepository.findByEmail(administrador).orElseThrow();
        var opcoes = itemService.buscarOpcoes();
        var escritorio = opcoes.escritorios().get(0);
        var departamento = opcoes.departamentos().get(0);
        Long itemId = itemService.criar(new ItemRequestDTO(
                "HIST-" + System.nanoTime(), "HIST-PAT", "Bem de teste de histórico", "Teste",
                "HIST-SERIAL", "Fabricante", "Modelo", null, "Ativo", java.time.LocalDate.now(),
                escritorio.id(), departamento.id(), null)).id();

        Movimentacao movimentacao = new Movimentacao();
        movimentacao.setTipo("TRANSFERENCIA");
        movimentacao.setData(java.time.LocalDateTime.now());
        movimentacao.setStatus("CONCLUIDA");
        movimentacao.setItemId(itemId);
        movimentacao.setUsuarioId(alvo.getId());
        movimentacaoRepository.save(movimentacao);

        Conserto conserto = new Conserto();
        conserto.setTipo("MANUTENCAO");
        conserto.setDescricaoProblema("Registro de teste");
        conserto.setDataEntrada(java.time.LocalDateTime.now());
        conserto.setStatus("CONCLUIDO");
        conserto.setTecnico("Técnico de teste");
        conserto.setItemId(itemId);
        conserto.setUsuarioId(alvo.getId());
        consertoRepository.save(conserto);

        mockMvc.perform(delete("/api/usuarios/" + alvo.getId())
                        .header("Authorization", bearer(administradorGeral)))
                .andExpect(status().isConflict());
        Assertions.assertTrue(usuarioRepository.existsById(alvo.getId()));
        Assertions.assertEquals(1, movimentacaoRepository.countByUsuarioId(alvo.getId()));
        Assertions.assertEquals(1, consertoRepository.countByUsuarioId(alvo.getId()));
    }

    @Test
    void administradorGeralNaoPodeAlterarOuExcluirPropriaConta() throws Exception {
        Long id = usuarioRepository.findByEmail(administradorGeral).orElseThrow().getId();
        mockMvc.perform(put("/api/usuarios/" + id).header("Authorization", bearer(administradorGeral))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomeCompleto\":\"Admin\",\"email\":\"" + administradorGeral
                                + "\",\"funcao\":\"ADMINISTRADOR\"}"))
                .andExpect(status().isForbidden());
        mockMvc.perform(patch("/api/usuarios/" + id + "/status?status=INATIVO")
                        .header("Authorization", bearer(administradorGeral)))
                .andExpect(status().isForbidden());
        mockMvc.perform(delete("/api/usuarios/" + id).header("Authorization", bearer(administradorGeral)))
                .andExpect(status().isForbidden());
    }

    @Test
    void endpointsDeUsuarioImpedemDesativarRebaixarOuExcluirUltimoAdministradorGeral() throws Exception {
        Long id = usuarioRepository.findByEmail(administradorGeral).orElseThrow().getId();
        usuarioRepository.findAll().stream()
                .filter(usuario -> !usuario.getId().equals(id)
                        && "ADMINISTRADOR GERAL".equals(usuario.getFuncao())
                        && "ATIVO".equals(usuario.getStatus()))
                .forEach(usuario -> {
                    usuario.setFuncao("ADMINISTRADOR");
                    usuarioRepository.save(usuario);
                });

        mockMvc.perform(patch("/api/usuarios/" + id + "/status?status=INATIVO")
                        .header("Authorization", bearer(administrador)))
                .andExpect(status().isConflict());
        mockMvc.perform(put("/api/usuarios/" + id).header("Authorization", bearer(administrador))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomeCompleto\":\"Admin geral\",\"email\":\"" + administradorGeral
                                + "\",\"funcao\":\"ADMINISTRADOR\"}"))
                .andExpect(status().isConflict());
        mockMvc.perform(delete("/api/usuarios/" + id).header("Authorization", bearer(administrador)))
                .andExpect(status().isConflict());
        Assertions.assertEquals("ATIVO", usuarioRepository.findById(id).orElseThrow().getStatus());
        Assertions.assertEquals("ADMINISTRADOR GERAL",
                usuarioRepository.findById(id).orElseThrow().getFuncao());
    }

    @Test
    void operacoesDeAutenticacaoContinuamValidasParaAmbosOsPerfis() throws Exception {
        for (String email : new String[] {administrador, administradorGeral}) {
            mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                            .content("{\"email\":\"" + email + "\",\"senha\":\"" + SENHA + "\"}"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.token").isNotEmpty())
                    .andExpect(jsonPath("$.usuario.senha").doesNotExist());
        }
    }

    @Test
    void servicoImpedeRemoverUltimoAdministradorGeralAtivo() {
        Long id = usuarioRepository.findByEmail(administradorGeral).orElseThrow().getId();
        usuarioRepository.findAll().stream()
                .filter(usuario -> !usuario.getId().equals(id)
                        && "ADMINISTRADOR GERAL".equals(usuario.getFuncao())
                        && "ATIVO".equals(usuario.getStatus()))
                .forEach(usuario -> {
                    usuario.setFuncao("ADMINISTRADOR");
                    usuarioRepository.save(usuario);
                });
        org.springframework.web.server.ResponseStatusException excecao = Assertions.assertThrows(
                org.springframework.web.server.ResponseStatusException.class,
                () -> usuarioService.alterarStatus(id, "INATIVO", administrador));
        Assertions.assertEquals(409, excecao.getStatusCode().value());
        Assertions.assertEquals("ATIVO", usuarioRepository.findById(id).orElseThrow().getStatus());

        excecao = Assertions.assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> usuarioService.atualizar(id,
                        new UsuarioAtualizarRequestDTO("Administrador", administradorGeral, "ADMINISTRADOR"),
                        administrador));
        Assertions.assertEquals(409, excecao.getStatusCode().value());
        Assertions.assertEquals("ADMINISTRADOR GERAL",
                usuarioRepository.findById(id).orElseThrow().getFuncao());

        excecao = Assertions.assertThrows(org.springframework.web.server.ResponseStatusException.class,
                () -> usuarioService.excluir(id, administrador));
        Assertions.assertEquals(409, excecao.getStatusCode().value());
        Assertions.assertTrue(usuarioRepository.existsById(id));
    }

    @Test
    void alterarSenhaConfereSenhaAtualEInvalidaTokenAnterior() throws Exception {
        String tokenAntigo = bearer(administrador);
        mockMvc.perform(put("/api/auth/alterar-senha").header("Authorization", tokenAntigo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"senhaAtual\":\"incorreta123\",\"novaSenha\":\"NovaSenha123\",\"confirmarSenha\":\"NovaSenha123\"}"))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/api/auth/alterar-senha").header("Authorization", tokenAntigo)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"senhaAtual\":\"" + SENHA + "\",\"novaSenha\":\"NovaSenha123\",\"confirmarSenha\":\"NovaSenha123\"}"))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/dashboard").header("Authorization", tokenAntigo))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + administrador + "\",\"senha\":\"" + SENHA + "\"}"))
                .andExpect(status().isUnauthorized());
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + administrador + "\",\"senha\":\"NovaSenha123\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void usuarioInativoNaoAcessaMesmoComTokenValido() throws Exception {
        mockMvc.perform(get("/api/dashboard").header("Authorization", bearer(inativo)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void cadastroPublicoEstaBloqueado() throws Exception {
        mockMvc.perform(post("/api/auth/cadastro").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nomeCompleto\":\"Público\",\"email\":\"publico-"
                                + System.nanoTime() + "@exemplo.com\",\"senha\":\"" + SENHA + "\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginNaoRevelaSeOEmailExiste() throws Exception {
        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"nao-existe@exemplo.com\",\"senha\":\"qualquer123\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensagem", equalTo("E-mail ou senha inválidos.")));

        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + adminParaTestes + "\",\"senha\":\"senhaErrada123\"}"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensagem", equalTo("E-mail ou senha inválidos.")));

        mockMvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + adminParaTestes + "\",\"senha\":\"" + SENHA + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }

    @Test
    void recuperacaoDeSenhaRespondeIgualParaQualquerEmail() throws Exception {
        mockMvc.perform(post("/api/auth/recuperar-senha").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"nao-existe@exemplo.com\"}"))
                .andExpect(status().isNoContent());
        mockMvc.perform(post("/api/auth/recuperar-senha").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + adminParaTestes + "\"}"))
                .andExpect(status().isNoContent());
    }

    @Test
    void tokenDeRecuperacaoEhArmazenadoComHashEConsumidoUmaUnicaVez() throws Exception {
        String valorOriginal = "token-de-teste-" + UUID.randomUUID();
        Usuario usuario = usuarioRepository.findByEmail(adminParaTestes).orElseThrow();
        TokenRecuperacaoSenha recuperacao = new TokenRecuperacaoSenha();
        recuperacao.setToken(hashParaTeste(valorOriginal));
        recuperacao.setUsuario(usuario);
        recuperacao.setExpiraEm(Instant.now().plus(Duration.ofMinutes(20)));
        recuperacao.setUsado(false);
        recuperacao = tokenRecuperacaoSenhaRepository.saveAndFlush(recuperacao);
        Long tokenId = recuperacao.getId();

        authService.redefinirSenha(valorOriginal, "SenhaRedefinida123");

        TokenRecuperacaoSenha consumido = tokenRecuperacaoSenhaRepository.findById(tokenId).orElseThrow();
        Assertions.assertNotEquals(valorOriginal, consumido.getToken());
        Assertions.assertTrue(consumido.isUsado());
        Assertions.assertTrue(passwordEncoder.matches("SenhaRedefinida123",
                usuarioRepository.findByEmail(adminParaTestes).orElseThrow().getSenha()));
        org.springframework.web.server.ResponseStatusException excecao = Assertions.assertThrows(
                org.springframework.web.server.ResponseStatusException.class,
                () -> authService.redefinirSenha(valorOriginal, "OutraSenha123"));
        Assertions.assertEquals(400, excecao.getStatusCode().value());
    }

    @Test
    void endpointDeRecuperacaoRecusaTokenExpiradoEAdulterado() throws Exception {
        Usuario usuario = usuarioRepository.findByEmail(adminParaTestes).orElseThrow();
        String expirado = "token-expirado-" + UUID.randomUUID();
        TokenRecuperacaoSenha registroExpirado = new TokenRecuperacaoSenha();
        registroExpirado.setToken(hashParaTeste(expirado));
        registroExpirado.setUsuario(usuario);
        registroExpirado.setExpiraEm(Instant.now().minusSeconds(1));
        registroExpirado.setUsado(false);
        tokenRecuperacaoSenhaRepository.save(registroExpirado);

        mockMvc.perform(post("/api/auth/redefinir-senha").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"" + expirado + "\",\"novaSenha\":\"NovaSenha123\"}"))
                .andExpect(status().isBadRequest());

        String original = "token-valido-" + UUID.randomUUID();
        TokenRecuperacaoSenha registroValido = new TokenRecuperacaoSenha();
        registroValido.setToken(hashParaTeste(original));
        registroValido.setUsuario(usuario);
        registroValido.setExpiraEm(Instant.now().plus(Duration.ofMinutes(20)));
        registroValido.setUsado(false);
        tokenRecuperacaoSenhaRepository.save(registroValido);

        mockMvc.perform(post("/api/auth/redefinir-senha").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"token\":\"" + original + "-adulterado\",\"novaSenha\":\"NovaSenha123\"}"))
                .andExpect(status().isBadRequest());
        Assertions.assertFalse(registroValido.isUsado());
    }

    private String hashParaTeste(String valor) throws Exception {
        byte[] hash = java.security.MessageDigest.getInstance("SHA-256")
                .digest(valor.getBytes(StandardCharsets.UTF_8));
        return java.util.HexFormat.of().formatHex(hash);
    }

    @Test
    void tokenPodeSerRenovado() throws Exception {
        mockMvc.perform(post("/api/auth/renovar").header("Authorization", bearer(adminParaTestes)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());
    }
}
