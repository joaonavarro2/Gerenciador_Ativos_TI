package br.com.stecar.stecar_backend.repository;

import br.com.stecar.stecar_backend.entity.OutroItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OutroItemRepository extends JpaRepository<OutroItem, Long> {
    List<OutroItem> findAllByOrderByIdDesc();
    boolean existsByCodigoIgnoreCase(String codigo);
    boolean existsByCodigoIgnoreCaseAndIdNot(String codigo, Long id);

    @Query(value = """
            SELECT EXISTS (
                SELECT 1 FROM outro_item
                WHERE patrimonio IS NOT NULL
                  AND UPPER(BTRIM(patrimonio)) = UPPER(BTRIM(:patrimonio))
                  AND (:itemId IS NULL OR id <> :itemId)
            )
            """, nativeQuery = true)
    boolean existePatrimonio(
            @Param("patrimonio") String patrimonio,
            @Param("itemId") Long itemId);
}
