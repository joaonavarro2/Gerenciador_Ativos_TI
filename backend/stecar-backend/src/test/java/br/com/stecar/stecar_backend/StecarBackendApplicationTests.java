package br.com.stecar.stecar_backend;

import br.com.stecar.stecar_backend.dto.DashboardResponseDTO;
import br.com.stecar.stecar_backend.dto.ItemRequestDTO;
import br.com.stecar.stecar_backend.dto.MovimentacaoItemRequestDTO;
import br.com.stecar.stecar_backend.repository.UsuarioRepository;
import br.com.stecar.stecar_backend.service.DashboardService;
import br.com.stecar.stecar_backend.service.ItemService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@SpringBootTest
class StecarBackendApplicationTests {

	@Autowired
	private DashboardService dashboardService;

	@Autowired
	private ItemService itemService;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Test
	void contextLoads() {
	}

	@Test
	void dashboardConsultaBancoEDevolveDadosNoFormatoEsperado() {
		DashboardResponseDTO resposta = dashboardService.buscarDashboard();

		Assertions.assertNotNull(resposta.getResumo());
		Assertions.assertEquals(12, resposta.getAtividadeMensal().size());
		Assertions.assertTrue(resposta.getBens().size() <= 5);
		Assertions.assertEquals(java.time.Year.now().getValue(), resposta.getAno());
	}

	@Test
	void inventarioConsultaItensOpcoesEHistoricosSemAlterarDados() {
		var bens = itemService.listar(null, null, null, null);
		var opcoes = itemService.buscarOpcoes();

		Assertions.assertNotNull(bens);
		Assertions.assertNotNull(opcoes.escritorios());
		Assertions.assertNotNull(opcoes.departamentos());
		Assertions.assertNotNull(opcoes.pessoas());
		Assertions.assertNotNull(opcoes.categorias());
		Assertions.assertNotNull(opcoes.status());

		if (!bens.isEmpty()) {
			Long id = bens.get(0).id();
			Assertions.assertNotNull(itemService.buscarPorId(id));
			Assertions.assertNotNull(itemService.listarMovimentacoes(id));
			Assertions.assertNotNull(itemService.listarConsertos(id));
		}
	}

	@Test
	@Transactional
	void inventarioCriaAtualizaMovimentaEExcluiDentroDeTransacaoRevertida() {
		var opcoes = itemService.buscarOpcoes();
		Assertions.assertFalse(opcoes.escritorios().isEmpty());
		Assertions.assertFalse(opcoes.departamentos().isEmpty());

		var escritorio = opcoes.escritorios().get(0);
		var departamento = opcoes.departamentos().get(0);
		var dados = new ItemRequestDTO(
				"TESTE-" + System.nanoTime(), "TESTE-PAT", "Bem de teste", "Teste",
				"TESTE-SERIAL", "Fabricante", "Modelo", null, "Ativo", LocalDate.now(),
				escritorio.id(), departamento.id(), null);

		var criado = itemService.criar(dados);
		Assertions.assertNotNull(criado.id());
		Assertions.assertEquals("Bem de teste", criado.nome());
		var erroDuplicidade = Assertions.assertThrows(ResponseStatusException.class,
				() -> itemService.criar(dados));
		Assertions.assertEquals(HttpStatus.CONFLICT, erroDuplicidade.getStatusCode());

		var atualizado = itemService.atualizar(criado.id(), new ItemRequestDTO(
				dados.codigo(), dados.patrimonio(), "Bem atualizado", dados.categoria(), dados.serial(),
				dados.fabricante(), dados.modelo(), dados.descricao(), dados.status(), dados.dataAquisicao(),
				dados.escritorioId(), dados.departamentoId(), dados.pessoaId()));
		Assertions.assertEquals("Bem atualizado", atualizado.nome());

		usuarioRepository.findAll().stream()
				.filter(usuario -> "ATIVO".equalsIgnoreCase(usuario.getStatus()))
				.findFirst()
				.ifPresent(usuario -> {
					var movimentado = itemService.movimentar(criado.id(), new MovimentacaoItemRequestDTO(
							"Transferência", LocalDateTime.now(), escritorio.id(), departamento.id(), null, "Teste transacional"),
							usuario.getEmail());
					Assertions.assertEquals(criado.id(), movimentado.id());
					Assertions.assertEquals(1, itemService.listarMovimentacoes(criado.id()).size());
					itemService.excluir(criado.id());
					Assertions.assertEquals(1, itemService.listarMovimentacoes(criado.id()).size());
				});
		Assertions.assertTrue(itemService.listar(null, "Bem atualizado", null, null).stream()
				.noneMatch(item -> item.id().equals(criado.id())));

		var paraExcluir = itemService.criar(new ItemRequestDTO(
				"TESTE-EXCLUIR-" + System.nanoTime(), "TESTE-PAT-2", "Bem para excluir", "Teste",
				"TESTE-SERIAL-2", "Fabricante", "Modelo", null, "Ativo", LocalDate.now(),
				escritorio.id(), departamento.id(), null));
		itemService.excluir(paraExcluir.id());
		Assertions.assertTrue(itemService.listar(null, "TESTE", null, null).stream()
				.noneMatch(item -> item.id().equals(paraExcluir.id())));
	}

}
