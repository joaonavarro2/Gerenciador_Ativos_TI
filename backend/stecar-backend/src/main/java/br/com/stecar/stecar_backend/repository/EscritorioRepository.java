package br.com.stecar.stecar_backend.repository;

import br.com.stecar.stecar_backend.entity.Escritorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EscritorioRepository extends JpaRepository<Escritorio, Long> {
    List<Escritorio> findAllByOrderByNomeAsc();
    boolean existsByNomeIgnoreCase(String nome);
    boolean existsByNomeIgnoreCaseAndIdNot(String nome, Long id);

    @Query(value = """
            SELECT (SELECT COUNT(*) FROM item WHERE escritorio_id = :id AND excluido = FALSE)
                 + (SELECT COUNT(*) FROM pessoa WHERE escritorio_id = :id)
                 + (SELECT COUNT(*) FROM outro_item WHERE escritorio_id = :id)
                 + (SELECT COUNT(*) FROM movimentacao WHERE escritorio_origem_id = :id OR escritorio_destino_id = :id)
            """, nativeQuery = true)
    long contarReferencias(@Param("id") Long id);
}
