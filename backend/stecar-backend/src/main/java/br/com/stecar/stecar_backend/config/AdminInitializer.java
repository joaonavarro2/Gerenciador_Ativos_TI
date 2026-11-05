package br.com.stecar.stecar_backend.config;

import br.com.stecar.stecar_backend.entity.Usuario;
import br.com.stecar.stecar_backend.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminInitializer {

    private static final Logger log = LoggerFactory.getLogger(AdminInitializer.class);

    @Bean
    CommandLineRunner criarAdministradorInicial(
            UsuarioRepository usuarioRepository,
            PasswordEncoder passwordEncoder,
            @Value("${stecar.admin.email}") String emailConfigurado,
            @Value("${stecar.admin.nome}") String nome,
            @Value("${stecar.admin.senha}") String senha) {

        return args -> {

            String email = emailConfigurado.trim().toLowerCase(java.util.Locale.ROOT);

            // Atualiza a função do administrador criado pelas versões anteriores.
            var administradorExistente = usuarioRepository.findByEmail(email);
            if (administradorExistente.isPresent()) {
                Usuario usuario = administradorExistente.get();
                if ("Estagiario".equals(usuario.getFuncao())) {
                    usuario.setFuncao("ADMINISTRADOR GERAL");
                    usuarioRepository.save(usuario);
                }
                return;
            }

            // Impede a criação caso nenhuma senha tenha sido informada
            if (senha == null || senha.isBlank()) {
                log.warn("Administrador inicial não criado: STECAR_ADMIN_PASSWORD não foi definida.");
                return;
            }

            Usuario usuario = new Usuario();

            usuario.setNomeCompleto(nome);
            usuario.setEmail(email);

            // A senha é transformada em hash antes de ir para o banco
            usuario.setSenha(
                    passwordEncoder.encode(senha)
            );

            usuario.setFuncao("ADMINISTRADOR GERAL");
            usuario.setStatus("ATIVO");

            usuarioRepository.save(usuario);

            log.info("Administrador inicial criado com sucesso.");
        };
    }
}
