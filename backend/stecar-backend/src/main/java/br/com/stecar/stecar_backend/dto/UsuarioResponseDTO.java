package br.com.stecar.stecar_backend.dto;

public class UsuarioResponseDTO {

    private Long id;
    private String nomeCompleto;
    private String email;
    private String funcao;
    private String status;

    public UsuarioResponseDTO() {
    }

    public UsuarioResponseDTO(
            Long id,
            String nomeCompleto,
            String email,
            String funcao,
            String status) {

        this.id = id;
        this.nomeCompleto = nomeCompleto;
        this.email = email;
        this.funcao = funcao;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}