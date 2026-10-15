package br.com.stecar.stecar_backend.repository;

import br.com.stecar.stecar_backend.entity.TokenRecuperacaoSenha;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRecuperacaoSenhaRepository extends JpaRepository<TokenRecuperacaoSenha, Long> {
    Optional<TokenRecuperacaoSenha> findByTokenAndUsadoFalse(String token);
    java.util.List<TokenRecuperacaoSenha> findByUsuarioIdAndUsadoFalse(Long usuarioId);
}