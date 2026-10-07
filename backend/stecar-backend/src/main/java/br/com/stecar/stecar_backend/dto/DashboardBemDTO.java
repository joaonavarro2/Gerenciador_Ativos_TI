package br.com.stecar.stecar_backend.dto;

public class DashboardBemDTO {

    private Long id;
    private String descricao;
    private String unidade;
    private String departamento;
    private String data;
    private String status;

    public DashboardBemDTO() {
    }

    public DashboardBemDTO(
            Long id,
            String descricao,
            String unidade,
            String departamento,
            String data,
            String status) {

        this.id = id;
        this.descricao = descricao;
        this.unidade = unidade;
        this.departamento = departamento;
        this.data = data;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getUnidade() {
        return unidade;
    }

    public void setUnidade(String unidade) {
        this.unidade = unidade;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}