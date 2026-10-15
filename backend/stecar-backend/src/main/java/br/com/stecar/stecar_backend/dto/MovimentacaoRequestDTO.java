
package br.com.stecar.stecar_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public class MovimentacaoRequestDTO {

    @NotBlank(message = "O tipo da movimentação é obrigatório.")
    @Size(max = 50, message = "O tipo deve ter no máximo 50 caracteres.")
    private String tipo;

    private LocalDateTime data;

    @NotBlank(message = "O status da movimentação é obrigatório.")
    @Size(max = 30, message = "O status deve ter no máximo 30 caracteres.")
    private String status;

    private Long departamentoOrigemId;

    private Long departamentoDestinoId;

    private Long escritorioOrigemId;

    private Long escritorioDestinoId;

    private Long pessoaOrigemId;

    private Long pessoaDestinoId;

    @NotNull(message = "O bem é obrigatório.")
    @Positive(message = "O ID do bem deve ser positivo.")
    private Long itemId;

    @Size(max = 5000, message = "A justificativa deve ter no máximo 5000 caracteres.")
    private String justificativa;

    public MovimentacaoRequestDTO() {
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

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }
}
