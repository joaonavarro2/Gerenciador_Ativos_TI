package br.com.stecar.stecar_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record PessoaRequestDTO(
        @NotBlank @Size(max = 150) String nomeCompleto,
        @Size(max = 30) String matricula,
        @NotBlank @Size(max = 14) String cpf,
        @NotBlank @Email @Size(max = 150) String email,
        @Size(max = 20) String telefone,
        @NotBlank @Size(max = 100) String cargo,
        @NotNull @Positive Long escritorioId,
        @NotNull @Positive Long departamentoId,
        @NotBlank @Size(max = 20) String status,
        @Size(min = 8, max = 100) String senha,
        @Size(max = 100) String confirmarSenha) {
}
