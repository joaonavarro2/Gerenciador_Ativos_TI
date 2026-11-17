package br.com.stecar.stecar_backend.repository;

import br.com.stecar.stecar_backend.entity.HistoricoInstituicao;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HistoricoInstituicaoRepository extends JpaRepository<HistoricoInstituicao, Long> {
    @Query(value = """
            SELECT h.id, h.instituicao_anterior, h.instituicao_nova, h.alterado_em, h.usuario_nome
            FROM historico_instituicao h
            WHERE h.tipo_entidade = :tipo AND h.entidade_id = :entidadeId
            ORDER BY h.alterado_em DESC, h.id DESC
            """, nativeQuery = true)
    List<Object[]> listarPorEntidade(
            @Param("tipo") String tipo,
            @Param("entidadeId") Long entidadeId);
}
