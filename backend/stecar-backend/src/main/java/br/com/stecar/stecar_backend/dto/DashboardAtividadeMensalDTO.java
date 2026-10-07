package br.com.stecar.stecar_backend.dto;

public class DashboardAtividadeMensalDTO {

    private String month;
    private long movimentacoes;
    private long consertos;

    public DashboardAtividadeMensalDTO() {
    }

    public DashboardAtividadeMensalDTO(
            String month,
            long movimentacoes,
            long consertos) {

        this.month = month;
        this.movimentacoes = movimentacoes;
        this.consertos = consertos;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public long getMovimentacoes() {
        return movimentacoes;
    }

    public void setMovimentacoes(long movimentacoes) {
        this.movimentacoes = movimentacoes;
    }

    public long getConsertos() {
        return consertos;
    }

    public void setConsertos(long consertos) {
        this.consertos = consertos;
    }
}