
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
        return movimentacaoRepository
                .findAllByOrderByDataDesc()
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    // Busca uma movimentação pelo ID.
    @Transactional(readOnly = true)
    public MovimentacaoResponseDTO buscarPorId(Long id) {
        Movimentacao movimentacao = movimentacaoRepository
                .findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Movimentação não encontrada."
                ));

        return converterParaResponse(movimentacao);
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
                .map(this::converterParaResponse)
                .toList();
    }

    // Lista movimentações por status.
    @Transactional(readOnly = true)
    public List<MovimentacaoResponseDTO> listarPorStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Informe o status da movimentação."
            );
        }

        return movimentacaoRepository
                .findByStatusIgnoreCaseOrderByDataDesc(status.trim())
                .stream()
                .map(this::converterParaResponse)
                .toList();
    }

    // Cadastra uma movimentação.
    @Transactional
    public MovimentacaoResponseDTO cadastrar(
            MovimentacaoRequestDTO dto,
            Long usuarioAutenticadoId) {

        if (dto.getItemId() == null ||
                !itemRepository.existsById(dto.getItemId())) {
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

        return converterParaResponse(salva);
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
                !itemRepository.existsById(dto.getItemId())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Bem não encontrado."
            );
        }

        validarOrigemDestino(dto);

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

        return converterParaResponse(atualizada);
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
}
