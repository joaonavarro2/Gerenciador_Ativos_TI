package br.com.stecar.stecar_backend.dto;

public record OutroItemResponseDTO(
        Long id,
        String codigo,
        String nome,
        String categoria,
        Long escritorioId,
        String escritorio,
        String localizacao,
        Integer quantidade,
        Integer estoqueMinimo,
        String fornecedor,
        String status) {
}
