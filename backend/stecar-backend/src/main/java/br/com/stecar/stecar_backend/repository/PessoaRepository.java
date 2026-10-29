package br.com.stecar.stecar_backend.repository;

import br.com.stecar.stecar_backend.entity.Pessoa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PessoaRepository extends JpaRepository<Pessoa, Long> {
    List<Pessoa> findAllByOrderByNomeCompletoAsc();
    boolean existsByCpf(String cpf);
    boolean existsByCpfAndIdNot(String cpf, Long id);
    boolean existsByMatriculaIgnoreCase(String matricula);
    boolean existsByMatriculaIgnoreCaseAndIdNot(String matricula, Long id);

    @Query(value = "SELECT COUNT(*) FROM item WHERE pessoa_id = :id AND excluido = FALSE", nativeQuery = true)
    long contarBens(@Param("id") Long id);

    @Query(value = """
            SELECT (SELECT COUNT(*) FROM item WHERE pessoa_id = :id)
                 + (SELECT COUNT(*) FROM movimentacao WHERE pessoa_origem_id = :id OR pessoa_destino_id = :id)
                 + (SELECT COUNT(*) FROM escritorio WHERE gestor_id = :id)
            """, nativeQuery = true)
    long contarVinculos(@Param("id") Long id);
}
