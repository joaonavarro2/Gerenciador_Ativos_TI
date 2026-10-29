package br.com.stecar.stecar_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record DepartamentoRequestDTO(
        @NotBlank @Size(max = 100) String nome,
        @NotBlank @Size(max = 20) String status,
        @Positive Long escritorioId) {
}
