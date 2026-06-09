package com.mecanica.oficina_api.application.ordemservico.usecase;

import com.mecanica.oficina_api.application.ordemservico.gateway.OrdemServicoGateway;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServico;

public class ConsultarOrdemServicoUseCase {

    private final OrdemServicoGateway ordemServicoGateway;

    public ConsultarOrdemServicoUseCase(OrdemServicoGateway ordemServicoGateway) {
        this.ordemServicoGateway = ordemServicoGateway;
    }

    public OrdemServico executar(String id) {
        return ordemServicoGateway.encontrarOuLancar(id);
    }
}
