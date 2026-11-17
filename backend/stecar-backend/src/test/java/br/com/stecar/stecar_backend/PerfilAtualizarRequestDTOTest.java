package br.com.stecar.stecar_backend;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.Validation;

import org.junit.jupiter.api.Test;

import br.com.stecar.stecar_backend.dto.PerfilAtualizarRequestDTO;

class PerfilAtualizarRequestDTOTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void aceitaSomenteNomeEEmail() throws Exception {
        PerfilAtualizarRequestDTO dados = objectMapper.readValue(
                """
                        {"nomeCompleto":"Nome válido","email":"nome@example.test"}
                        """,
                PerfilAtualizarRequestDTO.class);

        assertEquals("Nome válido", dados.nomeCompleto());
        assertEquals("nome@example.test", dados.email());
    }

    @Test
    void rejeitaTentativasDeEnviarFuncaoOuIdentificador() {
        assertThrows(JsonProcessingException.class, () -> objectMapper.readValue(
                """
                        {"nomeCompleto":"Nome","email":"nome@example.test","funcao":"ADMINISTRADOR GERAL"}
                        """,
                PerfilAtualizarRequestDTO.class));
        assertThrows(JsonProcessingException.class, () -> objectMapper.readValue(
                """
                        {"nomeCompleto":"Nome","email":"nome@example.test","id":999}
                        """,
                PerfilAtualizarRequestDTO.class));
    }

    @Test
    void validaNomeEEmailNoBackend() {
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            var validator = factory.getValidator();
            assertTrue(validator.validate(new PerfilAtualizarRequestDTO("", "email-invalido")).size() >= 2);
            assertTrue(validator.validate(
                    new PerfilAtualizarRequestDTO("N".repeat(151), "nome@example.test")).size() >= 1);
        }
    }
}
