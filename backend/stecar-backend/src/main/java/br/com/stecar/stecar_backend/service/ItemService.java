package br.com.stecar.stecar_backend.service;

import br.com.stecar.stecar_backend.dto.ItemOpcaoDTO;
import br.com.stecar.stecar_backend.dto.ItemOpcoesResponseDTO;
import br.com.stecar.stecar_backend.dto.ItemRequestDTO;
import br.com.stecar.stecar_backend.dto.ItemResponseDTO;
import br.com.stecar.stecar_backend.dto.MovimentacaoItemRequestDTO;
import br.com.stecar.stecar_backend.dto.MovimentacaoItemResponseDTO;
import br.com.stecar.stecar_backend.dto.ConsertoItemResponseDTO;
import br.com.stecar.stecar_backend.entity.Item;
import br.com.stecar.stecar_backend.entity.InstituicaoPatrimonio;
import br.com.stecar.stecar_backend.repository.ItemRepository;
import br.com.stecar.stecar_backend.repository.MovimentacaoRepository;
import br.com.stecar.stecar_backend.repository.OutroItemRepository;
import br.com.stecar.stecar_backend.repository.UsuarioRepository;
import br.com.stecar.stecar_backend.dto.HistoricoInstituicaoResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;
import java.time.LocalDate;
import java.util.stream.Stream;

@Service
public class ItemService {

    private static final List<String> CATEGORIAS_PADRAO = List.of(
            "Computadores",
            "Impressão",
            "Impressoras",
            "Monitores",
            "Notebooks",
            "Outros",
            "Periféricos",
            "Rede",
            "Scanner");

    private final ItemRepository itemRepository;
    private final UsuarioRepository usuarioRepository;
    private final MovimentacaoRepository movimentacaoRepository;
    private final HistoricoInstituicaoService historicoInstituicaoService;
    private final OutroItemRepository outroItemRepository;

    public ItemService(ItemRepository itemRepository, UsuarioRepository usuarioRepository,
            MovimentacaoRepository movimentacaoRepository,
            HistoricoInstituicaoService historicoInstituicaoService,
            OutroItemRepository outroItemRepository) {
        this.movimentacaoRepository = movimentacaoRepository;
        this.itemRepository = itemRepository;
        this.usuarioRepository = usuarioRepository;
        this.historicoInstituicaoService = historicoInstituicaoService;
        this.outroItemRepository = outroItemRepository;
    }

    @Transactional(readOnly = true)
    public List<ItemResponseDTO> listar(String busca, String categoria, String status, Long departamentoId) {
        return listar(busca, categoria, status, departamentoId, null);
    }

    @Transactional(readOnly = true)
    public List<ItemResponseDTO> listar(
            String busca, String categoria, String status, Long departamentoId, String departamento) {
        return listar(busca, categoria, status, departamentoId, departamento, null);
    }

    @Transactional(readOnly = true)
    public List<ItemResponseDTO> listar(
            String busca, String categoria, String status, Long departamentoId,
            String departamento, InstituicaoPatrimonio instituicao) {
        return itemRepository.listarComReferencias().stream()
                .map(this::mapearItem)
                .filter(item -> corresponde(item.nome() + " " + item.codigo() + " " + item.patrimonio() + " " + item.serial(), busca))
                .filter(item -> correspondeALista(item.categoria(), categoria))
                .filter(item -> correspondeALista(item.status(), status))
                .filter(item -> departamentoId == null || departamentoId.equals(item.departamentoId()))
                .filter(item -> corresponde(item.departamento(), departamento))
                .filter(item -> instituicao == null || instituicao == item.instituicao())
                .toList();
    }

    @Transactional(readOnly = true)
    public ItemResponseDTO buscarPorId(Long id) {
        return itemRepository.buscarComReferencias(id).stream()
                .findFirst()
                .map(this::mapearItem)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bem não encontrado."));
    }

