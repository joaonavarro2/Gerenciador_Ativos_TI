package br.com.stecar.stecar_backend.config;

import br.com.stecar.stecar_backend.entity.Usuario;
import br.com.stecar.stecar_backend.repository.UsuarioRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner criarAdministradorInicial(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            String email = "joaonavarro@car.ba.gov.br";

            // Verifica se o administrador já existe
            if (usuarioRepository.existsByEmail(email)) {
                return;
            }

            String senha = System.getenv("STECAR_ADMIN_PASSWORD");

            // Impede a criação caso nenhuma senha tenha sido informada
            if (senha == null || senha.isBlank()) {
                System.out.println(
                        "Administrador inicial não criado: " +
                        "STECAR_ADMIN_PASSWORD não foi definida."
                );
                return;
            }

            Usuario usuario = new Usuario();

            usuario.setNomeCompleto("João Navarro");
            usuario.setEmail(email);

            // A senha é transformada em hash antes de ir para o banco
            usuario.setSenha(
                    passwordEncoder.encode(senha)
            );

            usuario.setFuncao("Estagiario");
            usuario.setStatus("ATIVO");

            usuarioRepository.save(usuario);

            System.out.println(
                    "Administrador inicial criado com sucesso."
            );
        };
    }
}