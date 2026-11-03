package br.com.stecar.stecar_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EscritorioRequestDTO(
        @NotBlank @Size(max = 150) String nome,
        @NotBlank @Size(max = 100) String cidade,
        @Size(max = 2) String estado,
        @Size(max = 10) String cep,
        @Size(max = 255) String endereco,
        @Size(max = 20) String telefone,
        @Email @Size(max = 150) String email,
        @Size(max = 150) String gestor,
        @NotBlank @Size(max = 20) String status) {
}
