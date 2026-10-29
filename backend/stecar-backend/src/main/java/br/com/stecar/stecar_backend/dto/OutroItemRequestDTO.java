package br.com.stecar.stecar_backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record OutroItemRequestDTO(
        @NotBlank @Size(max = 50) String codigo,
        @NotBlank @Size(max = 150) String nome,
        @NotBlank @Size(max = 100) String categoria,
        @NotNull @Positive Long escritorioId,
        @NotBlank @Size(max = 255) String localizacao,
        @NotNull @Min(0) Integer quantidade,
        @NotNull @Min(0) Integer estoqueMinimo,
        @Size(max = 150) String fornecedor,
        @NotBlank @Size(max = 20) String status) {
}
