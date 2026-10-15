
package br.com.stecar.stecar_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "movimentacao")
public class Movimentacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo", nullable = false, length = 50)
    private String tipo;

    @Column(name = "data", nullable = false)
    private LocalDateTime data;

    @Column(name = "status", nullable = false, length = 30)
    private String status;

    @Column(name = "departamento_origem_id")
    private Long departamentoOrigemId;

    @Column(name = "departamento_destino_id")
    private Long departamentoDestinoId;

    @Column(name = "escritorio_origem_id")
    private Long escritorioOrigemId;

    @Column(name = "escritorio_destino_id")
    private Long escritorioDestinoId;

    @Column(name = "pessoa_origem_id")
    private Long pessoaOrigemId;

    @Column(name = "pessoa_destino_id")
    private Long pessoaDestinoId;

    @Column(name = "item_id", nullable = false)
    private Long itemId;

    @Column(name = "usuario_id", nullable = false)
    private Long usuarioId;

    @Column(name = "justificativa", columnDefinition = "text")
    private String justificativa;

    public Movimentacao() {
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

    public Long getDepartamentoOrigemId() {
        return departamentoOrigemId;
    }

    public void setDepartamentoOrigemId(Long departamentoOrigemId) {
        this.departamentoOrigemId = departamentoOrigemId;
    }

    public Long getDepartamentoDestinoId() {
        return departamentoDestinoId;
    }

    public void setDepartamentoDestinoId(Long departamentoDestinoId) {
        this.departamentoDestinoId = departamentoDestinoId;
    }

    public Long getEscritorioOrigemId() {
        return escritorioOrigemId;
    }

    public void setEscritorioOrigemId(Long escritorioOrigemId) {
        this.escritorioOrigemId = escritorioOrigemId;
    }

    public Long getEscritorioDestinoId() {
        return escritorioDestinoId;
    }

    public void setEscritorioDestinoId(Long escritorioDestinoId) {
        this.escritorioDestinoId = escritorioDestinoId;
    }

    public Long getPessoaOrigemId() {
        return pessoaOrigemId;
    }

    public void setPessoaOrigemId(Long pessoaOrigemId) {
        this.pessoaOrigemId = pessoaOrigemId;
    }

    public Long getPessoaDestinoId() {
        return pessoaDestinoId;
    }

    public void setPessoaDestinoId(Long pessoaDestinoId) {
        this.pessoaDestinoId = pessoaDestinoId;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }
}