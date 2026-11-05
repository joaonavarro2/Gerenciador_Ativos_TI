
package br.com.stecar.stecar_backend.service;

import br.com.stecar.stecar_backend.dto.MovimentacaoRequestDTO;
import br.com.stecar.stecar_backend.dto.MovimentacaoResponseDTO;
import br.com.stecar.stecar_backend.entity.Movimentacao;
import br.com.stecar.stecar_backend.repository.MovimentacaoRepository;
import br.com.stecar.stecar_backend.repository.ItemRepository;
import br.com.stecar.stecar_backend.repository.UsuarioRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovimentacaoService {

    private final MovimentacaoRepository movimentacaoRepository;
    private final ItemRepository itemRepository;
    private final UsuarioRepository usuarioRepository;

    public MovimentacaoService(
            MovimentacaoRepository movimentacaoRepository,
            ItemRepository itemRepository,
            UsuarioRepository usuarioRepository) {

        this.movimentacaoRepository = movimentacaoRepository;
        this.itemRepository = itemRepository;
        this.usuarioRepository = usuarioRepository;
    }

    // Lista todas as movimentações, da mais recente para a mais antiga.
    @Transactional(readOnly = true)
    public List<MovimentacaoResponseDTO> listarTodas() {
        return listarTodas(null);
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoResponseDTO> listarTodas(String tipo) {
        return movimentacaoRepository
                .listarTodasComReferencias()
                .stream()
                .map(this::converterParaResponse)
                .filter(m -> tipo == null || tipo.isBlank() || m.getTipo().equalsIgnoreCase(tipo.trim()))
                .toList();
    }

    // Busca uma movimentação pelo ID.
    @Transactional(readOnly = true)
    public MovimentacaoResponseDTO buscarPorId(Long id) {
        return movimentacaoRepository
            .buscarComReferencias(id)
            .stream()
            .findFirst()
            .map(this::converterParaResponse)
            .orElseThrow(() -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Movimentação não encontrada."
            ));

    }

    // Lista o histórico de movimentações de um bem.

    @Transactional(readOnly = true)
    public List<MovimentacaoResponseDTO> listarPorItem(Long itemId) {
        if (!itemRepository.existsById(itemId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Bem não encontrado."
            );
        }

        return movimentacaoRepository
                .findByItemIdOrderByDataDesc(itemId)
                .stream()
                .flatMap(movimentacao ->
                        movimentacaoRepository
                                .buscarComReferencias(movimentacao.getId())
                                .stream()
                                .map(this::converterParaResponse)
                )
                .toList();
    }


    // Lista movimentações por status.

    @Transactional(readOnly = true)
    public List<MovimentacaoResponseDTO> listarPorStatus(String status) {
        return listarPorStatus(status, null);
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoResponseDTO> listarPorStatus(String status, String tipo) {
        if (status == null || status.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe o status da movimentação."
            );
        }

        return movimentacaoRepository
                .findByStatusIgnoreCaseOrderByDataDesc(status.trim())
                .stream()
                .flatMap(movimentacao ->
                        movimentacaoRepository
                                .buscarComReferencias(movimentacao.getId())
                                .stream()
                                .map(this::converterParaResponse)
                )
                .filter(m -> tipo == null || tipo.isBlank() || m.getTipo().equalsIgnoreCase(tipo.trim()))
                .toList();
    }


    // Cadastra uma movimentação.
    @Transactional
    public MovimentacaoResponseDTO cadastrar(
            MovimentacaoRequestDTO dto,
            Long usuarioAutenticadoId) {

        if (dto.getItemId() == null ||
                !itemRepository.existsByIdAndExcluidoFalse(dto.getItemId())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Bem não encontrado."
            );
        }

        if (usuarioAutenticadoId == null ||
                !usuarioRepository.existsById(usuarioAutenticadoId)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Usuário autenticado não encontrado."
            );
        }

        validarOrigemDestino(dto);
        validarReferencias(dto);

        Movimentacao movimentacao = new Movimentacao();

        movimentacao.setTipo(dto.getTipo().trim());
        movimentacao.setData(
                dto.getData() != null
                        ? dto.getData()
                        : LocalDateTime.now()
        );
        movimentacao.setStatus(dto.getStatus().trim());

        movimentacao.setDepartamentoOrigemId(dto.getDepartamentoOrigemId());
        movimentacao.setDepartamentoDestinoId(dto.getDepartamentoDestinoId());
        movimentacao.setEscritorioOrigemId(dto.getEscritorioOrigemId());
        movimentacao.setEscritorioDestinoId(dto.getEscritorioDestinoId());
        movimentacao.setPessoaOrigemId(dto.getPessoaOrigemId());
        movimentacao.setPessoaDestinoId(dto.getPessoaDestinoId());
        movimentacao.setItemId(dto.getItemId());
        movimentacao.setUsuarioId(usuarioAutenticadoId);
        movimentacao.setJustificativa(dto.getJustificativa());

        Movimentacao salva = movimentacaoRepository.save(movimentacao);

        return movimentacaoRepository.buscarComReferencias(salva.getId()).stream()
                .findFirst().map(this::converterParaResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "Não foi possível consultar a movimentação cadastrada."));
    }

    // Atualiza os dados de uma movimentação existente.
    @Transactional
    public MovimentacaoResponseDTO atualizar(
            Long id,
            MovimentacaoRequestDTO dto) {

        Movimentacao movimentacao = movimentacaoRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Movimentação não encontrada."
                ));

        if (dto.getItemId() == null ||
                !itemRepository.existsByIdAndExcluidoFalse(dto.getItemId())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Bem não encontrado."
            );
        }

        validarOrigemDestino(dto);
        validarReferencias(dto);

        movimentacao.setTipo(dto.getTipo().trim());
        movimentacao.setData(
                dto.getData() != null
                        ? dto.getData()
                        : movimentacao.getData()
        );
        movimentacao.setStatus(dto.getStatus().trim());

        movimentacao.setDepartamentoOrigemId(dto.getDepartamentoOrigemId());
        movimentacao.setDepartamentoDestinoId(dto.getDepartamentoDestinoId());
        movimentacao.setEscritorioOrigemId(dto.getEscritorioOrigemId());
        movimentacao.setEscritorioDestinoId(dto.getEscritorioDestinoId());
        movimentacao.setPessoaOrigemId(dto.getPessoaOrigemId());
        movimentacao.setPessoaDestinoId(dto.getPessoaDestinoId());
        movimentacao.setItemId(dto.getItemId());
        movimentacao.setJustificativa(dto.getJustificativa());

        // Mantém o usuário original que registrou a movimentação.
        Movimentacao atualizada = movimentacaoRepository.save(movimentacao);

        return movimentacaoRepository
        .buscarComReferencias(atualizada.getId())
        .stream()
        .findFirst()
        .map(this::converterParaResponse)
        .orElseThrow(() -> new ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Não foi possível consultar a movimentação atualizada."
        ));
    }

    // Exclui uma movimentação.
    @Transactional
    public void excluir(Long id) {
        Movimentacao movimentacao = movimentacaoRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Movimentação não encontrada."
                ));

        movimentacaoRepository.delete(movimentacao);
    }

    private void validarOrigemDestino(MovimentacaoRequestDTO dto) {
        boolean possuiOrigem =
                dto.getDepartamentoOrigemId() != null ||
                dto.getEscritorioOrigemId() != null ||
                dto.getPessoaOrigemId() != null;

        boolean possuiDestino =
                dto.getDepartamentoDestinoId() != null ||
                dto.getEscritorioDestinoId() != null ||
                dto.getPessoaDestinoId() != null;

        if (!possuiOrigem || !possuiDestino) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe ao menos um campo de origem e um de destino."
            );
        }
    }

    private void validarReferencias(MovimentacaoRequestDTO dto) {
        validarReferencia(dto.getDepartamentoOrigemId(), "Departamento de origem", itemRepository::contarDepartamento);
        validarReferencia(dto.getDepartamentoDestinoId(), "Departamento de destino", itemRepository::contarDepartamento);
        validarReferencia(dto.getEscritorioOrigemId(), "Escritório de origem", itemRepository::contarEscritorio);
        validarReferencia(dto.getEscritorioDestinoId(), "Escritório de destino", itemRepository::contarEscritorio);
        validarReferencia(dto.getPessoaOrigemId(), "Pessoa de origem", itemRepository::contarPessoaAtiva);
        validarReferencia(dto.getPessoaDestinoId(), "Pessoa de destino", itemRepository::contarPessoaAtiva);
    }

    private void validarReferencia(Long id, String campo, java.util.function.ToLongFunction<Long> contar) {
        if (id != null && contar.applyAsLong(id) == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, campo + " inválido ou inativo.");
        }
    }

    private MovimentacaoResponseDTO converterParaResponse(
            Movimentacao movimentacao) {

        MovimentacaoResponseDTO dto = new MovimentacaoResponseDTO();

        dto.setId(movimentacao.getId());
        dto.setTipo(movimentacao.getTipo());
        dto.setData(movimentacao.getData());
        dto.setStatus(movimentacao.getStatus());
        dto.setJustificativa(movimentacao.getJustificativa());

        dto.setItemId(movimentacao.getItemId());
        dto.setDepartamentoOrigemId(movimentacao.getDepartamentoOrigemId());
        dto.setDepartamentoDestinoId(movimentacao.getDepartamentoDestinoId());
        dto.setEscritorioOrigemId(movimentacao.getEscritorioOrigemId());
        dto.setEscritorioDestinoId(movimentacao.getEscritorioDestinoId());
        dto.setPessoaOrigemId(movimentacao.getPessoaOrigemId());
        dto.setPessoaDestinoId(movimentacao.getPessoaDestinoId());

        dto.setUsuarioId(movimentacao.getUsuarioId());

        return dto;
        
    }       
    
        
    private MovimentacaoResponseDTO converterParaResponse(
        Object[] dados) {

        MovimentacaoResponseDTO dto = new MovimentacaoResponseDTO();

        dto.setId((Long) dados[0]);
        dto.setTipo((String) dados[1]);
        dto.setData((LocalDateTime) dados[2]);
        dto.setStatus((String) dados[3]);
        dto.setJustificativa((String) dados[4]);

        dto.setItemId((Long) dados[5]);
        dto.setPatrimonioBem((String) dados[6]);
        dto.setNomeBem((String) dados[7]);

        dto.setDepartamentoOrigemId((Long) dados[8]);
        dto.setDepartamentoOrigem((String) dados[9]);

        dto.setDepartamentoDestinoId((Long) dados[10]);
        dto.setDepartamentoDestino((String) dados[11]);

        dto.setEscritorioOrigemId((Long) dados[12]);
        dto.setEscritorioOrigem((String) dados[13]);

        dto.setEscritorioDestinoId((Long) dados[14]);
        dto.setEscritorioDestino((String) dados[15]);

        dto.setPessoaOrigemId((Long) dados[16]);
        dto.setPessoaOrigem((String) dados[17]);

        dto.setPessoaDestinoId((Long) dados[18]);
        dto.setPessoaDestino((String) dados[19]);

        dto.setUsuarioId((Long) dados[20]);
        dto.setResponsavel((String) dados[21]);

        return dto;
    }




}
