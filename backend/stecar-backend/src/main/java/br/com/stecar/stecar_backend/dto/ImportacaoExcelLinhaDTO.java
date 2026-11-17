package br.com.stecar.stecar_backend.dto;

import java.util.Map;

public record ImportacaoExcelLinhaDTO(Integer numeroLinha, Map<String, String> dados) {
}
