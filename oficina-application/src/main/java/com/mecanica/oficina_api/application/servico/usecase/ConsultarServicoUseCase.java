package com.mecanica.oficina_api.application.servico.usecase;

import com.mecanica.oficina_api.application.servico.gateway.ServicoGateway;
import com.mecanica.oficina_api.domain.servico.Servico;

public class ConsultarServicoUseCase {
    
    private final ServicoGateway servicoGateway;

    public ConsultarServicoUseCase(ServicoGateway servicoGateway) {
        this.servicoGateway = servicoGateway;
    }

    public Servico executar(String id) {
        return servicoGateway.buscarOuFalhar(id);
    }
}
