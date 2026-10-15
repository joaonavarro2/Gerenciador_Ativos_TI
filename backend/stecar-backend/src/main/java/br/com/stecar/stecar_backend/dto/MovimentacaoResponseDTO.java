
package br.com.stecar.stecar_backend.dto;

import java.time.LocalDateTime;

public class MovimentacaoResponseDTO {

    private Long id;
    private String tipo;
    private LocalDateTime data;
    private String status;
    private String justificativa;

    private Long itemId;
    private String patrimonioBem;
    private String nomeBem;

    private Long departamentoOrigemId;
    private String departamentoOrigem;

    private Long departamentoDestinoId;
    private String departamentoDestino;

    private Long escritorioOrigemId;
    private String escritorioOrigem;

    private Long escritorioDestinoId;
    private String escritorioDestino;

    private Long pessoaOrigemId;
    private String pessoaOrigem;

    private Long pessoaDestinoId;
    private String pessoaDestino;

    private Long usuarioId;
    private String responsavel;

    public MovimentacaoResponseDTO() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public String getPatrimonioBem() {
        return patrimonioBem;
    }

    public void setPatrimonioBem(String patrimonioBem) {
        this.patrimonioBem = patrimonioBem;
    }

    public String getNomeBem() {
        return nomeBem;
    }

    public void setNomeBem(String nomeBem) {
        this.nomeBem = nomeBem;
    }

    public Long getDepartamentoOrigemId() {
        return departamentoOrigemId;
    }

    public void setDepartamentoOrigemId(Long departamentoOrigemId) {
        this.departamentoOrigemId = departamentoOrigemId;
    }

    public String getDepartamentoOrigem() {
        return departamentoOrigem;
    }

    public void setDepartamentoOrigem(String departamentoOrigem) {
        this.departamentoOrigem = departamentoOrigem;
    }

    public Long getDepartamentoDestinoId() {
        return departamentoDestinoId;
    }

    public void setDepartamentoDestinoId(Long departamentoDestinoId) {
        this.departamentoDestinoId = departamentoDestinoId;
    }

    public String getDepartamentoDestino() {
        return departamentoDestino;
    }

    public void setDepartamentoDestino(String departamentoDestino) {
        this.departamentoDestino = departamentoDestino;
    }

    public Long getEscritorioOrigemId() {
        return escritorioOrigemId;
    }

    public void setEscritorioOrigemId(Long escritorioOrigemId) {
        this.escritorioOrigemId = escritorioOrigemId;
    }

    public String getEscritorioOrigem() {
        return escritorioOrigem;
    }

    public void setEscritorioOrigem(String escritorioOrigem) {
        this.escritorioOrigem = escritorioOrigem;
    }

    public Long getEscritorioDestinoId() {
        return escritorioDestinoId;
    }

    public void setEscritorioDestinoId(Long escritorioDestinoId) {
        this.escritorioDestinoId = escritorioDestinoId;
    }

    public String getEscritorioDestino() {
        return escritorioDestino;
    }

    public void setEscritorioDestino(String escritorioDestino) {
        this.escritorioDestino = escritorioDestino;
    }

    public Long getPessoaOrigemId() {
        return pessoaOrigemId;
    }

    public void setPessoaOrigemId(Long pessoaOrigemId) {
        this.pessoaOrigemId = pessoaOrigemId;
    }

    public String getPessoaOrigem() {
        return pessoaOrigem;
    }

    public void setPessoaOrigem(String pessoaOrigem) {
        this.pessoaOrigem = pessoaOrigem;
    }

    public Long getPessoaDestinoId() {
        return pessoaDestinoId;
    }

    public void setPessoaDestinoId(Long pessoaDestinoId) {
        this.pessoaDestinoId = pessoaDestinoId;
    }

    public String getPessoaDestino() {
        return pessoaDestino;
    }

    public void setPessoaDestino(String pessoaDestino) {
        this.pessoaDestino = pessoaDestino;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getResponsavel() {
        return responsavel;
    }

    public void setResponsavel(String responsavel) {
        this.responsavel = responsavel;
    }
}
