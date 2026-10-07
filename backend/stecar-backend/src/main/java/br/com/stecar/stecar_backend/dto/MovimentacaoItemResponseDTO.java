package br.com.stecar.stecar_backend.dto;

import java.time.LocalDateTime;

public record MovimentacaoItemResponseDTO(
        Long id,
        String tipo,
        LocalDateTime data,
        String origem,
        String destino,
        String usuario) {
}