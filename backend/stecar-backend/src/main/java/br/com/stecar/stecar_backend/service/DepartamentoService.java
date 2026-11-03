package br.com.stecar.stecar_backend.service;

import br.com.stecar.stecar_backend.dto.DepartamentoRequestDTO;
import br.com.stecar.stecar_backend.dto.DepartamentoResponseDTO;
import br.com.stecar.stecar_backend.entity.Departamento;
import br.com.stecar.stecar_backend.entity.Escritorio;
import br.com.stecar.stecar_backend.repository.DepartamentoRepository;
import br.com.stecar.stecar_backend.repository.EscritorioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

@Service
public class DepartamentoService {
    private final DepartamentoRepository repository;
    private final EscritorioRepository escritorioRepository;

    public DepartamentoService(DepartamentoRepository repository, EscritorioRepository escritorioRepository) {
        this.repository = repository;
        this.escritorioRepository = escritorioRepository;
    }

    @Transactional(readOnly = true)
    public List<DepartamentoResponseDTO> listar(String busca, String status, Long escritorioId) {
        return repository.findAllByOrderByNomeAsc().stream()
                .filter(item -> busca == null || busca.isBlank() || normalizar(item.getNome()).contains(normalizar(busca)))
                .filter(item -> status == null || status.isBlank() || normalizar(item.getStatus()).equals(normalizar(status)))
                .filter(item -> escritorioId == null || escritorioId.equals(item.getEscritorioId()))
                .map(this::mapear)
                .toList();
    }

    @Transactional(readOnly = true)
    public DepartamentoResponseDTO buscar(Long id) { return mapear(buscarEntidade(id)); }

    @Transactional
    public DepartamentoResponseDTO criar(DepartamentoRequestDTO dados) {
        validarNome(dados.nome(), null);
        Departamento departamento = new Departamento();
        aplicar(departamento, dados);
        return mapear(repository.save(departamento));
    }

    @Transactional
    public DepartamentoResponseDTO atualizar(Long id, DepartamentoRequestDTO dados) {
        Departamento departamento = buscarEntidade(id);
        validarNome(dados.nome(), id);
        aplicar(departamento, dados);
        return mapear(repository.save(departamento));
    }

    @Transactional
    public DepartamentoResponseDTO alterarStatus(Long id, String status) {
        Departamento departamento = buscarEntidade(id);
        departamento.setStatus(validarStatus(status));
        return mapear(repository.save(departamento));
    }

    @Transactional
    public void excluir(Long id) {
        Departamento departamento = buscarEntidade(id);
        if (repository.contarVinculos(id) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Este departamento possui bens, pessoas ou movimentações vinculadas e não pode ser excluído. Altere o status para Inativo.");
        }
        repository.delete(departamento);
    }

    private void aplicar(Departamento departamento, DepartamentoRequestDTO dados) {
        departamento.setNome(dados.nome().trim());
        departamento.setStatus(validarStatus(dados.status()));
        if (dados.escritorioId() != null && !escritorioRepository.existsById(dados.escritorioId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Escritório inválido.");
        }
        departamento.setEscritorioId(dados.escritorioId());
    }

    private void validarNome(String nome, Long id) {
        boolean duplicado = id == null ? repository.existsByNomeIgnoreCase(nome.trim())
                : repository.existsByNomeIgnoreCaseAndIdNot(nome.trim(), id);
        if (duplicado) throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um departamento com este nome.");
    }

    private String validarStatus(String status) {
        String valor = normalizar(status);
        if (valor.equals("ativo")) return "Ativo";
        if (valor.equals("inativo")) return "Inativo";
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status inválido. Use Ativo ou Inativo.");
    }

    private Departamento buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Departamento não encontrado."));
    }

    private DepartamentoResponseDTO mapear(Departamento departamento) {
        String nomeEscritorio = departamento.getEscritorioId() == null ? null
                : escritorioRepository.findById(departamento.getEscritorioId()).map(Escritorio::getNome).orElse(null);
        return new DepartamentoResponseDTO(departamento.getId(), departamento.getNome(), sigla(departamento.getNome()),
                departamento.getStatus(), departamento.getEscritorioId(), nomeEscritorio,
                repository.contarBens(departamento.getId()), repository.contarConsertos(departamento.getId()));
    }

    private String sigla(String nome) {
        return List.of(nome.trim().split("\\s+")).stream().filter(palavra -> !palavra.isBlank())
                .limit(2).map(palavra -> palavra.substring(0, 1).toUpperCase(Locale.ROOT))
                .collect(java.util.stream.Collectors.joining());
    }

    private String normalizar(String valor) {
        if (valor == null) return "";
        return Normalizer.normalize(valor, Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                .trim().toLowerCase(Locale.ROOT);
    }
}
