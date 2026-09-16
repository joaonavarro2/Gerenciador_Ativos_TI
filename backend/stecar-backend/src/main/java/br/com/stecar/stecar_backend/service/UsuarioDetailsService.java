package br.com.stecar.stecar_backend.service;

import br.com.stecar.stecar_backend.entity.Usuario;
import br.com.stecar.stecar_backend.repository.UsuarioRepository;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "Usuário não encontrado."
                        )
                );

        String autoridade = converterFuncaoParaAutoridade(
                usuario.getFuncao()
        );

        return User.builder()
                .username(usuario.getEmail())
                .password(usuario.getSenha())
                .authorities(List.of(
                        new SimpleGrantedAuthority(autoridade)
                ))
                .disabled(!"ATIVO".equals(usuario.getStatus()))
                .build();
    }

    /**
     * Converte a função cadastrada no banco
     * para o formato utilizado pelo Spring Security.
     */
    private String converterFuncaoParaAutoridade(String funcao) {

        if ("ADMINISTRADOR GERAL".equals(funcao)) {
            return "ADMINISTRADOR_GERAL";
        }

        if ("ADMINISTRADOR".equals(funcao)) {
            return "ADMINISTRADOR";
        }

        throw new IllegalArgumentException(
                "Função de usuário inválida: " + funcao
        );
    }
}