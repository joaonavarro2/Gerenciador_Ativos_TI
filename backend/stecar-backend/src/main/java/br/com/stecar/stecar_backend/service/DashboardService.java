package br.com.stecar.stecar_backend.service;

import br.com.stecar.stecar_backend.dto.DashboardAtividadeMensalDTO;
import br.com.stecar.stecar_backend.dto.DashboardBemDTO;
import br.com.stecar.stecar_backend.dto.DashboardCategoriaDTO;
import br.com.stecar.stecar_backend.dto.DashboardResponseDTO;
import br.com.stecar.stecar_backend.dto.DashboardResumoDTO;
import br.com.stecar.stecar_backend.repository.DashboardRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final DashboardRepository dashboardRepository;

    public DashboardService(DashboardRepository dashboardRepository) {
        this.dashboardRepository = dashboardRepository;
    }

    public DashboardResponseDTO buscarDashboard() {

        /*
         * ============================
         * RESUMO
         * ============================
         */

        long totalBens = dashboardRepository.contarTotalBens();

        long movimentacoesPendentes =
                dashboardRepository.contarMovimentacoesPorStatus("PENDENTE");

        long consertosAbertos =
                dashboardRepository.contarConsertosPorStatus("ABERTO");

        long itensEmAnalise =
                dashboardRepository.contarItensPorStatus("EM_ANALISE");

        DashboardResumoDTO resumo = new DashboardResumoDTO(
                totalBens,
                movimentacoesPendentes,
                consertosAbertos,
                itensEmAnalise
        );


        /*
         * ============================
         * ATIVIDADE MENSAL
         * ============================
         */

        int anoAtual = LocalDate.now().getYear();

        List<Object[]> movimentacoesPorMes =
                dashboardRepository.contarMovimentacoesPorMes(anoAtual);

        List<Object[]> consertosPorMes =
                dashboardRepository.contarConsertosPorMes(anoAtual);

        Map<Integer, Long> movimentacoesMap =
                movimentacoesPorMes.stream()
                        .collect(Collectors.toMap(
                                linha -> ((Number) linha[0]).intValue(),
                                linha -> ((Number) linha[1]).longValue()
                        ));

        Map<Integer, Long> consertosMap =
                consertosPorMes.stream()
                        .collect(Collectors.toMap(
                                linha -> ((Number) linha[0]).intValue(),
                                linha -> ((Number) linha[1]).longValue()
                        ));

        List<DashboardAtividadeMensalDTO> atividadeMensal =
                new ArrayList<>();

        for (int mes = 1; mes <= 12; mes++) {

            String nomeMes = Month.of(mes)
                    .getDisplayName(
                            java.time.format.TextStyle.SHORT,
                                    java.util.Locale.forLanguageTag("pt-BR")
                    );

            atividadeMensal.add(
                    new DashboardAtividadeMensalDTO(
                            nomeMes.substring(0, 1).toUpperCase()
                                    + nomeMes.substring(1),
                            movimentacoesMap.getOrDefault(mes, 0L),
                            consertosMap.getOrDefault(mes, 0L)
                    )
            );
        }


        /*
         * ============================
         * DISTRIBUIÇÃO POR CATEGORIA
         * ============================
         */

        List<Object[]> categorias =
                dashboardRepository.contarItensPorCategoria();

        long totalCategorias = categorias.stream()
                .mapToLong(linha -> ((Number) linha[1]).longValue())
                .sum();

        List<DashboardCategoriaDTO> distribuicaoCategoria =
                categorias.stream()
                        .map(linha -> {

                            String nome = (String) linha[0];

                            long quantidade =
                                    ((Number) linha[1]).longValue();

                            double percentual = totalCategorias == 0
                                    ? 0
                                    : (quantidade * 100.0)
                                    / totalCategorias;

                            return new DashboardCategoriaDTO(
                                    nome,
                                    Math.round(percentual * 100.0) / 100.0
                            );
                        })
                        .toList();


        /*
         * ============================
         * BENS DO DASHBOARD
         * ============================
         */

        List<Object[]> bens =
                dashboardRepository.buscarBensDashboard();

        List<DashboardBemDTO> bensDTO =
                bens.stream()
                        .map(linha -> {

                            Long id =
                                    ((Number) linha[0]).longValue();

                            String descricao =
                                    (String) linha[1];

                            String unidade =
                                    (String) linha[2];

                            String departamento =
                                    (String) linha[3];

                            String data =
                                    linha[4] != null
                                            ? linha[4].toString()
                                            : null;

                            String status =
                                    (String) linha[5];

                            return new DashboardBemDTO(
                                    id,
                                    descricao,
                                    unidade,
                                    departamento,
                                    data,
                                    status
                            );
                        })
                        .toList();


        /*
         * ============================
         * RESPOSTA FINAL
         * ============================
         */

        return new DashboardResponseDTO(
                anoAtual,
                resumo,
                atividadeMensal,
                distribuicaoCategoria,
                bensDTO
        );
    }
}