
package br.com.stecar.stecar_backend.repository;

import br.com.stecar.stecar_backend.entity.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



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

    
    // Busca as movimentações com os nomes dos registros relacionados.
    @Query(value = """
        SELECT
            m.id,
            m.tipo,
            m.data,
            m.status,
            m.justificativa,
            m.item_id,
            i.patrimonio,
            i.nome,
            m.departamento_origem_id,
            dep_orig.nome,
            m.departamento_destino_id,
            dep_dest.nome,
            m.escritorio_origem_id,
            eo.nome,
            m.escritorio_destino_id,
            ed.nome,
            m.pessoa_origem_id,
            po.nome_completo,
            m.pessoa_destino_id,
            pd.nome_completo,
            m.usuario_id,
            u.nome_completo
        FROM movimentacao m
        LEFT JOIN item i
            ON i.id = m.item_id
        LEFT JOIN departamento dep_orig
            ON dep_orig.id = m.departamento_origem_id
        LEFT JOIN departamento dep_dest
            ON dep_dest.id = m.departamento_destino_id
        LEFT JOIN escritorio eo
            ON eo.id = m.escritorio_origem_id
        LEFT JOIN escritorio ed
            ON ed.id = m.escritorio_destino_id
        LEFT JOIN pessoa po
            ON po.id = m.pessoa_origem_id
        LEFT JOIN pessoa pd
            ON pd.id = m.pessoa_destino_id
        LEFT JOIN usuario u
            ON u.id = m.usuario_id
        ORDER BY m.data DESC, m.id DESC
        """, nativeQuery = true)
    List<Object[]> listarTodasComReferencias();


    // Busca uma movimentação específica com os nomes relacionados.
    @Query(value = """
        SELECT
            m.id,
            m.tipo,
            m.data,
            m.status,
            m.justificativa,
            m.item_id,
            i.patrimonio,
            i.nome,
            m.departamento_origem_id,
            dep_orig.nome,
            m.departamento_destino_id,
            dep_dest.nome,
            m.escritorio_origem_id,
            eo.nome,
            m.escritorio_destino_id,
            ed.nome,
            m.pessoa_origem_id,
            po.nome_completo,
            m.pessoa_destino_id,
            pd.nome_completo,
            m.usuario_id,
            u.nome_completo
        FROM movimentacao m
        LEFT JOIN item i
            ON i.id = m.item_id
        LEFT JOIN departamento dep_orig
            ON dep_orig.id = m.departamento_origem_id
        LEFT JOIN departamento dep_dest
            ON dep_dest.id = m.departamento_destino_id
        LEFT JOIN escritorio eo
            ON eo.id = m.escritorio_origem_id
        LEFT JOIN escritorio ed
            ON ed.id = m.escritorio_destino_id
        LEFT JOIN pessoa po
            ON po.id = m.pessoa_origem_id
        LEFT JOIN pessoa pd
            ON pd.id = m.pessoa_destino_id
        LEFT JOIN usuario u
            ON u.id = m.usuario_id
        WHERE m.id = :id
        """, nativeQuery = true)
    List<Object[]> buscarComReferencias(@Param("id") Long id);

}