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
import br.com.stecar.stecar_backend.entity.Usuario;
import br.com.stecar.stecar_backend.repository.UsuarioRepository;
import br.com.stecar.stecar_backend.service.ItemService;
import br.com.stecar.stecar_backend.service.MovimentacaoService;

@SpringBootTest
@Transactional
class MovimentacaoIntegrationTests {

    @Autowired private ItemService itemService;
    @Autowired private MovimentacaoService movimentacaoService;
    @Autowired private UsuarioRepository usuarioRepository;

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

        dto.setStatus("Cancelada");
        dto.setJustificativa("Status corrigido");
        var atualizada = movimentacaoService.atualizar(registrada.getId(), dto);
        Assertions.assertEquals("Cancelada", atualizada.getStatus());

        dto.setEscritorioDestinoId(origem.id());
        var erro = Assertions.assertThrows(ResponseStatusException.class,
                () -> movimentacaoService.atualizar(registrada.getId(), dto));
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, erro.getStatusCode());
    }
}
