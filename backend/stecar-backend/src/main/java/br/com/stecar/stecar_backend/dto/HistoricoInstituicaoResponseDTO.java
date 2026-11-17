package br.com.stecar.stecar_backend.dto;

import java.time.LocalDateTime;

public record HistoricoInstituicaoResponseDTO(
        Long id,
        String instituicaoAnterior,
        String instituicaoNova,
        LocalDateTime alteradoEm,
        String usuario) {
}
