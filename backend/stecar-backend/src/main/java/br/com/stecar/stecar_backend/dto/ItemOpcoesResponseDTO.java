package br.com.stecar.stecar_backend.dto;

import java.util.List;

public record ItemOpcoesResponseDTO(
        List<ItemOpcaoDTO> escritorios,
        List<ItemOpcaoDTO> departamentos,
        List<ItemOpcaoDTO> pessoas,
        List<String> categorias,
        List<String> status) {
}