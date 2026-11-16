package br.com.stecar.stecar_backend.repository;

import br.com.stecar.stecar_backend.entity.TokenRecuperacaoSenha;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

public interface TokenRecuperacaoSenhaRepository extends JpaRepository<TokenRecuperacaoSenha, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<TokenRecuperacaoSenha> findByTokenAndUsadoFalse(String token);
    java.util.List<TokenRecuperacaoSenha> findByUsuarioIdAndUsadoFalse(Long usuarioId);
    void deleteAllByUsuario(br.com.stecar.stecar_backend.entity.Usuario usuario);
}