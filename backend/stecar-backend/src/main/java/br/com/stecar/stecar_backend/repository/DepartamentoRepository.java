package br.com.stecar.stecar_backend.repository;

import br.com.stecar.stecar_backend.entity.Departamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DepartamentoRepository extends JpaRepository<Departamento, Long> {
    List<Departamento> findAllByOrderByNomeAsc();
    boolean existsByNomeIgnoreCase(String nome);
    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);

    @Query(value = "SELECT COUNT(*) FROM item WHERE departamento_id = :id", nativeQuery = true)
    long contarBens(@Param("id") Long id);

    @Query(value = """
            SELECT COUNT(*) FROM conserto c JOIN item i ON i.id = c.item_id
            WHERE i.departamento_id = :id
            """, nativeQuery = true)
    long contarConsertos(@Param("id") Long id);

    @Query(value = """
            SELECT (SELECT COUNT(*) FROM item WHERE departamento_id = :id)
                 + (SELECT COUNT(*) FROM pessoa WHERE departamento_id = :id)
                 + (SELECT COUNT(*) FROM movimentacao WHERE departamento_origem_id = :id OR departamento_destino_id = :id)
            """, nativeQuery = true)
    long contarVinculos(@Param("id") Long id);
}
