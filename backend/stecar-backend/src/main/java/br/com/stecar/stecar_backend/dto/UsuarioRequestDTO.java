package br.com.stecar.stecar_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public class UsuarioRequestDTO {

    @NotBlank(message = "O nome completo é obrigatório.")
    @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
    private String nomeCompleto;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Informe um e-mail válido.")
    @Size(max = 150, message = "O e-mail deve ter no máximo 150 caracteres.")
    private String email;

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 8, max = 100, message = "A senha deve ter entre 8 e 100 caracteres.")
    private String senha;

    @Pattern(
    regexp = "ADMINISTRADOR GERAL|ADMINISTRADOR",
    message = "A função deve ser ADMINISTRADOR GERAL ou ADMINISTRADOR."
    )
    @NotBlank(message = "A função é obrigatória.")
    private String funcao;

    public UsuarioRequestDTO() {
    }

    public String getNomeCompleto() {
        return nomeCompleto;
    }

    public void setNomeCompleto(String nomeCompleto) {
        this.nomeCompleto = nomeCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }
}
