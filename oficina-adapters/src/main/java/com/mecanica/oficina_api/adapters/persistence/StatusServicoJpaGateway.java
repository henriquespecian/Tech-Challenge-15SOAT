package com.mecanica.oficina_api.adapters.persistence;

import com.mecanica.oficina_api.adapters.persistence.repository.StatusServicoSpringDataRepository;
import com.mecanica.oficina_api.application.servico.gateway.StatusServicoGateway;
import com.mecanica.oficina_api.domain.ordemservico.ServicoStatus;
import com.mecanica.oficina_api.domain.ordemservico.StatusServico;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Repository
public class StatusServicoJpaGateway implements StatusServicoGateway {

    private final StatusServicoSpringDataRepository repository;

    public StatusServicoJpaGateway(StatusServicoSpringDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public double calcularTempoMedioMinutos(String servicoId) {
        return repository.findByServicoIdAndStatus(servicoId, ServicoStatus.FINALIZADO.toString())
            .stream()
            .filter(e -> e.getDataInicio() != null && e.getDataFim() != null)
            .mapToLong(e -> Duration.between(e.getDataInicio(), e.getDataFim()).toMinutes())
            .average()
            .orElse(0.0);
    }

    @Override
    public List<StatusServico> salvarLista(List<StatusServico> listaStatusServico) {
        List<StatusServicoJpaEntity> entities = listaStatusServico.stream().map(this::toEntity).toList();
        return repository.saveAll(entities).stream().map(this::toDomain).toList();
    }

    @Override
    public List<StatusServico> listarServicosPorOS(String ordemServicoId) {
        return repository.findByOrdemServicoId(ordemServicoId).stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<StatusServico> buscarPorIdEStatus(String id, ServicoStatus status) {
        return repository.findByIdAndStatus(id, status.name()).map(this::toDomain);
    }

    @Override
    public StatusServico atualizar(StatusServico statusServico) {
        return toDomain(repository.save(toEntity(statusServico)));
    }

    // --- mapeamento ---

    private StatusServico toDomain(StatusServicoJpaEntity e) {
        return StatusServico.recriar(
            e.getId(),
            ServicoStatus.valueOf(e.getStatus()),
            e.getOrdemServicoId(),
            e.getServicoId(),
            e.getDataInicio(),
            e.getDataFim());
    }

    private StatusServicoJpaEntity toEntity(StatusServico s) {
        StatusServicoJpaEntity e = new StatusServicoJpaEntity();
        e.setId(s.getId());
        e.setStatus(s.getStatus().name());
        e.setOrdemServicoId(s.getOrdemServicoId());
        e.setServicoId(s.getServicoId());
        e.setDataInicio(s.getDataInicio());
        e.setDataFim(s.getDataFim());
        return e;
    }
}
