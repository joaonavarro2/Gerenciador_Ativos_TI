package br.com.stecar.stecar_backend.service;

import br.com.stecar.stecar_backend.dto.UsuarioAtualizarRequestDTO;
import br.com.stecar.stecar_backend.dto.UsuarioRequestDTO;
import br.com.stecar.stecar_backend.dto.UsuarioResponseDTO;
import br.com.stecar.stecar_backend.entity.Usuario;
import br.com.stecar.stecar_backend.repository.UsuarioRepository;
import br.com.stecar.stecar_backend.repository.PessoaRepository;
import br.com.stecar.stecar_backend.repository.ConsertoRepository;
import br.com.stecar.stecar_backend.repository.MovimentacaoRepository;
import br.com.stecar.stecar_backend.repository.TokenRecuperacaoSenhaRepository;
import java.util.List;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UsuarioService {
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final PessoaRepository pessoaRepository;
    private final MovimentacaoRepository movimentacaoRepository;
    private final ConsertoRepository consertoRepository;
    private final TokenRecuperacaoSenhaRepository tokenRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
            PessoaRepository pessoaRepository, MovimentacaoRepository movimentacaoRepository,
            ConsertoRepository consertoRepository, TokenRecuperacaoSenhaRepository tokenRepository) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.pessoaRepository = pessoaRepository;
        this.movimentacaoRepository = movimentacaoRepository;
        this.consertoRepository = consertoRepository;
        this.tokenRepository = tokenRepository;
    }

    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream().map(this::converterParaResponseDTO).toList();
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO buscarPorId(Long id) {
        return converterParaResponseDTO(buscarEntidadePorId(id));
    }

    @Transactional
    public UsuarioResponseDTO cadastrar(UsuarioRequestDTO dados) {
        String email = normalizarEmail(dados.getEmail());
        if (usuarioRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe um usuário cadastrado com este e-mail.");
        }
        validarFuncao(dados.getFuncao());
        Usuario usuario = new Usuario();
        usuario.setNomeCompleto(dados.getNomeCompleto().trim());
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(dados.getSenha()));
        usuario.setFuncao(dados.getFuncao());
        usuario.setStatus(dados.getStatus() == null ? "ATIVO" : dados.getStatus());
        return converterParaResponseDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponseDTO atualizar(Long id, UsuarioAtualizarRequestDTO dados, String emailSolicitante) {
        Usuario usuario = buscarEntidadePorId(id);
        String novaFuncao = validarFuncao(dados.funcao());
        if (usuario.getEmail().equalsIgnoreCase(emailSolicitante)
                && !usuario.getFuncao().equals(novaFuncao)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Não é permitido alterar a própria função.");
        }
        String email = normalizarEmail(dados.email());
        usuarioRepository.findByEmail(email).ifPresent(outro -> {
            if (!outro.getId().equals(id)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe outro usuário com este e-mail.");
            }
        });

        if ("ADMINISTRADOR GERAL".equals(usuario.getFuncao())
                && "ATIVO".equals(usuario.getStatus())
                && !"ADMINISTRADOR GERAL".equals(novaFuncao)) {
            garantirOutroAdministradorGeralAtivo(usuario.getId());
        }
        boolean alterouAutorizacao = !usuario.getFuncao().equals(novaFuncao)
                || !usuario.getEmail().equals(email);
        usuario.setNomeCompleto(dados.nomeCompleto().trim());
        usuario.setEmail(email);
        usuario.setFuncao(novaFuncao);
        if (alterouAutorizacao) usuario.marcarAlteracaoAutenticacao();
        return converterParaResponseDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public UsuarioResponseDTO alterarStatus(Long id, String status, String emailSolicitante) {
        String statusNormalizado = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        if (!"ATIVO".equals(statusNormalizado) && !"INATIVO".equals(statusNormalizado)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status inválido. Use ATIVO ou INATIVO.");
        }
        Usuario usuario = buscarEntidadePorId(id);
        if (usuario.getEmail().equalsIgnoreCase(emailSolicitante) && "INATIVO".equals(statusNormalizado)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Não é permitido desativar a própria conta.");
        }
        boolean mudouStatus = !usuario.getStatus().equals(statusNormalizado);
        if ("ATIVO".equals(usuario.getStatus()) && "INATIVO".equals(statusNormalizado)
                && "ADMINISTRADOR GERAL".equals(usuario.getFuncao())) {
            garantirOutroAdministradorGeralAtivo(usuario.getId());
        }
        usuario.setStatus(statusNormalizado);
        if (mudouStatus) usuario.marcarAlteracaoAutenticacao();
        return converterParaResponseDTO(usuarioRepository.save(usuario));
    }

    @Transactional
    public void excluir(Long id, String emailSolicitante) {
        Usuario usuario = buscarEntidadePorId(id);
        if (usuario.getEmail().equalsIgnoreCase(emailSolicitante)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Não é permitido excluir a própria conta.");
        }
        if (movimentacaoRepository.countByUsuarioId(id) > 0 || consertoRepository.countByUsuarioId(id) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Este usuário possui históricos patrimoniais. Desative a conta para preservar os registros.");
        }
        if ("ADMINISTRADOR GERAL".equals(usuario.getFuncao()) && "ATIVO".equals(usuario.getStatus())) {
            garantirOutroAdministradorGeralAtivo(usuario.getId());
        }
        // A pessoa e seus bens permanecem. Apenas removemos a referência opcional
        // antes de apagar a conta, sem apagar a pessoa nem os históricos patrimoniais.
        pessoaRepository.desvincularUsuario(usuario.getId());
        tokenRepository.deleteAllByUsuario(usuario);
        usuarioRepository.delete(usuario);
    }

    private void garantirOutroAdministradorGeralAtivo(Long idAlvo) {
        // Bloqueia a linha de todos os administradores gerais ativos em ordem fixa.
        // Operações concorrentes de desativação, rebaixamento e exclusão ficam serializadas.
        List<Usuario> ativos = usuarioRepository.bloquearAdministradoresGeraisAtivos();
        long outros = ativos.stream().filter(u -> !u.getId().equals(idAlvo)).count();
        if (outros < 1) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "O sistema precisa manter ao menos um administrador geral ativo.");
        }
    }

    private String validarFuncao(String funcao) {
        if (funcao == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Função inválida.");
        }
        String normalizada = funcao.trim().toUpperCase(Locale.ROOT);
        if (!"ADMINISTRADOR GERAL".equals(normalizada) && !"ADMINISTRADOR".equals(normalizada)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Função inválida.");
        }
        return normalizada;
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private Usuario buscarEntidadePorId(Long id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));
    }

    private UsuarioResponseDTO converterParaResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(usuario.getId(), usuario.getNomeCompleto(), usuario.getEmail(),
                usuario.getFuncao(), usuario.getStatus());
    }
}
