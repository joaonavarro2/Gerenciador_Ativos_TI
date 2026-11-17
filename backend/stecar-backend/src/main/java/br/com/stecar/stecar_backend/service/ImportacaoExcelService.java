package br.com.stecar.stecar_backend.service;

import br.com.stecar.stecar_backend.dto.ImportacaoExcelLinhaDTO;
import br.com.stecar.stecar_backend.dto.ImportacaoExcelLinhaResultadoDTO;
import br.com.stecar.stecar_backend.dto.ImportacaoExcelRequestDTO;
import br.com.stecar.stecar_backend.dto.ImportacaoExcelResultadoDTO;
import br.com.stecar.stecar_backend.dto.ItemOpcoesResponseDTO;
import br.com.stecar.stecar_backend.dto.ItemRequestDTO;
import br.com.stecar.stecar_backend.dto.OutroItemRequestDTO;
import br.com.stecar.stecar_backend.entity.Escritorio;
import br.com.stecar.stecar_backend.entity.InstituicaoPatrimonio;
import br.com.stecar.stecar_backend.repository.EscritorioRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ImportacaoExcelService {
    private static final String BENS = "bens";
    private static final String OUTROS_ITENS = "outros-itens";

    private final ItemService itemService;
    private final OutroItemService outroItemService;
    private final EscritorioRepository escritorioRepository;
    private final Validator validator;

    public ImportacaoExcelService(
            ItemService itemService,
            OutroItemService outroItemService,
            EscritorioRepository escritorioRepository,
            Validator validator) {
        this.itemService = itemService;
        this.outroItemService = outroItemService;
        this.escritorioRepository = escritorioRepository;
        this.validator = validator;
    }

    @Transactional(readOnly = true)
    public ImportacaoExcelResultadoDTO validar(String tipo, ImportacaoExcelRequestDTO request) {
        return processar(tipo, request, false, null);
    }

    @Transactional
    public ImportacaoExcelResultadoDTO confirmar(
            String tipo, ImportacaoExcelRequestDTO request, String emailUsuario) {
        return processar(tipo, request, true, emailUsuario);
    }

    private ImportacaoExcelResultadoDTO processar(
            String tipo, ImportacaoExcelRequestDTO request, boolean confirmar, String emailUsuario) {
        validarTipo(tipo);
        List<ImportacaoExcelLinhaDTO> entradas = request == null || request.linhas() == null
                ? List.of() : request.linhas();
        ItemOpcoesResponseDTO opcoesBens = BENS.equals(tipo) ? itemService.buscarOpcoes() : null;
        List<Escritorio> escritorios = OUTROS_ITENS.equals(tipo) ? escritorioRepository.findAll() : List.of();
        Set<String> codigos = new HashSet<>();
        Set<String> patrimonios = new HashSet<>();
        List<LinhaPreparada> preparadas = new ArrayList<>();

        for (int indice = 0; indice < entradas.size(); indice++) {
            ImportacaoExcelLinhaDTO entrada = entradas.get(indice);
            int numeroLinha = entrada == null || entrada.numeroLinha() == null
                    ? indice + 2 : entrada.numeroLinha();
            Map<String, String> dados = entrada == null || entrada.dados() == null
                    ? Map.of() : entrada.dados();
            List<String> erros = new ArrayList<>();
            if (BENS.equals(tipo)) {
                ItemRequestDTO item = converterBem(dados, opcoesBens, erros);
                if (item != null) {
                    validarDto(item, erros);
                    if (erros.isEmpty()) {
                        try {
                            itemService.validarParaImportacao(item);
                        } catch (ResponseStatusException erro) {
                            erros.add(erro.getReason());
                        }
                    }
                    verificarDuplicados(item.codigo(), item.patrimonio(), codigos, patrimonios, erros);
                    preparadas.add(new LinhaPreparada(numeroLinha, dados, erros, item, null));
                } else {
                    preparadas.add(new LinhaPreparada(numeroLinha, dados, erros, null, null));
                }
            } else {
                OutroItemRequestDTO item = converterOutroItem(dados, escritorios, erros);
                if (item != null) {
                    validarDto(item, erros);
                    if (erros.isEmpty()) {
                        try {
                            outroItemService.validarParaImportacao(item);
                        } catch (ResponseStatusException erro) {
                            erros.add(erro.getReason());
                        }
                    }
                    verificarDuplicados(item.codigo(), item.patrimonio(), codigos, patrimonios, erros);
                    preparadas.add(new LinhaPreparada(numeroLinha, dados, erros, null, item));
                } else {
                    preparadas.add(new LinhaPreparada(numeroLinha, dados, erros, null, null));
                }
            }
        }

        List<ImportacaoExcelLinhaResultadoDTO> resultados = new ArrayList<>();
        int validos = 0;
        int importados = 0;
        for (LinhaPreparada linha : preparadas) {
            Long id = null;
            if (linha.erros().isEmpty()) {
                validos++;
                if (confirmar) {
                    if (linha.bem() != null) {
                        id = itemService.criar(linha.bem(), emailUsuario).id();
                    } else {
                        id = outroItemService.criar(linha.outroItem(), emailUsuario).id();
                    }
                    importados++;
                }
            }
            resultados.add(new ImportacaoExcelLinhaResultadoDTO(
                    linha.numeroLinha(), id, linha.dados(), List.copyOf(linha.erros())));
        }
        return new ImportacaoExcelResultadoDTO(
                preparadas.size(), validos, importados, preparadas.size() - validos, resultados);
    }

    private ItemRequestDTO converterBem(
            Map<String, String> dados, ItemOpcoesResponseDTO opcoes, List<String> erros) {
        InstituicaoPatrimonio instituicao = instituicao(valor(dados, "instituicao"), erros);
        LocalDate dataAquisicao = data(valor(dados, "dataAquisicao"), erros);
        Long escritorioId = resolverOpcao(valor(dados, "escritorio"), opcoes.escritorios().stream()
                .map(opcao -> new Opcao(opcao.id(), opcao.nome())).toList(), "escritório", erros);
        Long departamentoId = resolverOpcao(valor(dados, "departamento"), opcoes.departamentos().stream()
                .map(opcao -> new Opcao(opcao.id(), opcao.nome())).toList(), "departamento", erros);
        Long pessoaId = resolverOpcaoOpcional(valor(dados, "responsavel"), opcoes.pessoas().stream()
                .map(opcao -> new Opcao(opcao.id(), opcao.nome())).toList(), "responsável", erros);
        return new ItemRequestDTO(
                valor(dados, "codigo"),
                valor(dados, "patrimonio"),
                instituicao,
                valor(dados, "nome"),
                valor(dados, "categoria"),
                valor(dados, "serial"),
                valor(dados, "fabricante"),
                valor(dados, "modelo"),
                valor(dados, "descricao"),
                valor(dados, "status"),
                dataAquisicao,
                escritorioId,
                departamentoId,
                pessoaId);
    }

    private OutroItemRequestDTO converterOutroItem(
            Map<String, String> dados, List<Escritorio> escritorios, List<String> erros) {
        InstituicaoPatrimonio instituicao = instituicao(valor(dados, "instituicao"), erros);
        Long escritorioId = resolverOpcao(valor(dados, "escritorio"), escritorios.stream()
                .map(escritorio -> new Opcao(escritorio.getId(), escritorio.getNome())).toList(),
                "escritório", erros);
        Integer quantidade = inteiro(valor(dados, "quantidade"), "quantidade", erros);
        Integer estoqueMinimo = inteiro(valor(dados, "estoqueMinimo"), "estoque mínimo", erros);
        return new OutroItemRequestDTO(
                valor(dados, "codigo"),
                valor(dados, "patrimonio"),
                instituicao,
                valor(dados, "nome"),
                valor(dados, "categoria"),
                escritorioId,
                valor(dados, "localizacao"),
                quantidade,
                estoqueMinimo,
                valor(dados, "fornecedor"),
                valor(dados, "status"));
    }

    private <T> void validarDto(T dto, List<String> erros) {
        validator.validate(dto).stream()
                .sorted((a, b) -> a.getPropertyPath().toString().compareTo(b.getPropertyPath().toString()))
                .map(this::mensagemViolacao)
                .forEach(erros::add);
    }

    private String mensagemViolacao(ConstraintViolation<?> violacao) {
        return violacao.getPropertyPath() + ": " + violacao.getMessage();
    }

    private InstituicaoPatrimonio instituicao(String valor, List<String> erros) {
        if (valor == null || valor.isBlank()) return null;
        try {
            return InstituicaoPatrimonio.valueOf(valor.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException erro) {
            erros.add("instituicao: use CAR ou FLEM.");
            return null;
        }
    }

    private LocalDate data(String valor, List<String> erros) {
        if (valor == null || valor.isBlank()) return null;
        try {
            return LocalDate.parse(valor.trim());
        } catch (DateTimeParseException ignorado) {
            try {
                return LocalDate.parse(valor.trim(), DateTimeFormatter.ofPattern("dd/MM/yyyy"));
            } catch (DateTimeParseException erro) {
                erros.add("dataAquisicao: use DD/MM/AAAA ou AAAA-MM-DD.");
                return null;
            }
        }
    }

    private Integer inteiro(String valor, String campo, List<String> erros) {
        if (valor == null || valor.isBlank()) return null;
        try {
            return new BigDecimal(valor.trim()).intValueExact();
        } catch (NumberFormatException | ArithmeticException erro) {
            erros.add(campo + ": informe um número inteiro.");
            return null;
        }
    }

    private Long resolverOpcao(
            String nome, List<Opcao> opcoes, String campo, List<String> erros) {
        if (nome == null || nome.isBlank()) return null;
        String termo = normalizar(nome);
        List<Opcao> encontradas = opcoes.stream()
                .filter(opcao -> normalizar(opcao.nome()).equals(termo))
                .toList();
        if (encontradas.size() != 1) {
            erros.add(campo + (encontradas.isEmpty()
                    ? ": opção não encontrada (" + nome.trim() + ")."
                    : ": nome ambíguo; informe uma opção única."));
            return null;
        }
        return encontradas.get(0).id();
    }

    private Long resolverOpcaoOpcional(
            String nome, List<Opcao> opcoes, String campo, List<String> erros) {
        return nome == null || nome.isBlank() ? null : resolverOpcao(nome, opcoes, campo, erros);
    }

    private void verificarDuplicados(
            String codigo, String patrimonio, Set<String> codigos, Set<String> patrimonios, List<String> erros) {
        if (codigo != null && !codigo.isBlank() && !codigos.add(chaveUnica(codigo))) {
            erros.add("codigo: duplicado em outra linha desta planilha.");
        }
        if (patrimonio != null && !patrimonio.isBlank() && !patrimonios.add(chaveUnica(patrimonio))) {
            erros.add("patrimonio: duplicado em outra linha desta planilha.");
        }
    }

    private String chaveUnica(String valor) {
        return valor.trim().toUpperCase(Locale.ROOT);
    }

    private String valor(Map<String, String> dados, String chave) {
        if (dados.containsKey(chave)) return dados.get(chave);
        String chaveNormalizada = normalizar(chave);
        return dados.entrySet().stream()
                .filter(entrada -> normalizar(entrada.getKey()).equals(chaveNormalizada))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(null);
    }

    private String normalizar(String valor) {
        return Normalizer.normalize(valor == null ? "" : valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^A-Za-z0-9]", "")
                .toLowerCase(Locale.ROOT);
    }

    private void validarTipo(String tipo) {
        if (!BENS.equals(tipo) && !OUTROS_ITENS.equals(tipo)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tipo de importação inválido.");
        }
    }

    private record Opcao(Long id, String nome) {}

    private record LinhaPreparada(
            int numeroLinha,
            Map<String, String> dados,
            List<String> erros,
            ItemRequestDTO bem,
            OutroItemRequestDTO outroItem) {}
}
