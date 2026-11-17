package br.com.stecar.stecar_backend.dto;

import br.com.stecar.stecar_backend.entity.InstituicaoPatrimonio;
import java.time.LocalDate;

public record ItemResponseDTO(
        Long id,
        String codigo,
        String patrimonio,
        InstituicaoPatrimonio instituicao,
        String nome,
        String categoria,
        String serial,
        String fabricante,
        String modelo,
        String descricao,
        String status,
        LocalDate dataAquisicao,
        Long escritorioId,
        String escritorio,
        Long departamentoId,
        String departamento,
        Long pessoaId,
        String responsavel) {
}