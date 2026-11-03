package br.com.stecar.stecar_backend.dto;

public record DepartamentoResponseDTO(
        Long id,
        String nome,
        String sigla,
        String status,
        Long escritorioId,
        String escritorio,
        long bens,
        long consertos) {
}
