package br.com.stecar.stecar_backend.dto;

import java.time.LocalDateTime;

public record ConsertoResponseDTO(
        Long id,
        String tipo,
        String descricaoProblema,
        String descricaoSolucao,
        LocalDateTime dataEntrada,
        LocalDateTime dataSaida,
        String status,
        String tecnico,
        Long itemId,
        String codigoBem,
        String patrimonioBem,
        String nomeBem,
        Long escritorioId,
        String escritorio,
        Long departamentoId,
        String departamento,
        Long usuarioId,
        String usuario) {
}
