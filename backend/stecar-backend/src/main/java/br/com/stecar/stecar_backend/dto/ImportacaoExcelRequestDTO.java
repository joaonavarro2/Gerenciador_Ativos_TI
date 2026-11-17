package br.com.stecar.stecar_backend.dto;

import java.util.List;

public record ImportacaoExcelRequestDTO(List<ImportacaoExcelLinhaDTO> linhas) {
}
