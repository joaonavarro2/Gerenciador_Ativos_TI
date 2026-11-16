package br.com.stecar.stecar_backend.repository;

import br.com.stecar.stecar_backend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import jakarta.persistence.LockModeType;

import java.util.Optional;
import java.util.List;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    List<Usuario> findByStatusIgnoreCaseOrderByNomeCompletoAsc(String status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from Usuario u where u.funcao = 'ADMINISTRADOR GERAL' and u.status = 'ATIVO' order by u.id")
    List<Usuario> bloquearAdministradoresGeraisAtivos();
}
