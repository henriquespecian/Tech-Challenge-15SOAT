package com.mecanica.oficina_api.adapters.persistence;

import com.mecanica.oficina_api.adapters.persistence.repository.StatusServicoSpringDataRepository;
import com.mecanica.oficina_api.application.servico.gateway.StatusServicoGateway;
import com.mecanica.oficina_api.domain.ordemservico.ServicoStatus;
import org.springframework.stereotype.Repository;

import java.time.Duration;

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
}
