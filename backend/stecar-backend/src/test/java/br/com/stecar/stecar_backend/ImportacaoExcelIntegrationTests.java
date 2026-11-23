package br.com.stecar.stecar_backend;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

import br.com.stecar.stecar_backend.entity.Departamento;
import br.com.stecar.stecar_backend.entity.Escritorio;
import br.com.stecar.stecar_backend.entity.Pessoa;
import br.com.stecar.stecar_backend.entity.Usuario;
import br.com.stecar.stecar_backend.repository.DepartamentoRepository;
import br.com.stecar.stecar_backend.repository.EscritorioRepository;
import br.com.stecar.stecar_backend.repository.PessoaRepository;
import br.com.stecar.stecar_backend.repository.UsuarioRepository;
import br.com.stecar.stecar_backend.service.JwtService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ImportacaoExcelIntegrationTests extends PostgresIntegrationTest {

    @Autowired private MockMvc mockMvc;
    private final ObjectMapper objectMapper = JsonMapper.builder().build();
    @Autowired private JdbcTemplate jdbc;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private EscritorioRepository escritorioRepository;
    @Autowired private DepartamentoRepository departamentoRepository;
    @Autowired private PessoaRepository pessoaRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtService jwtService;

    private String prefixo;
    private String email;
    private String token;
    private Long escritorioId;
    private Long departamentoId;
    private Long pessoaId;

    @BeforeEach
    void prepararMassaFicticia() {
        prefixo = "TESTE_EXCEL_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        email = prefixo.toLowerCase() + "@stecar.test";
        Usuario usuario = new Usuario();
        usuario.setNomeCompleto(prefixo);
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode("SenhaForte123"));
        usuario.setFuncao("ADMINISTRADOR GERAL");
        usuario.setStatus("ATIVO");
        usuario = usuarioRepository.save(usuario);
        token = "Bearer " + jwtService.gerarToken(email, Instant.now(), usuario.getSenhaAlteradaEm());

        Escritorio escritorio = new Escritorio();
        escritorio.setNome(prefixo + " Escritório");
        escritorio.setCidade("Cidade Teste");
        escritorio.setCep("00000000");
        escritorio.setEndereco("Endereço de teste");
        escritorio.setStatus("ATIVO");
        escritorioId = escritorioRepository.save(escritorio).getId();

        Departamento departamento = new Departamento();
        departamento.setNome(prefixo + " Departamento");
        departamento.setStatus("ATIVO");
        departamento.setEscritorioId(escritorioId);
        departamentoId = departamentoRepository.save(departamento).getId();

        Pessoa pessoa = new Pessoa();
        pessoa.setNomeCompleto(prefixo + " Responsável");
        pessoa.setEmail(email);
        pessoa.setCargo("Analista de teste");
        pessoa.setEscritorioId(escritorioId);
        pessoa.setDepartamentoId(departamentoId);
        pessoa.setStatus("ATIVO");
        pessoaId = pessoaRepository.save(pessoa).getId();
    }

    @Test
    void validaSemGravarDepoisConfirmaPersisteRelacionamentosEApiRetornaDados() throws Exception {
        Map<String, String> dados = linhaBem(prefixo + "_001", "NOTEBOOK-001", "SN-001");
        dados.put("nome", "Notebook de teste – São João");
        dados.put("descricao", "Equipamento de automação com acentuação çãõ");
        dados.put("responsavel", prefixo + " Responsável");
        String request = request(List.of(dados));

        mockMvc.perform(post("/api/importacao-excel/bens/validar").header("Authorization", token)
                        .contentType("application/json").content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRegistros", equalTo(1)))
                .andExpect(jsonPath("$.validos", equalTo(1)))
                .andExpect(jsonPath("$.importados", equalTo(0)))
                .andExpect(jsonPath("$.rejeitados", equalTo(0)));
        org.junit.jupiter.api.Assertions.assertEquals(0, contarCodigo(prefixo + "_001"));

        mockMvc.perform(post("/api/importacao-excel/bens/confirmar").header("Authorization", token)
                        .contentType("application/json").content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.importados", equalTo(1)))
                .andExpect(jsonPath("$.linhas[0].id").isNumber());

        Map<String, Object> gravado = jdbc.queryForMap("""
                SELECT i.codigo, i.patrimonio, i.serial, i.nome, i.descricao, i.status,
                       i.data_aquisicao, i.escritorio_id, i.departamento_id, i.pessoa_id,
                       e.nome AS escritorio_nome, d.nome AS departamento_nome, p.nome_completo AS pessoa_nome
                FROM item i JOIN escritorio e ON e.id=i.escritorio_id
                JOIN departamento d ON d.id=i.departamento_id
                LEFT JOIN pessoa p ON p.id=i.pessoa_id WHERE i.codigo=?
                """, prefixo + "_001");
        org.junit.jupiter.api.Assertions.assertEquals("Notebook de teste – São João", gravado.get("nome"));
        org.junit.jupiter.api.Assertions.assertEquals("Equipamento de automação com acentuação çãõ", gravado.get("descricao"));
        org.junit.jupiter.api.Assertions.assertEquals(LocalDate.of(2024, 2, 29).toString(), gravado.get("data_aquisicao").toString());
        org.junit.jupiter.api.Assertions.assertEquals(escritorioId, ((Number) gravado.get("escritorio_id")).longValue());
        org.junit.jupiter.api.Assertions.assertEquals(departamentoId, ((Number) gravado.get("departamento_id")).longValue());
        org.junit.jupiter.api.Assertions.assertEquals(pessoaId, ((Number) gravado.get("pessoa_id")).longValue());

        mockMvc.perform(get("/api/bens").param("busca", prefixo + "_001").header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo", equalTo(prefixo + "_001")))
                .andExpect(jsonPath("$[0].patrimonio", equalTo("NOTEBOOK-001")))
                .andExpect(jsonPath("$[0].serial", equalTo("SN-001")))
                .andExpect(jsonPath("$[0].responsavel", equalTo(prefixo + " Responsável")));
    }

    @Test
    void importaDezLinhasComUmaInvalidaERejeitaSomenteALinhaInvalida() throws Exception {
        List<Map<String, String>> linhas = new java.util.ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Map<String, String> dados = linhaBem(prefixo + "_MIX_" + i, "", "");
            if (i == 4) dados.put("nome", " ");
            linhas.add(dados);
        }
        mockMvc.perform(post("/api/importacao-excel/bens/confirmar").header("Authorization", token)
                        .contentType("application/json").content(request(linhas)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRegistros", equalTo(10)))
                .andExpect(jsonPath("$.validos", equalTo(9)))
                .andExpect(jsonPath("$.importados", equalTo(9)))
                .andExpect(jsonPath("$.rejeitados", equalTo(1)))
                .andExpect(jsonPath("$.linhas[3].erros[0]").isNotEmpty());
        org.junit.jupiter.api.Assertions.assertEquals(9, contarPrefixo(prefixo + "_MIX_%"));
        org.junit.jupiter.api.Assertions.assertEquals(0, contarCodigo(prefixo + "_MIX_4"));
    }

    @Test
    void importaLoteMaiorComPatrimonioESerieOpcionaisVazios() throws Exception {
        List<Map<String, String>> linhas = new java.util.ArrayList<>();
        for (int i = 1; i <= 250; i++) {
            linhas.add(linhaBem(prefixo + "_VOL_" + i, "", ""));
        }
        mockMvc.perform(post("/api/importacao-excel/bens/confirmar").header("Authorization", token)
                        .contentType("application/json").content(request(linhas)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalRegistros", equalTo(250)))
                .andExpect(jsonPath("$.validos", equalTo(250)))
                .andExpect(jsonPath("$.importados", equalTo(250)))
                .andExpect(jsonPath("$.rejeitados", equalTo(0)));
        org.junit.jupiter.api.Assertions.assertEquals(250, contarPrefixo(prefixo + "_VOL_%"));
        org.junit.jupiter.api.Assertions.assertEquals(0, jdbc.queryForObject(
                "SELECT COUNT(*) FROM item WHERE codigo LIKE ? AND (patrimonio IS NOT NULL OR serial IS NOT NULL)",
                Integer.class, prefixo + "_VOL_%"));
    }

    @Test
    void importaCategoriasStatusesEFormatosDeDataAceitos() throws Exception {
        Map<String, String> notebook = linhaBem(prefixo + "_CAT_1", "", "SN-CATEGORIA-1");
        Map<String, String> monitor = linhaBem(prefixo + "_CAT_2", "PAT-CATEGORIA-2", "SN-CATEGORIA-2");
        monitor.put("categoria", "Monitor");
        monitor.put("status", "Inativo");
        monitor.put("dataAquisicao", "29/02/2024");
        mockMvc.perform(post("/api/importacao-excel/bens/confirmar").header("Authorization", token)
                        .contentType("application/json").content(request(List.of(notebook, monitor))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.importados", equalTo(2)))
                .andExpect(jsonPath("$.rejeitados", equalTo(0)));
        org.junit.jupiter.api.Assertions.assertEquals(2, contarPrefixo(prefixo + "_CAT_%"));
        org.junit.jupiter.api.Assertions.assertEquals("Monitor", jdbc.queryForObject(
                "SELECT categoria FROM item WHERE codigo=?", String.class, prefixo + "_CAT_2"));
        org.junit.jupiter.api.Assertions.assertEquals("Inativo", jdbc.queryForObject(
                "SELECT status FROM item WHERE codigo=?", String.class, prefixo + "_CAT_2"));
        org.junit.jupiter.api.Assertions.assertEquals("2024-02-29", jdbc.queryForObject(
                "SELECT data_aquisicao::text FROM item WHERE codigo=?", String.class, prefixo + "_CAT_2"));
    }

    @Test
    void trataReimportacaoComoDuplicidadeENaoCriaSegundaCopia() throws Exception {
        String request = request(List.of(linhaBem(prefixo + "_REPETIDO", "PAT-REPETIDO", "SERIE-REPETIDA")));
        mockMvc.perform(post("/api/importacao-excel/bens/confirmar").header("Authorization", token)
                        .contentType("application/json").content(request))
                .andExpect(status().isOk()).andExpect(jsonPath("$.importados", equalTo(1)));
        mockMvc.perform(post("/api/importacao-excel/bens/confirmar").header("Authorization", token)
                        .contentType("application/json").content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.importados", equalTo(0)))
                .andExpect(jsonPath("$.rejeitados", equalTo(1)));
        org.junit.jupiter.api.Assertions.assertEquals(1, contarCodigo(prefixo + "_REPETIDO"));
    }

    @Test
    void importaOutrosItensComEscritorioResolvidoENaoGravaQuantidadeNegativa() throws Exception {
        Map<String, String> valido = new LinkedHashMap<>();
        valido.put("codigo", prefixo + "_ESTOQUE_1");
        valido.put("patrimonio", "");
        valido.put("instituicao", "FLEM");
        valido.put("nome", "Papel A4 de teste");
        valido.put("categoria", "Material de escritório");
        valido.put("escritorio", prefixo + " Escritório");
        valido.put("localizacao", "Almoxarifado teste");
        valido.put("quantidade", "12");
        valido.put("estoqueMinimo", "3");
        valido.put("fornecedor", "Fornecedor de teste");
        valido.put("status", "Ativo");
        Map<String, String> invalido = new LinkedHashMap<>(valido);
        invalido.put("codigo", prefixo + "_ESTOQUE_NEGATIVO");
        invalido.put("quantidade", "-1");

        mockMvc.perform(post("/api/importacao-excel/outros-itens/confirmar").header("Authorization", token)
                        .contentType("application/json").content(request(List.of(valido, invalido))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.validos", equalTo(1)))
                .andExpect(jsonPath("$.importados", equalTo(1)))
                .andExpect(jsonPath("$.rejeitados", equalTo(1)));
        Map<String, Object> gravado = jdbc.queryForMap(
                "SELECT codigo, instituicao, quantidade, estoque_minimo, escritorio_id FROM outro_item WHERE codigo=?",
                prefixo + "_ESTOQUE_1");
        org.junit.jupiter.api.Assertions.assertEquals("FLEM", gravado.get("instituicao").toString());
        org.junit.jupiter.api.Assertions.assertEquals(12, ((Number) gravado.get("quantidade")).intValue());
        org.junit.jupiter.api.Assertions.assertEquals(escritorioId, ((Number) gravado.get("escritorio_id")).longValue());
        org.junit.jupiter.api.Assertions.assertEquals(0, jdbc.queryForObject(
                "SELECT COUNT(*) FROM outro_item WHERE codigo=?", Integer.class, prefixo + "_ESTOQUE_NEGATIVO"));
        mockMvc.perform(get("/api/outros-itens").param("busca", prefixo + "_ESTOQUE_1")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].codigo", equalTo(prefixo + "_ESTOQUE_1")))
                .andExpect(jsonPath("$[0].quantidade", equalTo(12)));
    }

    @Test
    void rejeitaCamposObrigatoriosReferenciasEInstituicaoInvalidaSemGravar() throws Exception {
        Map<String, String> faltando = linhaBem(prefixo + "_REQ", "", "");
        faltando.put("fabricante", "");
        Map<String, String> referencia = linhaBem(prefixo + "_REF", "", "");
        referencia.put("escritorio", "Escritório inexistente");
        Map<String, String> instituicao = linhaBem(prefixo + "_INST", "", "");
        instituicao.put("instituicao", "INVALIDA");
        mockMvc.perform(post("/api/importacao-excel/bens/confirmar").header("Authorization", token)
                        .contentType("application/json").content(request(List.of(faltando, referencia, instituicao))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.validos", equalTo(0)))
                .andExpect(jsonPath("$.importados", equalTo(0)))
                .andExpect(jsonPath("$.rejeitados", equalTo(3)))
                .andExpect(jsonPath("$.linhas[0].erros[0]").isNotEmpty());
        org.junit.jupiter.api.Assertions.assertEquals(0, contarPrefixo(prefixo + "_REQ%"));
        org.junit.jupiter.api.Assertions.assertEquals(0, contarPrefixo(prefixo + "_REF%"));
        org.junit.jupiter.api.Assertions.assertEquals(0, contarPrefixo(prefixo + "_INST%"));
    }

    @Test
    void detectaDuplicidadeDeCodigoEPatrimonioNaPlanilhaENoBancoMasPermiteSerieRepetida() throws Exception {
        Map<String, String> primeira = linhaBem(prefixo + "_DUP", "PAT-DUP", "SERIE-COMUM");
        Map<String, String> codigoDuplicado = linhaBem(prefixo + "_DUP", "PAT-OUTRO", "SERIE-COMUM");
        Map<String, String> patrimonioDuplicado = linhaBem(prefixo + "_OUTRO", "PAT-DUP", "SERIE-COMUM");
        mockMvc.perform(post("/api/importacao-excel/bens/validar").header("Authorization", token)
                        .contentType("application/json").content(request(List.of(primeira, codigoDuplicado, patrimonioDuplicado))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.validos", equalTo(1)))
                .andExpect(jsonPath("$.rejeitados", equalTo(2)));

        mockMvc.perform(post("/api/importacao-excel/bens/confirmar").header("Authorization", token)
                        .contentType("application/json").content(request(List.of(primeira))))
                .andExpect(status().isOk()).andExpect(jsonPath("$.importados", equalTo(1)));
        Map<String, String> patrimonioNoBanco = linhaBem(prefixo + "_BANCO", "PAT-DUP", "SERIE-COMUM");
        mockMvc.perform(post("/api/importacao-excel/bens/confirmar").header("Authorization", token)
                        .contentType("application/json").content(request(List.of(patrimonioNoBanco))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.importados", equalTo(0)))
                .andExpect(jsonPath("$.rejeitados", equalTo(1)));
        org.junit.jupiter.api.Assertions.assertEquals(1, contarCodigo(prefixo + "_DUP"));
        org.junit.jupiter.api.Assertions.assertEquals(0, contarCodigo(prefixo + "_BANCO"));
    }

    @Test
    void rejeitaTipoDeImportacaoInvalido() throws Exception {
        mockMvc.perform(post("/api/importacao-excel/tipo-invalido/validar")
                        .header("Authorization", token).contentType("application/json")
                        .content(request(List.of())))
                .andExpect(status().isBadRequest());
    }

    private Map<String, String> linhaBem(String codigo, String patrimonio, String serial) {
        Map<String, String> dados = new LinkedHashMap<>();
        dados.put("codigo", codigo);
        dados.put("patrimonio", patrimonio);
        dados.put("instituicao", "CAR");
        dados.put("nome", "Bem de teste " + codigo);
        dados.put("categoria", "Notebook");
        dados.put("serial", serial);
        dados.put("fabricante", "Fabricante Teste");
        dados.put("modelo", "Modelo Teste");
        dados.put("descricao", "Descrição da automação");
        dados.put("status", "Ativo");
        dados.put("dataAquisicao", "2024-02-29");
        dados.put("escritorio", prefixo + " Escritório");
        dados.put("departamento", prefixo + " Departamento");
        dados.put("responsavel", "");
        return dados;
    }

    private String request(List<Map<String, String>> dados) throws Exception {
        List<Map<String, Object>> linhas = new java.util.ArrayList<>();
        for (int i = 0; i < dados.size(); i++) {
            linhas.add(Map.of("numeroLinha", i + 2, "dados", dados.get(i)));
        }
        return objectMapper.writeValueAsString(Map.of("linhas", linhas));
    }

    private int contarCodigo(String codigo) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM item WHERE codigo=?", Integer.class, codigo);
    }

    private int contarPrefixo(String padrao) {
        return jdbc.queryForObject("SELECT COUNT(*) FROM item WHERE codigo LIKE ?", Integer.class, padrao);
    }
}
