package br.com.stecar.stecar_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record MovimentacaoItemRequestDTO(
        @NotBlank String tipo,
        @NotNull LocalDateTime data,
        @NotNull Long escritorioDestinoId,
        @NotNull Long departamentoDestinoId,
        Long responsavelDestinoId,
        @Size(max = 2000) String justificativa) {
}