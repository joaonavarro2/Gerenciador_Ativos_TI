package br.com.stecar.stecar_backend.dto;

import java.time.LocalDateTime;

public record ConsertoItemResponseDTO(
        Long id,
        String tipo,
        LocalDateTime data,
        String usuario,
        String descricao) {
}