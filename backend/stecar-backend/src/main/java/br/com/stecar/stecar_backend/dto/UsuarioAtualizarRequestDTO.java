package br.com.stecar.stecar_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UsuarioAtualizarRequestDTO(
        @NotBlank @Size(max = 150) String nomeCompleto,
        @NotBlank @Email @Size(max = 150) String email,
        @NotBlank @Pattern(regexp = "ADMINISTRADOR GERAL|ADMINISTRADOR") String funcao) {
}
