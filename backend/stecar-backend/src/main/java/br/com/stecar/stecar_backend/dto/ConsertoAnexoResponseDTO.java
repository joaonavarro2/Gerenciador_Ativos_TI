package br.com.stecar.stecar_backend.dto;

import java.time.LocalDateTime;

public record ConsertoAnexoResponseDTO(Long id, String nomeArquivo, String tipoConteudo,
                                       Long tamanho, LocalDateTime dataUpload) {}
