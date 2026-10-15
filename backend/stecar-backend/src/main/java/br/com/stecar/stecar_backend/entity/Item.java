package br.com.stecar.stecar_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "item")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "patrimonio")
    private String patrimonio;

    @Column(name = "codigo", unique = true, length = 50)
    private String codigo;

    @Column(name = "nome", nullable = false)
    private String nome;

    @Column(name = "categoria", nullable = false)
    private String categoria;

    @Column(name = "serial")
    private String serial;

    @Column(name = "fabricante", nullable = false)
    private String fabricante;

    @Column(name = "modelo", nullable = false)
    private String modelo;

    @Column(name = "descricao")
    private String descricao;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "data_aquisicao", nullable = false)
    private LocalDate dataAquisicao;

    @Column(name = "escritorio_id", nullable = false)
    private Long escritorioId;

    @Column(name = "departamento_id", nullable = false)
    private Long departamentoId;

    @Column(name = "pessoa_id")
    private Long pessoaId;

    @Column(name = "excluido", nullable = false)
    private boolean excluido;

    public Item() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPatrimonio() {
        return patrimonio;
    }

    public void setPatrimonio(String patrimonio) {
        this.patrimonio = patrimonio;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getFabricante() {
        return fabricante;
    }

    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDate getDataAquisicao() {
        return dataAquisicao;
    }

    public void setDataAquisicao(LocalDate dataAquisicao) {
        this.dataAquisicao = dataAquisicao;
    }

    public Long getEscritorioId() {
        return escritorioId;
    }

    public void setEscritorioId(Long escritorioId) {
        this.escritorioId = escritorioId;
    }

    public Long getDepartamentoId() {
        return departamentoId;
    }

    public void setDepartamentoId(Long departamentoId) {
        this.departamentoId = departamentoId;
    }

    public Long getPessoaId() {
        return pessoaId;
    }

    public void setPessoaId(Long pessoaId) {
        this.pessoaId = pessoaId;
    }

    public boolean isExcluido() {
        return excluido;
    }

    public void setExcluido(boolean excluido) {
        this.excluido = excluido;
    }
}