package com.mecanica.oficina_api.application.ordemservico.usecase;

import com.mecanica.oficina_api.application.ordemservico.gateway.OrdemServicoGateway;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServico;

public class EntregarOrdemServicoUseCase {
    private final OrdemServicoGateway ordemServicoGateway;

    public EntregarOrdemServicoUseCase(OrdemServicoGateway ordemServicoGateway) {
        this.ordemServicoGateway = ordemServicoGateway;
    }

    public OrdemServico executar(String id) {
        OrdemServico ordemServico = ordemServicoGateway.encontrarOuLancar(id);
        ordemServico.entregar();
        return ordemServicoGateway.atualizar(ordemServico);
    }

}
