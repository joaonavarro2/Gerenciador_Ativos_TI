package br.com.stecar.stecar_backend.service;

import br.com.stecar.stecar_backend.dto.HistoricoInstituicaoResponseDTO;
import br.com.stecar.stecar_backend.entity.HistoricoInstituicao;
import br.com.stecar.stecar_backend.entity.InstituicaoPatrimonio;
import br.com.stecar.stecar_backend.repository.HistoricoInstituicaoRepository;
import br.com.stecar.stecar_backend.repository.UsuarioRepository;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class HistoricoInstituicaoService {
    private final HistoricoInstituicaoRepository historicoRepository;
    private final UsuarioRepository usuarioRepository;

    public HistoricoInstituicaoService(
            HistoricoInstituicaoRepository historicoRepository,
            UsuarioRepository usuarioRepository) {
        this.historicoRepository = historicoRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional
    public void registrar(
            String tipo,
            Long entidadeId,
            InstituicaoPatrimonio anterior,
            InstituicaoPatrimonio nova,
            String emailUsuario) {
        if (anterior == nova) return;

        HistoricoInstituicao registro = new HistoricoInstituicao();
        registro.setTipoEntidade(tipo);
        registro.setEntidadeId(entidadeId);
        registro.setInstituicaoAnterior(anterior == null ? null : anterior.name());
        registro.setInstituicaoNova(nova.name());
        registro.setAlteradoEm(LocalDateTime.now());
        if (emailUsuario != null && !emailUsuario.isBlank()) {
            usuarioRepository.findByEmail(emailUsuario).ifPresent(usuario -> {
                registro.setUsuarioId(usuario.getId());
                registro.setUsuarioNome(usuario.getNomeCompleto());
            });
        }
        historicoRepository.save(registro);
    }

    @Transactional(readOnly = true)
    public List<HistoricoInstituicaoResponseDTO> listar(String tipo, Long entidadeId) {
        return historicoRepository.listarPorEntidade(tipo, entidadeId).stream()
                .map(this::mapear)
                .toList();
    }

    private HistoricoInstituicaoResponseDTO mapear(Object[] linha) {
        Object data = linha[3];
        LocalDateTime alteradoEm = data instanceof LocalDateTime localDateTime
                ? localDateTime
                : data instanceof Timestamp timestamp ? timestamp.toLocalDateTime() : null;
        return new HistoricoInstituicaoResponseDTO(
                ((Number) linha[0]).longValue(),
                (String) linha[1],
                (String) linha[2],
                alteradoEm,
                (String) linha[4]);
    }
}
