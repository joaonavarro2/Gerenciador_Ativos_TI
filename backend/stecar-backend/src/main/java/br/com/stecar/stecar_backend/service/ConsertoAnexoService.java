package br.com.stecar.stecar_backend.service;

import br.com.stecar.stecar_backend.dto.ConsertoAnexoResponseDTO;
import br.com.stecar.stecar_backend.entity.ConsertoAnexo;
import br.com.stecar.stecar_backend.repository.ConsertoAnexoRepository;
import br.com.stecar.stecar_backend.repository.ConsertoRepository;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ConsertoAnexoService {
    private static final long TAMANHO_MAXIMO = 20L * 1024 * 1024;
    private static final Set<String> EXTENSOES = Set.of("pdf", "jpg", "jpeg", "png", "gif", "doc", "docx");
    private final ConsertoAnexoRepository anexoRepository;
    private final ConsertoRepository consertoRepository;

    public ConsertoAnexoService(ConsertoAnexoRepository anexoRepository, ConsertoRepository consertoRepository) {
        this.anexoRepository = anexoRepository;
        this.consertoRepository = consertoRepository;
    }

    @Transactional(readOnly = true)
    public List<ConsertoAnexoResponseDTO> listar(Long consertoId) {
        validarConserto(consertoId);
        return anexoRepository.findByConsertoIdOrderByDataUploadDesc(consertoId).stream()
                .map(this::dto).toList();
    }

    @Transactional
    public List<ConsertoAnexoResponseDTO> salvar(Long consertoId, List<MultipartFile> arquivos) {
        validarConserto(consertoId);
        if (arquivos == null || arquivos.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Selecione ao menos um arquivo.");
        }
        return arquivos.stream().map(arquivo -> salvarUm(consertoId, arquivo)).toList();
    }

    @Transactional(readOnly = true)
    public ConsertoAnexo buscar(Long consertoId, Long anexoId) {
        validarConserto(consertoId);
        return anexoRepository.findById(anexoId)
                .filter(a -> a.getConsertoId().equals(consertoId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Anexo não encontrado."));
    }

    @Transactional
    public void excluir(Long consertoId, Long anexoId) {
        anexoRepository.delete(buscar(consertoId, anexoId));
    }

    private ConsertoAnexoResponseDTO salvarUm(Long consertoId, MultipartFile arquivo) {
        String nome = arquivo.getOriginalFilename();
        if (arquivo.isEmpty() || nome == null || nome.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O arquivo está vazio ou sem nome.");
        }
        if (arquivo.getSize() > TAMANHO_MAXIMO) {
            throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Cada arquivo pode ter no máximo 20 MB.");
        }
        String nomeSeguro = nome.replace('\\', '/');
        nomeSeguro = nomeSeguro.substring(nomeSeguro.lastIndexOf('/') + 1).trim();
        if (nomeSeguro.length() > 255) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O nome do arquivo excede 255 caracteres.");
        }
        int ponto = nomeSeguro.lastIndexOf('.');
        String extensao = ponto < 0 ? "" : nomeSeguro.substring(ponto + 1).toLowerCase(Locale.ROOT);
        if (!EXTENSOES.contains(extensao)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato não permitido. Envie PDF, JPG, PNG, GIF, DOC ou DOCX.");
        }
        try {
            ConsertoAnexo anexo = new ConsertoAnexo();
            anexo.setConsertoId(consertoId);
            anexo.setNomeArquivo(nomeSeguro);
            anexo.setTipoConteudo(tipoConteudo(extensao));
            anexo.setTamanho(arquivo.getSize());
            anexo.setConteudo(arquivo.getBytes());
            anexo.setDataUpload(LocalDateTime.now());
            return dto(anexoRepository.save(anexo));
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Não foi possível ler o arquivo enviado.");
        }
    }

    private void validarConserto(Long id) {
        if (!consertoRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Reparo não encontrado.");
        }
    }

    private String tipoConteudo(String extensao) {
        return switch (extensao) {
            case "pdf" -> "application/pdf";
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "doc" -> "application/msword";
            case "docx" -> "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
            default -> "application/octet-stream";
        };
    }

    private ConsertoAnexoResponseDTO dto(ConsertoAnexo a) {
        return new ConsertoAnexoResponseDTO(a.getId(), a.getNomeArquivo(), a.getTipoConteudo(),
                a.getTamanho(), a.getDataUpload());
    }
}
