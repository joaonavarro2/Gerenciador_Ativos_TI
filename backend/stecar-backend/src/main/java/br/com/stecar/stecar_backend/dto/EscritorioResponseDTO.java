package br.com.stecar.stecar_backend.dto;

public record EscritorioResponseDTO(
        Long id,
        String nome,
        String cidade,
        String estado,
        String cep,
        String endereco,
        String telefone,
        String email,
        String gestor,
        String responsavel,
        String status) {
}
