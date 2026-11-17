package br.com.stecar.stecar_backend.dto;

import br.com.stecar.stecar_backend.entity.InstituicaoPatrimonio;

public record OutroItemResponseDTO(
        Long id,
        String codigo,
        String patrimonio,
        InstituicaoPatrimonio instituicao,
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
