package br.com.stecar.stecar_backend.repository;

import br.com.stecar.stecar_backend.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DashboardRepository extends JpaRepository<Item, Long>  {

    /*
     * ============================================================
     * RESUMO DO DASHBOARD
     * ============================================================
     */

    // Total de bens cadastrados.
    @Query("""
        SELECT COUNT(i)
        FROM Item i
        WHERE i.excluido = false
    """)
    long contarTotalBens();

    /*
     * Quantidade de movimentações com determinado status.
     */
    @Query(value = """
        SELECT COUNT(*)
        FROM movimentacao
        WHERE status = :status
    """, nativeQuery = true)
    long contarMovimentacoesPorStatus(@Param("status") String status);

    /*
     * Quantidade de consertos com determinado status.
     */
    @Query(value = """
        SELECT COUNT(*)
        FROM conserto
        WHERE status = :status
    """, nativeQuery = true)
    long contarConsertosPorStatus(@Param("status") String status);

    /*
     * Quantidade de itens com determinado status.
     */
    @Query("""
        SELECT COUNT(i)
        FROM Item i
        WHERE i.status = :status AND i.excluido = false
    """)
    long contarItensPorStatus(@Param("status") String status);


    /*
     * ============================================================
     * ATIVIDADE MENSAL
     * ============================================================
     */

    /*
     * Retorna a quantidade de movimentações por mês
     * para o ano informado.
     *
     * O PostgreSQL gera os 12 meses para que o gráfico
     * também mostre meses que não tiveram movimentações.
     */
    @Query(value = """
        SELECT
            EXTRACT(MONTH FROM data)::INTEGER AS mes,
            COUNT(*) AS quantidade
        FROM movimentacao
        WHERE EXTRACT(YEAR FROM data) = :ano
        GROUP BY EXTRACT(MONTH FROM data)
        ORDER BY mes
    """, nativeQuery = true)
    List<Object[]> contarMovimentacoesPorMes(@Param("ano") int ano);

    /*
     * Retorna a quantidade de consertos por mês
     * para o ano informado.
     */
    @Query(value = """
        SELECT
            EXTRACT(MONTH FROM data_entrada)::INTEGER AS mes,
            COUNT(*) AS quantidade
        FROM conserto
        WHERE EXTRACT(YEAR FROM data_entrada) = :ano
        GROUP BY EXTRACT(MONTH FROM data_entrada)
        ORDER BY mes
    """, nativeQuery = true)
    List<Object[]> contarConsertosPorMes(@Param("ano") int ano);


    /*
     * ============================================================
     * DISTRIBUIÇÃO POR CATEGORIA
     * ============================================================
     */

    /*
     * Retorna a quantidade de bens por categoria.
     *
     * O percentual será calculado posteriormente no Service.
     */
    @Query(value = """
        SELECT
            categoria,
            COUNT(*) AS quantidade
        FROM item
        WHERE excluido = FALSE
        GROUP BY categoria
        ORDER BY quantidade DESC
    """, nativeQuery = true)
    List<Object[]> contarItensPorCategoria();


    /*
     * ============================================================
     * REGISTRO DE BENS
     * ============================================================
     */

    /*
    * Busca os cinco bens adquiridos mais recentemente para o Dashboard.
     *
     * Os dados de escritório e departamento são obtidos
     * através dos relacionamentos existentes no banco.
     */
    @Query(value = """
        SELECT
            i.id,
            i.nome,
            e.nome AS escritorio,
            d.nome AS departamento,
            i.data_aquisicao,
            i.status
        FROM item i
        INNER JOIN escritorio e
            ON e.id = i.escritorio_id
        INNER JOIN departamento d
            ON d.id = i.departamento_id
        WHERE i.excluido = FALSE
        ORDER BY i.data_aquisicao DESC, i.id DESC
        LIMIT 5
    """, nativeQuery = true)
    List<Object[]> buscarBensDashboard();
}