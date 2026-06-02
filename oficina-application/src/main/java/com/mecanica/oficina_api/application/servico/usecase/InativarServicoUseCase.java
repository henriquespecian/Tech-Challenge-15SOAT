package com.mecanica.oficina_api.application.servico.usecase;

import com.mecanica.oficina_api.application.servico.gateway.ServicoGateway;

public class InativarServicoUseCase {
    private final ServicoGateway servicoGateway;

    public InativarServicoUseCase(ServicoGateway servicoGateway) {
        this.servicoGateway = servicoGateway;
    }

    public void executar(String id) {
        servicoGateway.buscar(id).orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));
        servicoGateway.inativar(id);
    }
}
