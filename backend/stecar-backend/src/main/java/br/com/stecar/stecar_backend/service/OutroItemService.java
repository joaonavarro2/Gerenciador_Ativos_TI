package br.com.stecar.stecar_backend.service;

import br.com.stecar.stecar_backend.dto.OutroItemRequestDTO;
import br.com.stecar.stecar_backend.dto.OutroItemResponseDTO;
import br.com.stecar.stecar_backend.entity.Escritorio;
import br.com.stecar.stecar_backend.entity.OutroItem;
import br.com.stecar.stecar_backend.repository.EscritorioRepository;
import br.com.stecar.stecar_backend.repository.OutroItemRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

@Service
public class OutroItemService {
    private final OutroItemRepository repository;
    private final EscritorioRepository escritorioRepository;

    public OutroItemService(OutroItemRepository repository, EscritorioRepository escritorioRepository) {
        this.repository = repository;
        this.escritorioRepository = escritorioRepository;
    }

    @Transactional(readOnly = true)
    public List<OutroItemResponseDTO> listar(String busca, String categoria, Long escritorioId, String status) {
        String termo = normalizar(busca);
        String categoriaFiltro = normalizar(categoria);
        String statusFiltro = normalizar(status);
        return repository.findAllByOrderByIdDesc().stream()
                .filter(item -> termo.isEmpty() || normalizar(String.join(" ", n(item.getCodigo()), n(item.getNome()),
                        n(item.getCategoria()), n(item.getLocalizacao()), n(item.getFornecedor()))).contains(termo))
                .filter(item -> categoriaFiltro.isEmpty() || normalizar(item.getCategoria()).equals(categoriaFiltro))
                .filter(item -> escritorioId == null || escritorioId.equals(item.getEscritorioId()))
                .filter(item -> statusFiltro.isEmpty() || normalizar(item.getStatus()).equals(statusFiltro))
                .map(this::mapear)
                .toList();
    }

    @Transactional(readOnly = true)
    public OutroItemResponseDTO buscar(Long id) { return mapear(buscarEntidade(id)); }

    @Transactional
    public OutroItemResponseDTO criar(OutroItemRequestDTO dados) {
        validarCodigoUnico(dados.codigo(), null);
        validarEscritorio(dados.escritorioId());
        OutroItem item = new OutroItem();
        aplicar(item, dados);
        return mapear(repository.save(item));
    }

    @Transactional
    public OutroItemResponseDTO atualizar(Long id, OutroItemRequestDTO dados) {
        OutroItem item = buscarEntidade(id);
        validarCodigoUnico(dados.codigo(), id);
        validarEscritorio(dados.escritorioId());
        aplicar(item, dados);
        return mapear(repository.save(item));
    }

    @Transactional
    public void excluir(Long id) { repository.delete(buscarEntidade(id)); }

    private void aplicar(OutroItem item, OutroItemRequestDTO dados) {
        item.setCodigo(dados.codigo().trim());
        item.setNome(dados.nome().trim());
        item.setCategoria(dados.categoria().trim());
        item.setEscritorioId(dados.escritorioId());
        item.setLocalizacao(dados.localizacao().trim());
        item.setQuantidade(dados.quantidade());
        item.setEstoqueMinimo(dados.estoqueMinimo());
        item.setFornecedor(vazioNulo(dados.fornecedor()));
        item.setStatus(validarStatus(dados.status()));
    }

    private void validarCodigoUnico(String codigo, Long id) {
        String valor = codigo.trim();
        boolean duplicado = id == null ? repository.existsByCodigoIgnoreCase(valor)
                : repository.existsByCodigoIgnoreCaseAndIdNot(valor, id);
        if (duplicado) throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um item com este código.");
    }

    private void validarEscritorio(Long id) {
        if (!escritorioRepository.existsById(id))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O escritório informado não existe.");
    }

    private String validarStatus(String status) {
        String valor = normalizar(status);
        if (valor.equals("ativo")) return "Ativo";
        if (valor.equals("inativo")) return "Inativo";
        if (valor.equals("esgotado")) return "Esgotado";
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status inválido. Use Ativo, Inativo ou Esgotado.");
    }

    private OutroItem buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Item não encontrado."));
    }

    private OutroItemResponseDTO mapear(OutroItem item) {
        Escritorio escritorio = escritorioRepository.findById(item.getEscritorioId()).orElse(null);
        return new OutroItemResponseDTO(item.getId(), item.getCodigo(), item.getNome(), item.getCategoria(),
                item.getEscritorioId(), escritorio == null ? null : escritorio.getNome(), item.getLocalizacao(),
                item.getQuantidade(), item.getEstoqueMinimo(), item.getFornecedor(), item.getStatus());
    }

    private String normalizar(String valor) {
        return Normalizer.normalize(n(valor), Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                .trim().toLowerCase(Locale.ROOT);
    }

    private String n(String valor) { return valor == null ? "" : valor; }
    private String vazioNulo(String valor) { return valor == null || valor.isBlank() ? null : valor.trim(); }
}
