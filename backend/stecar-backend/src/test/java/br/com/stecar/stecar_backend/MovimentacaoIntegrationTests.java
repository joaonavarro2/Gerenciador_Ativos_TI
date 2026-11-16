package br.com.stecar.stecar_backend;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import br.com.stecar.stecar_backend.dto.ItemRequestDTO;
import br.com.stecar.stecar_backend.dto.MovimentacaoRequestDTO;
import br.com.stecar.stecar_backend.entity.Departamento;
import br.com.stecar.stecar_backend.entity.Pessoa;
import br.com.stecar.stecar_backend.entity.Usuario;
import br.com.stecar.stecar_backend.repository.DepartamentoRepository;
import br.com.stecar.stecar_backend.repository.PessoaRepository;
import br.com.stecar.stecar_backend.repository.UsuarioRepository;
import br.com.stecar.stecar_backend.service.ItemService;
import br.com.stecar.stecar_backend.service.MovimentacaoService;

@SpringBootTest
@Transactional
class MovimentacaoIntegrationTests extends PostgresIntegrationTest {

    @Autowired private ItemService itemService;
    @Autowired private MovimentacaoService movimentacaoService;
    @Autowired private UsuarioRepository usuarioRepository;
    @Autowired private DepartamentoRepository departamentoRepository;
    @Autowired private PessoaRepository pessoaRepository;

