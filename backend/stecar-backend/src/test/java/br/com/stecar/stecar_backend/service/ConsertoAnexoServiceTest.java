package br.com.stecar.stecar_backend.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ConsertoAnexoServiceTest {

    @Test
    void aceitaArquivoComAssinaturaCompativel() {
        Assertions.assertTrue(ConsertoAnexoService.conteudoCompativel("pdf", "%PDF-1.7".getBytes()));
        Assertions.assertTrue(ConsertoAnexoService.conteudoCompativel("jpg",
                new byte[] {(byte) 0xFF, (byte) 0xD8, (byte) 0xFF, (byte) 0xE0}));
        Assertions.assertTrue(ConsertoAnexoService.conteudoCompativel("docx", new byte[] {0x50, 0x4B, 0x03, 0x04, 0x14}));
    }

    @Test
    void recusaArquivoComConteudoDiferenteDaExtensao() {
        Assertions.assertFalse(ConsertoAnexoService.conteudoCompativel("pdf", "<html><script>".getBytes()));
        Assertions.assertFalse(ConsertoAnexoService.conteudoCompativel("png", "%PDF-1.7".getBytes()));
        Assertions.assertFalse(ConsertoAnexoService.conteudoCompativel("gif", new byte[0]));
    }
}
