package br.com.stecar.stecar_backend.dto;

public record PessoaResponseDTO(
        Long id,
        String nomeCompleto,
        String matricula,
        String cpf,
        String email,
        String telefone,
        String cargo,
        Long escritorioId,
        String escritorio,
        Long departamentoId,
        String departamento,
        String status,
        long bens) {
}
