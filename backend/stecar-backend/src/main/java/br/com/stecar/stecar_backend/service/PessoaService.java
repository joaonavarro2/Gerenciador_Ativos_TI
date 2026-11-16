package br.com.stecar.stecar_backend.service;

import br.com.stecar.stecar_backend.dto.PessoaRequestDTO;
import br.com.stecar.stecar_backend.dto.PessoaResponseDTO;
import br.com.stecar.stecar_backend.entity.Departamento;
import br.com.stecar.stecar_backend.entity.Escritorio;
import br.com.stecar.stecar_backend.entity.Pessoa;
import br.com.stecar.stecar_backend.repository.DepartamentoRepository;
import br.com.stecar.stecar_backend.repository.EscritorioRepository;
import br.com.stecar.stecar_backend.repository.PessoaRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@Service
public class PessoaService {
    private final PessoaRepository pessoaRepository;
    private final EscritorioRepository escritorioRepository;
    private final DepartamentoRepository departamentoRepository;

    public PessoaService(PessoaRepository pessoaRepository, EscritorioRepository escritorioRepository,
                         DepartamentoRepository departamentoRepository) {
        this.pessoaRepository = pessoaRepository;
        this.escritorioRepository = escritorioRepository;
        this.departamentoRepository = departamentoRepository;
    }

    @Transactional(readOnly = true)
    public List<PessoaResponseDTO> listar(String busca, String status, Long escritorioId, Long departamentoId) {
        String termo = normalizar(busca);
        String filtroStatus = normalizar(status);
        return pessoaRepository.findAllByOrderByNomeCompletoAsc().stream()
                .filter(pessoa -> termo.isEmpty() || normalizar(String.join(" ", pessoa.getNomeCompleto(),
                        valor(pessoa.getMatricula()), valor(pessoa.getCpf()), pessoa.getEmail(), pessoa.getCargo())).contains(termo))
                .filter(pessoa -> filtroStatus.isEmpty() || normalizar(pessoa.getStatus()).equals(filtroStatus))
                .filter(pessoa -> escritorioId == null || escritorioId.equals(pessoa.getEscritorioId()))
                .filter(pessoa -> departamentoId == null || departamentoId.equals(pessoa.getDepartamentoId()))
                .map(this::mapear)
                .toList();
    }

    @Transactional(readOnly = true)
    public PessoaResponseDTO buscar(Long id) { return mapear(buscarEntidade(id)); }

    @Transactional
    public PessoaResponseDTO criar(PessoaRequestDTO dados) {
        validarIdentificadores(dados.cpf(), dados.matricula(), null);
        validarReferencias(dados.escritorioId(), dados.departamentoId());
        Pessoa pessoa = new Pessoa();
        aplicar(pessoa, dados);
        Pessoa salva = pessoaRepository.saveAndFlush(pessoa);
        if (salva.getMatricula() == null) {
            salva.setMatricula(String.format(Locale.ROOT, "MAT-%06d", salva.getId()));
            salva = pessoaRepository.save(salva);
        }
        return mapear(salva);
    }

    @Transactional
    public PessoaResponseDTO atualizar(Long id, PessoaRequestDTO dados) {
        Pessoa pessoa = buscarEntidade(id);
        validarIdentificadores(dados.cpf(), dados.matricula(), id);
        validarReferencias(dados.escritorioId(), dados.departamentoId());
        aplicar(pessoa, dados);
        return mapear(pessoaRepository.save(pessoa));
    }

    @Transactional
    public PessoaResponseDTO alterarStatus(Long id, String status) {
        Pessoa pessoa = buscarEntidade(id);
        pessoa.setStatus(validarStatus(status));
        return mapear(pessoaRepository.save(pessoa));
    }

    @Transactional
    public void excluir(Long id) {
        Pessoa pessoa = buscarEntidade(id);
        if (pessoaRepository.contarVinculos(id) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Esta pessoa possui bens, movimentações ou vínculo como gestor de escritório. Altere o status para Inativo.");
        }
        pessoaRepository.delete(pessoa);
    }

