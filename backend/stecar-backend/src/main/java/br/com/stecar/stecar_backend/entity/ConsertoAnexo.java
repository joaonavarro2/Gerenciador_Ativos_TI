package br.com.stecar.stecar_backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "conserto_anexo")
public class ConsertoAnexo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "conserto_id", nullable = false)
    private Long consertoId;
    @Column(name = "nome_arquivo", nullable = false, length = 255)
    private String nomeArquivo;
    @Column(name = "tipo_conteudo", nullable = false, length = 150)
    private String tipoConteudo;
    @Column(name = "tamanho", nullable = false)
    private Long tamanho;
    @Column(name = "conteudo", nullable = false, columnDefinition = "bytea")
    private byte[] conteudo;
    @Column(name = "data_upload", nullable = false)
    private LocalDateTime dataUpload;

    public Long getId() { return id; }
    public Long getConsertoId() { return consertoId; }
    public void setConsertoId(Long consertoId) { this.consertoId = consertoId; }
    public String getNomeArquivo() { return nomeArquivo; }
    public void setNomeArquivo(String nomeArquivo) { this.nomeArquivo = nomeArquivo; }
    public String getTipoConteudo() { return tipoConteudo; }
    public void setTipoConteudo(String tipoConteudo) { this.tipoConteudo = tipoConteudo; }
    public Long getTamanho() { return tamanho; }
    public void setTamanho(Long tamanho) { this.tamanho = tamanho; }
    public byte[] getConteudo() { return conteudo; }
    public void setConteudo(byte[] conteudo) { this.conteudo = conteudo; }
    public LocalDateTime getDataUpload() { return dataUpload; }
    public void setDataUpload(LocalDateTime dataUpload) { this.dataUpload = dataUpload; }
}
