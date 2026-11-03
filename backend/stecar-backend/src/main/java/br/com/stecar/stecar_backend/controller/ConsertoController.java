package br.com.stecar.stecar_backend.controller;

import br.com.stecar.stecar_backend.dto.ConsertoRequestDTO;
import br.com.stecar.stecar_backend.dto.ConsertoResponseDTO;
import br.com.stecar.stecar_backend.dto.ConsertoAnexoResponseDTO;
import br.com.stecar.stecar_backend.entity.ConsertoAnexo;
import br.com.stecar.stecar_backend.entity.Usuario;
import br.com.stecar.stecar_backend.repository.UsuarioRepository;
import br.com.stecar.stecar_backend.service.ConsertoService;
import br.com.stecar.stecar_backend.service.ConsertoAnexoService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.nio.charset.StandardCharsets;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ContentDisposition;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/consertos")
public class ConsertoController {
    private final ConsertoService consertoService;
    private final UsuarioRepository usuarioRepository;
    private final ConsertoAnexoService anexoService;

    public ConsertoController(ConsertoService consertoService, UsuarioRepository usuarioRepository,
                              ConsertoAnexoService anexoService) {
        this.consertoService = consertoService;
        this.usuarioRepository = usuarioRepository;
        this.anexoService = anexoService;
    }

    @GetMapping("/tecnicos")
    public ResponseEntity<List<String>> listarTecnicos() {
        return ResponseEntity.ok(consertoService.listarTecnicosAtivos());
    }

    @GetMapping
    public ResponseEntity<List<ConsertoResponseDTO>> listar(
            @RequestParam(required = false) String busca,
            @RequestParam(required = false) String tipo,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long itemId,
            @RequestParam(required = false) Long departamentoId,
            @RequestParam(required = false) Long escritorioId,
            @RequestParam(required = false) LocalDate dataInicial,
            @RequestParam(required = false) LocalDate dataFinal) {
        return ResponseEntity.ok(consertoService.listar(busca, tipo, status, itemId,
                departamentoId, escritorioId, dataInicial, dataFinal));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsertoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(consertoService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<ConsertoResponseDTO> criar(
            @Valid @RequestBody ConsertoRequestDTO dados, Authentication authentication) {
        Usuario usuario = usuarioRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                        "Usuário autenticado não encontrado."));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(consertoService.criar(dados, usuario.getId()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsertoResponseDTO> atualizar(
            @PathVariable Long id, @Valid @RequestBody ConsertoRequestDTO dados) {
        return ResponseEntity.ok(consertoService.atualizar(id, dados));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        consertoService.excluir(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/anexos")
    public ResponseEntity<List<ConsertoAnexoResponseDTO>> listarAnexos(@PathVariable Long id) {
        return ResponseEntity.ok(anexoService.listar(id));
    }

    @PostMapping(value = "/{id}/anexos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<ConsertoAnexoResponseDTO>> enviarAnexos(
            @PathVariable Long id, @RequestParam("arquivos") List<MultipartFile> arquivos) {
        return ResponseEntity.status(HttpStatus.CREATED).body(anexoService.salvar(id, arquivos));
    }

    @GetMapping("/{id}/anexos/{anexoId}")
    public ResponseEntity<ByteArrayResource> baixarAnexo(@PathVariable Long id, @PathVariable Long anexoId) {
        ConsertoAnexo anexo = anexoService.buscar(id, anexoId);
        ContentDisposition disposition = ContentDisposition.attachment()
                .filename(anexo.getNomeArquivo(), StandardCharsets.UTF_8).build();
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(anexo.getTipoConteudo()))
                .contentLength(anexo.getTamanho())
                .header(HttpHeaders.CONTENT_DISPOSITION, disposition.toString())
                .body(new ByteArrayResource(anexo.getConteudo()));
    }

    @DeleteMapping("/{id}/anexos/{anexoId}")
    public ResponseEntity<Void> excluirAnexo(@PathVariable Long id, @PathVariable Long anexoId) {
        anexoService.excluir(id, anexoId);
        return ResponseEntity.noContent().build();
    }
}
