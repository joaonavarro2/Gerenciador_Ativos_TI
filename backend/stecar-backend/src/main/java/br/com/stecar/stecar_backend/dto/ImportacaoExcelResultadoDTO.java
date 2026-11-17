package br.com.stecar.stecar_backend.dto;

import java.util.List;

public record ImportacaoExcelResultadoDTO(
        int totalRegistros,
        int validos,
        int importados,
        int rejeitados,
        List<ImportacaoExcelLinhaResultadoDTO> linhas) {
}
