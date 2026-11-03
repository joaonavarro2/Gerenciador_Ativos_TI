package br.com.stecar.stecar_backend.service;

import br.com.stecar.stecar_backend.dto.EscritorioRequestDTO;
import br.com.stecar.stecar_backend.dto.EscritorioResponseDTO;
import br.com.stecar.stecar_backend.entity.Escritorio;
import br.com.stecar.stecar_backend.repository.EscritorioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@Service
public class EscritorioService {
    private final EscritorioRepository repository;

    public EscritorioService(EscritorioRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<EscritorioResponseDTO> listar(String busca, String status, String cidade) {
        String termo = normalizar(busca);
        String filtroCidade = normalizar(cidade);
        String filtroStatus = normalizar(status);
        return repository.findAllByOrderByNomeAsc().stream()
                .filter(item -> termo.isEmpty() || normalizar(String.join(" ",
                        n(item.getNome()), n(item.getCidade()), n(item.getEstado()), n(item.getGestor()), n(item.getEndereco()))).contains(termo))
                .filter(item -> filtroCidade.isEmpty() || normalizar(item.getCidade()).equals(filtroCidade))
                .filter(item -> filtroStatus.isEmpty() || normalizar(item.getStatus()).equals(filtroStatus))
                .map(this::mapear)
                .toList();
    }

    @Transactional(readOnly = true)
    public EscritorioResponseDTO buscar(Long id) {
        return mapear(buscarEntidade(id));
    }

    @Transactional
    public EscritorioResponseDTO criar(EscritorioRequestDTO dados) {
        validarNomeUnico(dados.nome(), null);
        Escritorio item = new Escritorio();
        aplicar(item, dados);
        return mapear(repository.save(item));
    }

    @Transactional
    public EscritorioResponseDTO atualizar(Long id, EscritorioRequestDTO dados) {
        Escritorio item = buscarEntidade(id);
        validarNomeUnico(dados.nome(), id);
        aplicar(item, dados);
        return mapear(repository.save(item));
    }

    @Transactional
    public EscritorioResponseDTO alterarStatus(Long id, String status) {
        Escritorio item = buscarEntidade(id);
        item.setStatus(validarStatus(status));
        return mapear(repository.save(item));
    }

    @Transactional
    public void excluir(Long id) {
        Escritorio item = buscarEntidade(id);
        if (repository.contarReferencias(id) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Este escritório possui bens, pessoas ou movimentações vinculadas e não pode ser excluído. Altere o status para Inativo.");
        }
        repository.delete(item);
    }

    private void aplicar(Escritorio item, EscritorioRequestDTO dados) {
        item.setNome(dados.nome().trim());
        item.setCidade(dados.cidade().trim());
        item.setEstado(vazioNulo(dados.estado()));
        item.setCep(valorOuPadrao(dados.cep(), "N/A"));
        item.setEndereco(valorOuPadrao(dados.endereco(), "Endereço não informado"));
        item.setTelefone(vazioNulo(dados.telefone()));
        item.setEmail(vazioNulo(dados.email()));
        item.setGestor(vazioNulo(dados.gestor()));
        item.setStatus(validarStatus(dados.status()));
    }

    private void validarNomeUnico(String nome, Long id) {
        boolean duplicado = id == null ? repository.existsByNomeIgnoreCase(nome.trim())
                : repository.existsByNomeIgnoreCaseAndIdNot(nome.trim(), id);
        if (duplicado) throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um escritório com este nome.");
    }

    private String validarStatus(String status) {
        String valor = status == null ? "" : status.trim();
        String statusNormalizado = normalizar(valor);
        if (statusNormalizado.equals("ativo")) return "Ativo";
        if (statusNormalizado.equals("inativo")) return "Inativo";
        if (statusNormalizado.equals("em expansao")) return "Em expansão";
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status inválido. Use Ativo, Inativo ou Em expansão.");
    }

    private Escritorio buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Escritório não encontrado."));
    }

    private EscritorioResponseDTO mapear(Escritorio item) {
        return new EscritorioResponseDTO(item.getId(), item.getNome(), item.getCidade(), item.getEstado(),
                item.getCep(), item.getEndereco(), item.getTelefone(), item.getEmail(), item.getGestor(),
                item.getGestor(), item.getStatus());
    }

    private String normalizar(String valor) {
        return java.text.Normalizer.normalize(n(valor), java.text.Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "").trim().toLowerCase(Locale.ROOT);
    }

    private String n(String valor) { return valor == null ? "" : valor; }
    private String vazioNulo(String valor) { return valor == null || valor.isBlank() ? null : valor.trim(); }
    private String valorOuPadrao(String valor, String padrao) { return valor == null || valor.isBlank() ? padrao : valor.trim(); }
}