    @Test
    void movimentacaoAtualizaLocalDoBemEHistoricoNaoPodeSerReescrito() {
        var opcoes = itemService.buscarOpcoes();
        Assertions.assertTrue(opcoes.escritorios().size() >= 2, "O catálogo inicial deve ter ao menos 2 escritórios.");
        var origem = opcoes.escritorios().get(0);
        var destino = opcoes.escritorios().get(opcoes.escritorios().size() - 1);
        Assertions.assertNotEquals(origem.id(), destino.id());

        var bem = itemService.criar(new ItemRequestDTO(
                "TESTE-MOV-" + System.nanoTime(), "PAT-MOV", "Bem movimentado", "Teste",
                "SERIAL-MOV", "Fabricante", "Modelo", null, "Ativo", LocalDate.now(),
                origem.id(), opcoes.departamentos().get(0).id(), null));

        Usuario usuario = new Usuario();
        usuario.setNomeCompleto("Responsável");
        usuario.setEmail("mov-" + System.nanoTime() + "@exemplo.com");
        usuario.setSenha("hash");
        usuario.setFuncao("ADMINISTRADOR");
        usuario.setStatus("ATIVO");
        usuarioRepository.save(usuario);

        MovimentacaoRequestDTO dto = new MovimentacaoRequestDTO();
        dto.setTipo("Transferência");
        dto.setData(LocalDateTime.now().withNano(0));
        dto.setStatus("Concluída");
        dto.setItemId(bem.id());
        dto.setEscritorioOrigemId(origem.id());
        dto.setEscritorioDestinoId(destino.id());
        dto.setJustificativa("Teste");

        var registrada = movimentacaoService.cadastrar(dto, usuario.getId());
        Assertions.assertEquals(destino.id(), itemService.buscarPorId(bem.id()).escritorioId());

        ResponseStatusException exclusao = Assertions.assertThrows(
                ResponseStatusException.class,
                () -> movimentacaoService.excluir(registrada.getId()));
        Assertions.assertEquals(HttpStatus.CONFLICT, exclusao.getStatusCode());
        Assertions.assertEquals(destino.id(), itemService.buscarPorId(bem.id()).escritorioId());
        Assertions.assertEquals(1, movimentacaoService.listarPorItem(bem.id()).size());

        MovimentacaoRequestDTO retroativa = new MovimentacaoRequestDTO();
        retroativa.setTipo("Transferência");
        retroativa.setData(dto.getData().minusDays(10));
        retroativa.setStatus("Concluída");
        retroativa.setItemId(bem.id());
        retroativa.setEscritorioOrigemId(destino.id());
        retroativa.setEscritorioDestinoId(origem.id());
        movimentacaoService.cadastrar(retroativa, usuario.getId());
        Assertions.assertEquals(destino.id(), itemService.buscarPorId(bem.id()).escritorioId(),
                "Lançamento retroativo não deve alterar a localização atual.");

        var atual = itemService.buscarPorId(bem.id());
        itemService.movimentar(bem.id(), new br.com.stecar.stecar_backend.dto.MovimentacaoItemRequestDTO(
                "Transferência", dto.getData().minusDays(5), origem.id(), atual.departamentoId(), null, null),
                usuario.getEmail());
        Assertions.assertEquals(destino.id(), itemService.buscarPorId(bem.id()).escritorioId(),
                "Lançamento retroativo pela tela do bem não deve alterar a localização atual.");

        dto.setStatus("Cancelada");
        dto.setJustificativa("Status corrigido");
        var atualizada = movimentacaoService.atualizar(registrada.getId(), dto);
        Assertions.assertEquals("Cancelada", atualizada.getStatus());

        dto.setEscritorioDestinoId(origem.id());
        var erro = Assertions.assertThrows(ResponseStatusException.class,
                () -> movimentacaoService.atualizar(registrada.getId(), dto));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, erro.getStatusCode());
    }

    @Test
    void impedeVinculoDeBemETransferenciaParaDepartamentoDeOutroEscritorio() {
        var opcoes = itemService.buscarOpcoes();
        var escritorioOrigem = opcoes.escritorios().get(0);
        var escritorioDestino = opcoes.escritorios().get(1);
        var departamentoVinculadoAoDestino = new Departamento();
        departamentoVinculadoAoDestino.setNome("Departamento de teste " + System.nanoTime());
        departamentoVinculadoAoDestino.setStatus("ATIVO");
        departamentoVinculadoAoDestino.setEscritorioId(escritorioDestino.id());
        departamentoVinculadoAoDestino = departamentoRepository.save(departamentoVinculadoAoDestino);

        Long departamentoId = departamentoVinculadoAoDestino.getId();
        var vinculoInvalido = Assertions.assertThrows(ResponseStatusException.class,
                () -> itemService.criar(new ItemRequestDTO(
                        "TESTE-LOCAL-" + System.nanoTime(), "PAT-LOCAL", "Bem com vínculo inválido", "Teste",
                        "SERIAL-LOCAL", "Fabricante", "Modelo", null, "Ativo", LocalDate.now(),
                        escritorioOrigem.id(), departamentoId, null)));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, vinculoInvalido.getStatusCode());

        var bem = itemService.criar(new ItemRequestDTO(
                "TESTE-LOCAL-OK-" + System.nanoTime(), "PAT-LOCAL-OK", "Bem de origem", "Teste",
                "SERIAL-LOCAL-OK", "Fabricante", "Modelo", null, "Ativo", LocalDate.now(),
                escritorioOrigem.id(), opcoes.departamentos().get(0).id(), null));
        Assertions.assertTrue(itemService.listarPorEscritorio(escritorioOrigem.id()).stream()
                .anyMatch(item -> item.id().equals(bem.id())));

        Usuario usuario = new Usuario();
        usuario.setNomeCompleto("Usuário de teste");
        usuario.setEmail("local-" + System.nanoTime() + "@exemplo.com");
        usuario.setSenha("hash");
        usuario.setFuncao("ADMINISTRADOR");
        usuario.setStatus("ATIVO");
        usuarioRepository.save(usuario);

        MovimentacaoRequestDTO movimentacao = new MovimentacaoRequestDTO();
        movimentacao.setTipo("Transferência");
        movimentacao.setData(LocalDateTime.now().withNano(0));
        movimentacao.setStatus("Concluída");
        movimentacao.setItemId(bem.id());
        movimentacao.setEscritorioOrigemId(escritorioOrigem.id());
        movimentacao.setEscritorioDestinoId(escritorioOrigem.id());
        movimentacao.setDepartamentoOrigemId(opcoes.departamentos().get(0).id());
        movimentacao.setDepartamentoDestinoId(departamentoId);

        var destinoInvalido = Assertions.assertThrows(ResponseStatusException.class,
                () -> movimentacaoService.cadastrar(movimentacao, usuario.getId()));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, destinoInvalido.getStatusCode());
        Assertions.assertTrue(movimentacaoService.listarPorItem(bem.id()).isEmpty());
    }

    @Test
    void impedeMovimentacaoParaPessoaForaDoDepartamentoDeDestino() {
        var opcoes = itemService.buscarOpcoes();
        var escritorioOrigem = opcoes.escritorios().get(0);
        var escritorioDestino = opcoes.escritorios().get(1);
        var departamentoOrigem = opcoes.departamentos().get(0);

        var departamentoDestino = new Departamento();
        departamentoDestino.setNome("Departamento destino " + System.nanoTime());
        departamentoDestino.setStatus("ATIVO");
        departamentoDestino.setEscritorioId(escritorioDestino.id());
        departamentoDestino = departamentoRepository.save(departamentoDestino);

        var pessoaDestino = new Pessoa();
        pessoaDestino.setNomeCompleto("Pessoa em outro departamento");
        pessoaDestino.setEmail("pessoa-" + System.nanoTime() + "@exemplo.com");
        pessoaDestino.setCargo("Analista");
        pessoaDestino.setEscritorioId(escritorioDestino.id());
        pessoaDestino.setDepartamentoId(departamentoOrigem.id());
        pessoaDestino.setStatus("ATIVO");
        pessoaDestino = pessoaRepository.save(pessoaDestino);

        var bem = itemService.criar(new ItemRequestDTO(
                "TESTE-PESSOA-" + System.nanoTime(), "PAT-PESSOA", "Bem com pessoa incompatível", "Teste",
                "SERIAL-PESSOA", "Fabricante", "Modelo", null, "Ativo", LocalDate.now(),
                escritorioOrigem.id(), departamentoOrigem.id(), null));

        Usuario usuario = new Usuario();
        usuario.setNomeCompleto("Usuário de teste");
        usuario.setEmail("pessoa-mov-" + System.nanoTime() + "@exemplo.com");
        usuario.setSenha("hash");
        usuario.setFuncao("ADMINISTRADOR");
        usuario.setStatus("ATIVO");
        Long usuarioId = usuarioRepository.save(usuario).getId();

        MovimentacaoRequestDTO movimentacao = new MovimentacaoRequestDTO();
        movimentacao.setTipo("Transferência");
        movimentacao.setData(LocalDateTime.now().withNano(0));
        movimentacao.setStatus("Concluída");
        movimentacao.setItemId(bem.id());
        movimentacao.setEscritorioOrigemId(escritorioOrigem.id());
        movimentacao.setEscritorioDestinoId(escritorioDestino.id());
        movimentacao.setDepartamentoOrigemId(departamentoOrigem.id());
        movimentacao.setDepartamentoDestinoId(departamentoDestino.getId());
        movimentacao.setPessoaDestinoId(pessoaDestino.getId());

        var erro = Assertions.assertThrows(ResponseStatusException.class,
                () -> movimentacaoService.cadastrar(movimentacao, usuarioId));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, erro.getStatusCode());
        Assertions.assertTrue(movimentacaoService.listarPorItem(bem.id()).isEmpty());
    }
}
