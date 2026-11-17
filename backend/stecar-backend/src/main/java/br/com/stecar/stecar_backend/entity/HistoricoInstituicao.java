package br.com.stecar.stecar_backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "historico_instituicao")
public class HistoricoInstituicao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_entidade", nullable = false, length = 20)
    private String tipoEntidade;

    @Column(name = "entidade_id", nullable = false)
    private Long entidadeId;

    @Column(name = "instituicao_anterior", length = 4)
    private String instituicaoAnterior;

    @Column(name = "instituicao_nova", nullable = false, length = 4)
    private String instituicaoNova;

    @Column(name = "alterado_em", nullable = false)
    private LocalDateTime alteradoEm;

    @Column(name = "usuario_id")
    private Long usuarioId;

    @Column(name = "usuario_nome", length = 150)
    private String usuarioNome;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTipoEntidade() { return tipoEntidade; }
    public void setTipoEntidade(String tipoEntidade) { this.tipoEntidade = tipoEntidade; }
    public Long getEntidadeId() { return entidadeId; }
    public void setEntidadeId(Long entidadeId) { this.entidadeId = entidadeId; }
    public String getInstituicaoAnterior() { return instituicaoAnterior; }
    public void setInstituicaoAnterior(String instituicaoAnterior) { this.instituicaoAnterior = instituicaoAnterior; }
    public String getInstituicaoNova() { return instituicaoNova; }
    public void setInstituicaoNova(String instituicaoNova) { this.instituicaoNova = instituicaoNova; }
    public LocalDateTime getAlteradoEm() { return alteradoEm; }
    public void setAlteradoEm(LocalDateTime alteradoEm) { this.alteradoEm = alteradoEm; }
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public String getUsuarioNome() { return usuarioNome; }
    public void setUsuarioNome(String usuarioNome) { this.usuarioNome = usuarioNome; }
}
