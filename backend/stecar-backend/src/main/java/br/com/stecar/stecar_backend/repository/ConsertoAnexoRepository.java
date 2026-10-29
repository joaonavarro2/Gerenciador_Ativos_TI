package br.com.stecar.stecar_backend.repository;

import br.com.stecar.stecar_backend.entity.ConsertoAnexo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConsertoAnexoRepository extends JpaRepository<ConsertoAnexo, Long> {
    List<ConsertoAnexo> findByConsertoIdOrderByDataUploadDesc(Long consertoId);
}
