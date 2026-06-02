package com.mecanica.oficina_api.application.servico.usecase;

import com.mecanica.oficina_api.application.servico.gateway.ServicoGateway;
import com.mecanica.oficina_api.application.servico.gateway.StatusServicoGateway;
import com.mecanica.oficina_api.domain.servico.Servico;
import com.mecanica.oficina_api.domain.servico.TempoMedioServico;

public class ConsultarTempoMedioUseCase {
    
    private final ServicoGateway servicoGateway;
    private final StatusServicoGateway statusServicoGateway;

    public ConsultarTempoMedioUseCase(ServicoGateway servicoGateway, StatusServicoGateway statusServicoGateway) {
        this.servicoGateway = servicoGateway;
        this.statusServicoGateway = statusServicoGateway;
    }

    public TempoMedioServico executar(String id) {
        Servico servico = servicoGateway.buscar(id)
            .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));
        double media = statusServicoGateway.calcularTempoMedioMinutos(id);
        return new TempoMedioServico(servico.getId(), servico.getNome(), media);
    }
}
