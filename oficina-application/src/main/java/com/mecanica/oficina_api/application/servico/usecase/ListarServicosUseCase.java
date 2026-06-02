package com.mecanica.oficina_api.application.servico.usecase;

import java.util.List;

import com.mecanica.oficina_api.application.servico.gateway.ServicoGateway;
import com.mecanica.oficina_api.domain.servico.Servico;

public class ListarServicosUseCase {
    
    private final ServicoGateway servicoGateway;

    public ListarServicosUseCase(ServicoGateway servicoGateway) {
        this.servicoGateway = servicoGateway;
    }

    public List<Servico> executar() {
        return servicoGateway.listar();
    }
}
