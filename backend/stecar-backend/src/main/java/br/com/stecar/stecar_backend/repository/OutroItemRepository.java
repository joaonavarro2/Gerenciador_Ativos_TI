package br.com.stecar.stecar_backend.repository;

import br.com.stecar.stecar_backend.entity.OutroItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutroItemRepository extends JpaRepository<OutroItem, Long> {
    List<OutroItem> findAllByOrderByIdDesc();
    boolean existsByCodigoIgnoreCase(String codigo);
    boolean existsByCodigoIgnoreCaseAndIdNot(String codigo, Long id);
}
