package br.com.stecar.stecar_backend.dto;

import java.util.List;

public class DashboardResponseDTO {

    private int ano;
    private DashboardResumoDTO resumo;

    private List<DashboardAtividadeMensalDTO> atividadeMensal;

    private List<DashboardCategoriaDTO> distribuicaoCategoria;

    private List<DashboardBemDTO> bens;

    public DashboardResponseDTO() {
    }

    public DashboardResponseDTO(
            int ano,
            DashboardResumoDTO resumo,
            List<DashboardAtividadeMensalDTO> atividadeMensal,
            List<DashboardCategoriaDTO> distribuicaoCategoria,
            List<DashboardBemDTO> bens) {

        this.ano = ano;
        this.resumo = resumo;
        this.atividadeMensal = atividadeMensal;
        this.distribuicaoCategoria = distribuicaoCategoria;
        this.bens = bens;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }

    public DashboardResumoDTO getResumo() {
        return resumo;
    }

    public void setResumo(DashboardResumoDTO resumo) {
        this.resumo = resumo;
    }

    public List<DashboardAtividadeMensalDTO> getAtividadeMensal() {
        return atividadeMensal;
    }

    public void setAtividadeMensal(
            List<DashboardAtividadeMensalDTO> atividadeMensal) {

        this.atividadeMensal = atividadeMensal;
    }

    public List<DashboardCategoriaDTO> getDistribuicaoCategoria() {
        return distribuicaoCategoria;
    }

    public void setDistribuicaoCategoria(
            List<DashboardCategoriaDTO> distribuicaoCategoria) {

        this.distribuicaoCategoria = distribuicaoCategoria;
    }

    public List<DashboardBemDTO> getBens() {
        return bens;
    }

    public void setBens(List<DashboardBemDTO> bens) {
        this.bens = bens;
    }
}