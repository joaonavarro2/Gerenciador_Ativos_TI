package br.com.stecar.stecar_backend.service;

import br.com.stecar.stecar_backend.dto.ConsertoRequestDTO;
import br.com.stecar.stecar_backend.dto.ConsertoResponseDTO;
import br.com.stecar.stecar_backend.entity.Conserto;
import br.com.stecar.stecar_backend.repository.ConsertoRepository;
import br.com.stecar.stecar_backend.repository.ItemRepository;
import br.com.stecar.stecar_backend.repository.UsuarioRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ConsertoService {
    private final ConsertoRepository consertoRepository;
    private final ItemRepository itemRepository;
    private final UsuarioRepository usuarioRepository;

    public ConsertoService(ConsertoRepository consertoRepository,
                           ItemRepository itemRepository,
                           UsuarioRepository usuarioRepository) {
        this.consertoRepository = consertoRepository;
        this.itemRepository = itemRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<String> listarTecnicosAtivos() {
        return usuarioRepository.findByStatusIgnoreCaseOrderByNomeCompletoAsc("ATIVO").stream()
                .map(usuario -> usuario.getNomeCompleto())
                .filter(nome -> nome != null && !nome.isBlank())
                .distinct()
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ConsertoResponseDTO> listar(String busca, String tipo, String status,
            Long itemId, Long departamentoId, Long escritorioId,
            LocalDate dataInicial, LocalDate dataFinal) {
        if (dataInicial != null && dataFinal != null && dataFinal.isBefore(dataInicial)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A data final não pode ser anterior à data inicial.");
        }
        return consertoRepository.listarComReferencias().stream()
                .map(this::mapear)
                .filter(c -> contem(c, busca))
                .filter(c -> igual(c.tipo(), tipo))
                .filter(c -> igual(c.status(), status))
                .filter(c -> itemId == null || itemId.equals(c.itemId()))
                .filter(c -> departamentoId == null || departamentoId.equals(c.departamentoId()))
                .filter(c -> escritorioId == null || escritorioId.equals(c.escritorioId()))
                .filter(c -> dataInicial == null || !c.dataEntrada().toLocalDate().isBefore(dataInicial))
                .filter(c -> dataFinal == null || !c.dataEntrada().toLocalDate().isAfter(dataFinal))
                .toList();
    }

    @Transactional(readOnly = true)
    public ConsertoResponseDTO buscarPorId(Long id) {
        return consertoRepository.buscarComReferencias(id).stream()
                .findFirst()
                .map(this::mapear)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Reparo não encontrado."));
    }

    @Transactional
    public ConsertoResponseDTO criar(ConsertoRequestDTO dados, Long usuarioId) {
        validar(dados);
        if (usuarioId == null || !usuarioRepository.existsById(usuarioId)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Usuário autenticado não encontrado.");
        }
        Conserto conserto = new Conserto();
        conserto.setUsuarioId(usuarioId);
        preencher(conserto, dados);
        return salvarERetornar(conserto);
    }

    @Transactional
    public ConsertoResponseDTO atualizar(Long id, ConsertoRequestDTO dados) {
        validar(dados);
        Conserto conserto = consertoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Reparo não encontrado."));
        preencher(conserto, dados);
        return salvarERetornar(conserto);
    }

    @Transactional
    public void excluir(Long id) {
        Conserto conserto = consertoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Reparo não encontrado."));
        consertoRepository.delete(conserto);
    }

    private void validar(ConsertoRequestDTO dados) {
        if (!itemRepository.existsByIdAndExcluidoFalse(dados.itemId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Bem não encontrado ou arquivado.");
        }
        if (dados.dataSaida() != null && dados.dataSaida().isBefore(dados.dataEntrada())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A data de conclusão não pode ser anterior à data de início.");
        }
        String status = normalizar(dados.status());
        if (!List.of("pendente", "em andamento", "concluido").contains(status)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Status inválido. Use Pendente, Em andamento ou Concluído.");
        }
    }

    private void preencher(Conserto c, ConsertoRequestDTO d) {
        c.setTipo(d.tipo().trim());
        c.setDescricaoProblema(d.descricaoProblema().trim());
        c.setDescricaoSolucao(normalizarTexto(d.descricaoSolucao()));
        c.setDataEntrada(d.dataEntrada());
        c.setDataSaida(d.dataSaida());
        c.setStatus(d.status().trim());
        c.setTecnico(d.tecnico().trim());
        c.setItemId(d.itemId());
    }

    private ConsertoResponseDTO salvarERetornar(Conserto c) {
        Long id = consertoRepository.save(c).getId();
        return buscarPorId(id);
    }

    private ConsertoResponseDTO mapear(Object[] r) {
        return new ConsertoResponseDTO(numero(r[0]), (String) r[1], (String) r[2],
                (String) r[3], data(r[4]), data(r[5]), (String) r[6], (String) r[7],
                numero(r[8]), (String) r[9], (String) r[10], (String) r[11],
                numero(r[12]), (String) r[13], numero(r[14]), (String) r[15],
                numero(r[16]), (String) r[17]);
    }

    private Long numero(Object value) {
        return value == null ? null : ((Number) value).longValue();
    }

    private LocalDateTime data(Object value) {
        if (value instanceof LocalDateTime date) return date;
        if (value instanceof java.sql.Timestamp timestamp) return timestamp.toLocalDateTime();
        return null;
    }

    private boolean igual(String valor, String filtro) {
        return filtro == null || filtro.isBlank() || normalizar(valor).equals(normalizar(filtro));
    }

    private boolean contem(ConsertoResponseDTO c, String busca) {
        if (busca == null || busca.isBlank()) return true;
        String termo = normalizar(busca);
        return java.util.Arrays.asList(c.codigoBem(), c.patrimonioBem(), c.nomeBem(), c.descricaoProblema(),
                        c.tecnico(), c.tipo(), c.status(), c.escritorio(), c.departamento())
                .stream().anyMatch(v -> v != null && normalizar(v).contains(termo));
    }

    private String normalizar(String valor) {
        return valor == null ? "" : java.text.Normalizer.normalize(valor.trim().toLowerCase(Locale.ROOT),
                java.text.Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }

    private String normalizarTexto(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
