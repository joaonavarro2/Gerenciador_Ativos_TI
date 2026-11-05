package br.com.stecar.stecar_backend.service;

import br.com.stecar.stecar_backend.dto.UsuarioRequestDTO;
import br.com.stecar.stecar_backend.dto.UsuarioResponseDTO;
import br.com.stecar.stecar_backend.entity.Usuario;
import br.com.stecar.stecar_backend.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Locale;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

@Service
public class UsuarioService {

    private static final Set<String> STATUS_VALIDOS = Set.of("ATIVO", "INATIVO");

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Lista todos os usuários
    public List<UsuarioResponseDTO> listarTodos() {

        return usuarioRepository.findAll()
                .stream()
                .map(this::converterParaResponseDTO)
                .toList();
    }

    // Busca um usuário pelo ID
    public UsuarioResponseDTO buscarPorId(Long id) {

        Usuario usuario = buscarEntidadePorId(id);

        return converterParaResponseDTO(usuario);
    }

    // Cadastra um novo usuário
    public UsuarioResponseDTO cadastrar(UsuarioRequestDTO dados) {
        return cadastrar(dados.getNomeCompleto(), dados.getEmail(), dados.getSenha(), dados.getFuncao());
    }

    public UsuarioResponseDTO cadastrarPublico(
            br.com.stecar.stecar_backend.dto.UsuarioCadastroRequestDTO dados) {
        return cadastrar(dados.getNomeCompleto(), dados.getEmail(), dados.getSenha(), "USUARIO");
    }

    private UsuarioResponseDTO cadastrar(String nomeCompleto, String email, String senha, String funcao) {
        email = normalizarEmail(email);

        // Verifica se o e-mail já está cadastrado
        if (usuarioRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Já existe um usuário cadastrado com este e-mail.");
        }

        Usuario usuario = new Usuario();

        usuario.setNomeCompleto(nomeCompleto);
        usuario.setEmail(email);

        // A senha nunca é salva em texto puro
        usuario.setSenha(
                passwordEncoder.encode(senha)
        );

        usuario.setFuncao(funcao);

        // Novo usuário começa ativo
        usuario.setStatus("ATIVO");

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        return converterParaResponseDTO(usuarioSalvo);
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    // Atualiza os dados de um usuário
    public UsuarioResponseDTO atualizar(
            Long id,
            UsuarioRequestDTO dados) {

        Usuario usuario = buscarEntidadePorId(id);

        // Verifica se o novo e-mail pertence a outro usuário
        String email = normalizarEmail(dados.getEmail());

        usuarioRepository.findByEmail(email)
                .ifPresent(usuarioEncontrado -> {

                    if (!usuarioEncontrado.getId().equals(id)) {
                        throw new ResponseStatusException(HttpStatus.CONFLICT,
                                "Já existe outro usuário com este e-mail.");
                    }
                });

        usuario.setNomeCompleto(dados.getNomeCompleto());
        usuario.setEmail(email);
        usuario.setFuncao(dados.getFuncao());

        /*
         * A senha não é alterada aqui.
         * Ela terá uma operação específica posteriormente.
         */

        Usuario usuarioAtualizado = usuarioRepository.save(usuario);

        return converterParaResponseDTO(usuarioAtualizado);
    }

    // Altera somente o status do usuário
    public UsuarioResponseDTO alterarStatus(
            Long id,
            String status) {

        String statusNormalizado = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        if (!STATUS_VALIDOS.contains(statusNormalizado)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Status inválido. Use ATIVO ou INATIVO.");
        }

        Usuario usuario = buscarEntidadePorId(id);

        usuario.setStatus(statusNormalizado);

        Usuario usuarioAtualizado = usuarioRepository.save(usuario);

        return converterParaResponseDTO(usuarioAtualizado);
    }

    // Exclui um usuário
    public void excluir(Long id) {

        Usuario usuario = buscarEntidadePorId(id);

        usuarioRepository.delete(usuario);
    }

    // Busca a entidade diretamente no banco
    private Usuario buscarEntidadePorId(Long id) {

        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado."));
    }

    // Converte Entity para ResponseDTO
    private UsuarioResponseDTO converterParaResponseDTO(
            Usuario usuario) {

        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNomeCompleto(),
                usuario.getEmail(),
                usuario.getFuncao(),
                usuario.getStatus()
        );
    }
}