    private void aplicar(Pessoa pessoa, PessoaRequestDTO dados) {
        pessoa.setNomeCompleto(dados.nomeCompleto().trim());
        pessoa.setMatricula(vazioNulo(dados.matricula()));
        pessoa.setCpf(normalizarCpf(dados.cpf()));
        pessoa.setEmail(dados.email().trim().toLowerCase(Locale.ROOT));
        pessoa.setTelefone(vazioNulo(dados.telefone()));
        pessoa.setCargo(dados.cargo().trim());
        pessoa.setEscritorioId(dados.escritorioId());
        pessoa.setDepartamentoId(dados.departamentoId());
        pessoa.setStatus(validarStatus(dados.status()));
    }

    private void validarReferencias(Long escritorioId, Long departamentoId) {
        Escritorio escritorio = escritorioRepository.findById(escritorioId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Escritório inválido."));
        Departamento departamento = departamentoRepository.findById(departamentoId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Departamento inválido."));
        if (departamento.getEscritorioId() != null && !departamento.getEscritorioId().equals(escritorio.getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "O departamento selecionado está vinculado a outro escritório.");
        }
    }

    private void validarIdentificadores(String cpf, String matricula, Long id) {
        String cpfNormalizado = normalizarCpf(cpf);
        if (cpfNormalizado.length() != 11) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF deve conter 11 dígitos.");
        }
        boolean cpfDuplicado = id == null ? pessoaRepository.existsByCpf(cpfNormalizado)
                : pessoaRepository.existsByCpfAndIdNot(cpfNormalizado, id);
        if (cpfDuplicado) throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma pessoa cadastrada com este CPF.");

        String matriculaNormalizada = vazioNulo(matricula);
        if (matriculaNormalizada != null) {
            boolean matriculaDuplicada = id == null ? pessoaRepository.existsByMatriculaIgnoreCase(matriculaNormalizada)
                    : pessoaRepository.existsByMatriculaIgnoreCaseAndIdNot(matriculaNormalizada, id);
            if (matriculaDuplicada) throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe uma pessoa cadastrada com esta matrícula.");
        }
    }

    private String validarStatus(String status) {
        String valor = normalizar(status);
        if (valor.equals("ativo")) return "Ativo";
        if (valor.equals("inativo")) return "Inativo";
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status inválido. Use Ativo ou Inativo.");
    }

    private Pessoa buscarEntidade(Long id) {
        return pessoaRepository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Pessoa não encontrada."));
    }

    private PessoaResponseDTO mapear(Pessoa pessoa) {
        String nomeEscritorio = escritorioRepository.findById(pessoa.getEscritorioId()).map(Escritorio::getNome).orElse(null);
        String nomeDepartamento = departamentoRepository.findById(pessoa.getDepartamentoId()).map(Departamento::getNome).orElse(null);
        return new PessoaResponseDTO(pessoa.getId(), pessoa.getNomeCompleto(), pessoa.getMatricula(), pessoa.getCpf(),
                pessoa.getEmail(), pessoa.getTelefone(), pessoa.getCargo(), pessoa.getEscritorioId(), nomeEscritorio,
                pessoa.getDepartamentoId(), nomeDepartamento, pessoa.getStatus(), pessoaRepository.contarBens(pessoa.getId()));
    }

    private String normalizarCpf(String cpf) { return cpf == null ? "" : cpf.replaceAll("\\D", ""); }
    private String normalizar(String valor) {
        if (valor == null) return "";
        return java.text.Normalizer.normalize(valor, java.text.Normalizer.Form.NFD).replaceAll("\\p{M}", "")
                .trim().toLowerCase(Locale.ROOT);
    }
    private String vazioNulo(String valor) { return valor == null || valor.isBlank() ? null : valor.trim(); }
    private String valor(String valor) { return valor == null ? "" : valor; }
}
