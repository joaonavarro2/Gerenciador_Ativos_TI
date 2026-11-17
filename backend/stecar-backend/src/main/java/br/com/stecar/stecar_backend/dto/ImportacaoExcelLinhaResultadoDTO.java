package br.com.stecar.stecar_backend.dto;

import java.util.List;
import java.util.Map;

public record ImportacaoExcelLinhaResultadoDTO(
        Integer numeroLinha,
        Long id,
        Map<String, String> dados,
        List<String> erros) {
}
