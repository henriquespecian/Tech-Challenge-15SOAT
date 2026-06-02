package com.mecanica.oficina_api.application.servico.usecase;

import com.mecanica.oficina_api.application.servico.gateway.ServicoGateway;
import com.mecanica.oficina_api.domain.servico.Servico;

public class AtivarServicoUseCase {
    private final ServicoGateway servicoGateway;

    public AtivarServicoUseCase(ServicoGateway servicoGateway) {
        this.servicoGateway = servicoGateway;
    }

    public Servico executar(String id) {
        servicoGateway.buscar(id).orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));
        return servicoGateway.ativar(id);
    }
}
