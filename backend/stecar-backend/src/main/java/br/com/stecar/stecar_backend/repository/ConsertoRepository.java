package br.com.stecar.stecar_backend.repository;

import br.com.stecar.stecar_backend.entity.Conserto;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ConsertoRepository extends JpaRepository<Conserto, Long> {
    @Query(value = """
            SELECT c.id, c.tipo, c.descricao_problema, c.descricao_solucao,
                   c.data_entrada, c.data_saida, c.status, c.tecnico,
                   c.item_id, i.codigo, i.patrimonio, i.nome,
                   i.escritorio_id, e.nome, i.departamento_id, d.nome,
                   c.usuario_id, u.nome_completo
            FROM conserto c
            JOIN item i ON i.id = c.item_id
            JOIN escritorio e ON e.id = i.escritorio_id
            JOIN departamento d ON d.id = i.departamento_id
            JOIN usuario u ON u.id = c.usuario_id
            WHERE i.excluido = FALSE
            ORDER BY c.data_entrada DESC, c.id DESC
            """, nativeQuery = true)
    List<Object[]> listarComReferencias();

    @Query(value = """
            SELECT c.id, c.tipo, c.descricao_problema, c.descricao_solucao,
                   c.data_entrada, c.data_saida, c.status, c.tecnico,
                   c.item_id, i.codigo, i.patrimonio, i.nome,
                   i.escritorio_id, e.nome, i.departamento_id, d.nome,
                   c.usuario_id, u.nome_completo
            FROM conserto c
            JOIN item i ON i.id = c.item_id
            JOIN escritorio e ON e.id = i.escritorio_id
            JOIN departamento d ON d.id = i.departamento_id
            JOIN usuario u ON u.id = c.usuario_id
            WHERE c.id = :id AND i.excluido = FALSE
            """, nativeQuery = true)
    List<Object[]> buscarComReferencias(@Param("id") Long id);
}
