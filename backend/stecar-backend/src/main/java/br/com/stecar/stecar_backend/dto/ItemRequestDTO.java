package br.com.stecar.stecar_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record ItemRequestDTO(
        @NotBlank @Size(max = 50) String codigo,
        @Size(max = 50) String patrimonio,
        @NotBlank @Size(max = 150) String nome,
        @NotBlank @Size(max = 100) String categoria,
        @Size(max = 150) String serial,
        @NotBlank @Size(max = 100) String fabricante,
        @NotBlank @Size(max = 100) String modelo,
        String descricao,
        @NotBlank @Size(max = 30) String status,
        @NotNull LocalDate dataAquisicao,
        @NotNull Long escritorioId,
        @NotNull Long departamentoId,
        Long pessoaId) {
}