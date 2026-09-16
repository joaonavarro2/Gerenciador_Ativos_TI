package br.com.stecar.stecar_backend.service;

import br.com.stecar.stecar_backend.dto.UsuarioRequestDTO;
import br.com.stecar.stecar_backend.dto.UsuarioResponseDTO;
import br.com.stecar.stecar_backend.entity.Usuario;
import br.com.stecar.stecar_backend.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

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

        // Verifica se o e-mail já está cadastrado
        if (usuarioRepository.existsByEmail(dados.getEmail())) {
            throw new RuntimeException(
                    "Já existe um usuário cadastrado com este e-mail."
            );
        }

        Usuario usuario = new Usuario();

        usuario.setNomeCompleto(dados.getNomeCompleto());
        usuario.setEmail(dados.getEmail());

        // A senha nunca é salva em texto puro
        usuario.setSenha(
                passwordEncoder.encode(dados.getSenha())
        );

        usuario.setFuncao(dados.getFuncao());

        // Novo usuário começa ativo
        usuario.setStatus("ATIVO");

        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        return converterParaResponseDTO(usuarioSalvo);
    }

    // Atualiza os dados de um usuário
    public UsuarioResponseDTO atualizar(
            Long id,
            UsuarioRequestDTO dados) {

        Usuario usuario = buscarEntidadePorId(id);

        // Verifica se o novo e-mail pertence a outro usuário
        usuarioRepository.findByEmail(dados.getEmail())
                .ifPresent(usuarioEncontrado -> {

                    if (!usuarioEncontrado.getId().equals(id)) {
                        throw new RuntimeException(
                                "Já existe outro usuário com este e-mail."
                        );
                    }
                });

        usuario.setNomeCompleto(dados.getNomeCompleto());
        usuario.setEmail(dados.getEmail());
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

        Usuario usuario = buscarEntidadePorId(id);

        usuario.setStatus(status);

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
                .orElseThrow(() ->
                        new RuntimeException("Usuário não encontrado.")
                );
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