    @Transactional(readOnly = true)
    public List<ItemResponseDTO> listarPorEscritorio(Long escritorioId) {
        if (itemRepository.contarEscritorio(escritorioId) == 0) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Escritório não encontrado.");
        }
        return itemRepository.listarComReferencias().stream()
                .map(this::mapearItem)
                .filter(item -> escritorioId.equals(item.escritorioId()))
                .toList();
    }

        @Transactional(readOnly = true)
        public List<MovimentacaoItemResponseDTO> listarMovimentacoes(Long id) {
            validarExistencia(id);
        return itemRepository.listarMovimentacoes(id).stream()
            .map(row -> new MovimentacaoItemResponseDTO(
                numero(row[0]), (String) row[1], dataHoraLocal(row[2]),
                (String) row[3], (String) row[4], (String) row[5]))
            .toList();
        }

        @Transactional(readOnly = true)
        public List<ConsertoItemResponseDTO> listarConsertos(Long id) {
            validarExistencia(id);
        return itemRepository.listarConsertos(id).stream()
            .map(row -> new ConsertoItemResponseDTO(
                numero(row[0]), (String) row[1], dataHoraLocal(row[2]),
                (String) row[3], (String) row[4]))
            .toList();
        }

    @Transactional(readOnly = true)
    public List<HistoricoInstituicaoResponseDTO> listarHistoricoInstituicao(Long id) {
        validarExistencia(id);
        return historicoInstituicaoService.listar("ITEM", id);
    }

    @Transactional(readOnly = true)
    public ItemOpcoesResponseDTO buscarOpcoes() {
        List<ItemOpcaoDTO> escritorios = itemRepository.listarEscritorios().stream()
                .map(row -> new ItemOpcaoDTO(numero(row[0]), (String) row[1], null))
                .toList();
        List<ItemOpcaoDTO> departamentos = itemRepository.listarDepartamentos().stream()
                .map(row -> new ItemOpcaoDTO(numero(row[0]), (String) row[1], null))
                .toList();
        List<ItemOpcaoDTO> pessoas = itemRepository.listarPessoasAtivas().stream()
                .map(row -> new ItemOpcaoDTO(numero(row[0]), (String) row[1], numero(row[2])))
                .toList();
        List<String> categorias = Stream.concat(
                        CATEGORIAS_PADRAO.stream(), itemRepository.listarCategorias().stream())
                .distinct()
                .sorted()
                .toList();
        return new ItemOpcoesResponseDTO(
            escritorios, departamentos, pessoas,
            categorias, itemRepository.listarStatus());
    }

    @Transactional
    public ItemResponseDTO criar(ItemRequestDTO dados) {
        return criar(dados, null);
    }

    @Transactional
    public ItemResponseDTO criar(ItemRequestDTO dados, String emailUsuario) {
        validarParaImportacao(dados);
        Item item = new Item();
        preencherItem(item, dados);
        return salvarERetornar(item, null, emailUsuario);
    }

    @Transactional
    public ItemResponseDTO atualizar(Long id, ItemRequestDTO dados) {
        return atualizar(id, dados, null);
    }

    @Transactional
    public ItemResponseDTO atualizar(Long id, ItemRequestDTO dados, String emailUsuario) {
        validarReferencias(dados);
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bem não encontrado."));
        if (item.isExcluido()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bem não encontrado.");
        }
        InstituicaoPatrimonio instituicaoAnterior = item.getInstituicao();
        validarCodigo(dados.codigo(), id);
        validarPatrimonioUnico(dados.patrimonio(), id, item.getPatrimonio());
        preencherItem(item, dados);
        return salvarERetornar(item, instituicaoAnterior, emailUsuario);
    }

    @Transactional
    public void excluir(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bem não encontrado."));
        if (item.isExcluido()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bem não encontrado.");
        }
        item.setExcluido(true);
        itemRepository.save(item);
    }

    @Transactional
    public ItemResponseDTO movimentar(Long id, MovimentacaoItemRequestDTO dados, String emailUsuario) {
        if (!List.of("Transferência", "Empréstimo", "Devolução").contains(dados.tipo())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de movimentação inválido.");
        }
        validarEscritorio(dados.escritorioDestinoId());
        validarDepartamento(dados.departamentoDestinoId());
        validarDepartamentoNoEscritorio(dados.departamentoDestinoId(), dados.escritorioDestinoId());
        validarPessoa(dados.responsavelDestinoId());
        validarPessoaNoLocal(
                dados.responsavelDestinoId(),
                dados.escritorioDestinoId(),
                dados.departamentoDestinoId());

        Item item = itemRepository.bloquearPorId(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Bem não encontrado."));
        if (item.isExcluido()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bem não encontrado.");
        }
        Long usuarioId = usuarioRepository.findByEmail(emailUsuario)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário autenticado não encontrado."))
                .getId();

        itemRepository.registrarMovimentacao(
                dados.tipo(), dados.data(), item.getDepartamentoId(), dados.departamentoDestinoId(),
                item.getEscritorioId(), dados.escritorioDestinoId(), item.getPessoaId(),
                dados.responsavelDestinoId(), item.getId(), usuarioId, dados.justificativa());

        // Lançamentos retroativos ficam só no histórico: a localização atual
        // segue a movimentação mais recente.
        if (!movimentacaoRepository.existsByItemIdAndDataAfter(id, dados.data())) {
            item.setEscritorioId(dados.escritorioDestinoId());
            item.setDepartamentoId(dados.departamentoDestinoId());
            item.setPessoaId(dados.responsavelDestinoId());
            itemRepository.save(item);
        }
        return buscarPorId(id);
    }

    private ItemResponseDTO salvarERetornar(
            Item item, InstituicaoPatrimonio instituicaoAnterior, String emailUsuario) {
        Long id = itemRepository.save(item).getId();
        historicoInstituicaoService.registrar(
                "ITEM", id, instituicaoAnterior, item.getInstituicao(), emailUsuario);
        return buscarPorId(id);
    }

    private void validarExistencia(Long id) {
        if (!itemRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Bem não encontrado.");
        }
    }

    private void preencherItem(Item item, ItemRequestDTO dados) {
        item.setCodigo(dados.codigo().trim());
        item.setPatrimonio(normalizar(dados.patrimonio()));
        if (dados.instituicao() != null) {
            item.setInstituicao(dados.instituicao());
        }
        item.setNome(dados.nome().trim());
        item.setCategoria(dados.categoria().trim());
        item.setSerial(normalizar(dados.serial()));
        item.setFabricante(dados.fabricante().trim());
        item.setModelo(dados.modelo().trim());
        item.setDescricao(normalizar(dados.descricao()));
        item.setStatus(dados.status().trim());
        item.setDataAquisicao(dados.dataAquisicao());
        item.setEscritorioId(dados.escritorioId());
        item.setDepartamentoId(dados.departamentoId());
        item.setPessoaId(dados.pessoaId());
    }

    private void validarReferencias(ItemRequestDTO dados) {
        validarEscritorio(dados.escritorioId());
        validarDepartamento(dados.departamentoId());
        validarDepartamentoNoEscritorio(dados.departamentoId(), dados.escritorioId());
        validarPessoa(dados.pessoaId());
        validarPessoaNoLocal(dados.pessoaId(), dados.escritorioId(), dados.departamentoId());
    }

    private void validarEscritorio(Long id) {
        if (id == null || itemRepository.contarEscritorio(id) == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Escritório inválido.");
        }
    }

    private void validarDepartamento(Long id) {
        if (id == null || itemRepository.contarDepartamento(id) == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Departamento inválido.");
        }
    }

    private void validarDepartamentoNoEscritorio(Long departamentoId, Long escritorioId) {
        if (itemRepository.contarDepartamentoNoEscritorio(departamentoId, escritorioId) == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O departamento selecionado pertence a outro escritório.");
        }
    }

    private void validarPessoa(Long id) {
        if (id != null && itemRepository.contarPessoaAtiva(id) == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Responsável inválido ou inativo.");
        }
    }

    private void validarPessoaNoLocal(Long pessoaId, Long escritorioId, Long departamentoId) {
        if (pessoaId != null
                && itemRepository.contarPessoaAtivaNoLocal(pessoaId, escritorioId, departamentoId) == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O responsável selecionado não pertence ao escritório e departamento informados.");
        }
    }

    private void validarCodigo(String codigo, Long itemId) {
        boolean duplicado = itemId == null
                ? itemRepository.existsByCodigoAndExcluidoFalse(codigo.trim())
                : itemRepository.existsByCodigoAndIdNotAndExcluidoFalse(codigo.trim(), itemId);
        if (duplicado) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Já existe um bem ativo com esse código interno. Informe outro código.");
        }
    }

    private void validarInstituicaoObrigatoria(InstituicaoPatrimonio instituicao) {
        if (instituicao == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A instituição do bem é obrigatória.");
        }
    }

    @Transactional(readOnly = true)
    public void validarParaImportacao(ItemRequestDTO dados) {
        validarInstituicaoObrigatoria(dados.instituicao());
        validarReferencias(dados);
        validarCodigo(dados.codigo(), null);
        validarPatrimonioUnico(dados.patrimonio(), null, null);
    }

    private void validarPatrimonioUnico(String patrimonio, Long itemId, String patrimonioAnterior) {
        String valor = normalizar(patrimonio);
        if (valor == null || valor.equalsIgnoreCase(normalizar(patrimonioAnterior))) return;
        if (itemRepository.existePatrimonio(valor, itemId)
                || outroItemRepository.existePatrimonio(valor, null)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Já existe um registro com este número de patrimônio.");
        }
    }

    private ItemResponseDTO mapearItem(Object[] row) {
        Long id = numero(row[0]);
        String codigo = row[1] == null ? String.format(Locale.ROOT, "BEM-%03d", id) : (String) row[1];
        return new ItemResponseDTO(
            id, codigo, (String) row[2], instituicao(row[17]), (String) row[3], (String) row[4], (String) row[5],
            (String) row[6], (String) row[7], (String) row[8], (String) row[9],
                dataLocal(row[10]),
            numero(row[11]), (String) row[12], numero(row[13]), (String) row[14],
            numero(row[15]), (String) row[16]);
    }

    private InstituicaoPatrimonio instituicao(Object value) {
        return value == null ? null : InstituicaoPatrimonio.valueOf(value.toString());
    }

    private Long numero(Object value) {
        return value == null ? null : ((Number) value).longValue();
    }

    private LocalDate dataLocal(Object value) {
        if (value instanceof LocalDate data) {
            return data;
        }
        if (value instanceof java.sql.Date data) {
            return data.toLocalDate();
        }
        return null;
    }

    private java.time.LocalDateTime dataHoraLocal(Object value) {
        if (value instanceof java.time.LocalDateTime dataHora) {
            return dataHora;
        }
        if (value instanceof java.sql.Timestamp dataHora) {
            return dataHora.toLocalDateTime();
        }
        return null;
    }

    private String normalizar(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private boolean corresponde(String valor, String filtro) {
        return filtro == null || filtro.isBlank()
                || (valor != null && valor.toLowerCase(Locale.ROOT).contains(filtro.trim().toLowerCase(Locale.ROOT)));
    }

    private boolean correspondeALista(String valor, String filtros) {
        return filtros == null || filtros.isBlank()
                || List.of(filtros.split(",")).stream().anyMatch(filtro -> corresponde(valor, filtro));
    }
}
