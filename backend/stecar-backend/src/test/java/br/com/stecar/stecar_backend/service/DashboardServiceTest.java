package br.com.stecar.stecar_backend.service;

import br.com.stecar.stecar_backend.dto.DashboardResponseDTO;
import br.com.stecar.stecar_backend.repository.DashboardRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DashboardServiceTest {

    @Test
    void deveMontarDadosDoDashboardComAgregacoesDoRepositorio() {
        DashboardRepository repository = mock(DashboardRepository.class);
        DashboardService service = new DashboardService(repository);
        int anoAtual = Year.now().getValue();

        when(repository.contarTotalBens()).thenReturn(4L);
        when(repository.contarMovimentacoesPorStatus("PENDENTE")).thenReturn(2L);
        when(repository.contarConsertosPorStatus("ABERTO")).thenReturn(1L);
        when(repository.contarItensPorStatus("EM_ANALISE")).thenReturn(1L);
        when(repository.contarMovimentacoesPorMes(anoAtual))
                .thenReturn(List.<Object[]>of(new Object[]{1, 3L}));
        when(repository.contarConsertosPorMes(anoAtual))
                .thenReturn(List.<Object[]>of(new Object[]{2, 1L}));
        List<Object[]> categorias = new java.util.ArrayList<>();
        categorias.add(new Object[]{"Computadores", 3L});
        categorias.add(new Object[]{"Rede", 1L});
        when(repository.contarItensPorCategoria()).thenReturn(categorias);
        List<Object[]> bens = new java.util.ArrayList<>();
        bens.add(new Object[]{11L, "Notebook", "Salvador", "TI", LocalDate.parse("2026-09-10"), "DISPONIVEL"});
        when(repository.buscarBensDashboard()).thenReturn(bens);

        DashboardResponseDTO resposta = service.buscarDashboard();

        assertEquals(anoAtual, resposta.getAno());
        assertEquals(4L, resposta.getResumo().getTotalAssets());
        assertEquals(2L, resposta.getResumo().getPendingMovements());
        assertEquals(1L, resposta.getResumo().getMaintenanceOpen());
        assertEquals(1L, resposta.getResumo().getItemsInAnalysis());
        assertEquals(12, resposta.getAtividadeMensal().size());
        assertEquals(3L, resposta.getAtividadeMensal().get(0).getMovimentacoes());
        assertEquals(1L, resposta.getAtividadeMensal().get(1).getConsertos());
        assertEquals(75.0, resposta.getDistribuicaoCategoria().get(0).getValue());
        assertEquals(25.0, resposta.getDistribuicaoCategoria().get(1).getValue());
        assertEquals(11L, resposta.getBens().get(0).getId());
        assertEquals("Notebook", resposta.getBens().get(0).getDescricao());
        verify(repository).contarMovimentacoesPorMes(anoAtual);
        verify(repository).contarConsertosPorMes(anoAtual);
    }
}