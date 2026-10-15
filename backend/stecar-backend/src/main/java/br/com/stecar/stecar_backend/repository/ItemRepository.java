package br.com.stecar.stecar_backend.repository;

import br.com.stecar.stecar_backend.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

        boolean existsByCodigoAndExcluidoFalse(String codigo);

        boolean existsByCodigoAndIdNotAndExcluidoFalse(String codigo, Long id);

    @Query(value = """
            SELECT i.id, i.codigo, i.patrimonio, i.nome, i.categoria, i.serial,
                   i.fabricante, i.modelo, i.descricao, i.status, i.data_aquisicao,
                   e.id, e.nome, d.id, d.nome, p.id, p.nome_completo
            FROM item i
            JOIN escritorio e ON e.id = i.escritorio_id
            JOIN departamento d ON d.id = i.departamento_id
            LEFT JOIN pessoa p ON p.id = i.pessoa_id
            WHERE i.excluido = FALSE
            ORDER BY i.id DESC
            """, nativeQuery = true)
    List<Object[]> listarComReferencias();

    @Query(value = """
            SELECT i.id, i.codigo, i.patrimonio, i.nome, i.categoria, i.serial,
                   i.fabricante, i.modelo, i.descricao, i.status, i.data_aquisicao,
                   e.id, e.nome, d.id, d.nome, p.id, p.nome_completo
            FROM item i
            JOIN escritorio e ON e.id = i.escritorio_id
            JOIN departamento d ON d.id = i.departamento_id
            LEFT JOIN pessoa p ON p.id = i.pessoa_id
            WHERE i.id = :id AND i.excluido = FALSE
            """, nativeQuery = true)
    List<Object[]> buscarComReferencias(@Param("id") Long id);

    @Query(value = """
             SELECT m.id, m.tipo, m.data,
                     CONCAT(COALESCE(eo.nome, ''), ' / ', COALESCE(dep_orig.nome, ''),
                             CASE WHEN po.nome_completo IS NULL THEN '' ELSE CONCAT(' / ', po.nome_completo) END),
                     CONCAT(COALESCE(ed.nome, ''), ' / ', COALESCE(dep_dest.nome, ''),
                             CASE WHEN pd.nome_completo IS NULL THEN '' ELSE CONCAT(' / ', pd.nome_completo) END),
                     u.nome_completo
             FROM movimentacao m
             JOIN usuario u ON u.id = m.usuario_id
             LEFT JOIN escritorio eo ON eo.id = m.escritorio_origem_id
             LEFT JOIN departamento dep_orig ON dep_orig.id = m.departamento_origem_id
             LEFT JOIN pessoa po ON po.id = m.pessoa_origem_id
             LEFT JOIN escritorio ed ON ed.id = m.escritorio_destino_id
             LEFT JOIN departamento dep_dest ON dep_dest.id = m.departamento_destino_id
             LEFT JOIN pessoa pd ON pd.id = m.pessoa_destino_id
             WHERE m.item_id = :itemId
             ORDER BY m.data DESC, m.id DESC
             """, nativeQuery = true)
    List<Object[]> listarMovimentacoes(@Param("itemId") Long itemId);

    @Query(value = """
             SELECT c.id, c.tipo, c.data_entrada, u.nome_completo,
                     COALESCE(c.descricao_solucao, c.descricao_problema)
             FROM conserto c
             JOIN usuario u ON u.id = c.usuario_id
             WHERE c.item_id = :itemId
             ORDER BY c.data_entrada DESC, c.id DESC
             """, nativeQuery = true)
    List<Object[]> listarConsertos(@Param("itemId") Long itemId);

    @Query(value = "SELECT id, nome FROM escritorio ORDER BY nome", nativeQuery = true)
    List<Object[]> listarEscritorios();

    @Query(value = "SELECT id, nome FROM departamento ORDER BY nome", nativeQuery = true)
    List<Object[]> listarDepartamentos();

        @Query(value = "SELECT DISTINCT categoria FROM item WHERE excluido = FALSE ORDER BY categoria", nativeQuery = true)
        List<String> listarCategorias();

        @Query(value = "SELECT DISTINCT status FROM item WHERE excluido = FALSE ORDER BY status", nativeQuery = true)
        List<String> listarStatus();

    @Query(value = """
            SELECT id, nome_completo, departamento_id
            FROM pessoa
            WHERE UPPER(status) = 'ATIVO'
            ORDER BY nome_completo
            """, nativeQuery = true)
    List<Object[]> listarPessoasAtivas();

    @Query(value = "SELECT COUNT(*) FROM escritorio WHERE id = :id", nativeQuery = true)
    long contarEscritorio(@Param("id") Long id);

    @Query(value = "SELECT COUNT(*) FROM departamento WHERE id = :id", nativeQuery = true)
    long contarDepartamento(@Param("id") Long id);

    @Query(value = "SELECT COUNT(*) FROM pessoa WHERE id = :id AND UPPER(status) = 'ATIVO'", nativeQuery = true)
    long contarPessoaAtiva(@Param("id") Long id);

    @Modifying
    @Query(value = """
            INSERT INTO movimentacao (
                tipo, data, status, departamento_origem_id, departamento_destino_id,
                escritorio_origem_id, escritorio_destino_id, pessoa_origem_id,
                pessoa_destino_id, item_id, usuario_id
                                , justificativa
            ) VALUES (
                :tipo, :data, 'CONCLUIDA', :departamentoOrigemId, :departamentoDestinoId,
                :escritorioOrigemId, :escritorioDestinoId, :pessoaOrigemId,
                                :pessoaDestinoId, :itemId, :usuarioId, :justificativa
            )
            """, nativeQuery = true)
    int registrarMovimentacao(
            @Param("tipo") String tipo,
            @Param("data") LocalDateTime data,
            @Param("departamentoOrigemId") Long departamentoOrigemId,
            @Param("departamentoDestinoId") Long departamentoDestinoId,
            @Param("escritorioOrigemId") Long escritorioOrigemId,
            @Param("escritorioDestinoId") Long escritorioDestinoId,
            @Param("pessoaOrigemId") Long pessoaOrigemId,
            @Param("pessoaDestinoId") Long pessoaDestinoId,
            @Param("itemId") Long itemId,
            @Param("usuarioId") Long usuarioId,
            @Param("justificativa") String justificativa);
}