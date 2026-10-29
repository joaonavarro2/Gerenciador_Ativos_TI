package br.com.stecar.stecar_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

public record ConsertoRequestDTO(
        @NotBlank @Size(max = 50) String tipo,
        @NotBlank @Size(max = 5000) String descricaoProblema,
        @Size(max = 10000) String descricaoSolucao,
        @NotNull LocalDateTime dataEntrada,
        LocalDateTime dataSaida,
        @NotBlank @Size(max = 30) String status,
        @NotBlank @Size(max = 150) String tecnico,
        @NotNull @Positive Long itemId) {
}
