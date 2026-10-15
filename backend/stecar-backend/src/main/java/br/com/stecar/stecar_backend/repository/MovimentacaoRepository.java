
package br.com.stecar.stecar_backend.repository;

import br.com.stecar.stecar_backend.entity.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MovimentacaoRepository
        extends JpaRepository<Movimentacao, Long> {

    // Busca as movimentações mais recentes primeiro.
    List<Movimentacao> findAllByOrderByDataDesc();

    // Busca movimentações de um determinado item.
    List<Movimentacao> findByItemIdOrderByDataDesc(Long itemId);

    // Busca movimentações por status.
    List<Movimentacao> findByStatusIgnoreCaseOrderByDataDesc(
            String status
    );

    // Busca movimentações por tipo.
    List<Movimentacao> findByTipoIgnoreCaseOrderByDataDesc(
            String tipo
    );

    // Conta quantas movimentações existem com determinado status.
    long countByStatusIgnoreCase(String status);
}