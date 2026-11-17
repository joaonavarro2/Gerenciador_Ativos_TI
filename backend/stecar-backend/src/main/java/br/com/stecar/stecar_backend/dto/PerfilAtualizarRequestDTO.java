package br.com.stecar.stecar_backend.dto;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PerfilAtualizarRequestDTO(
        @NotBlank @Size(max = 150) String nomeCompleto,
        @NotBlank @Email @Size(max = 150) String email) {

    @JsonAnySetter
    public void rejeitarCampoNaoPermitido(String campo, Object valor) {
        throw new IllegalArgumentException("A atualização do perfil contém um campo não permitido.");
    }
}
