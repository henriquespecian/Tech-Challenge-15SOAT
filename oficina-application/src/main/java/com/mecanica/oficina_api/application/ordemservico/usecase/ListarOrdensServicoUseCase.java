package com.mecanica.oficina_api.application.ordemservico.usecase;

import java.util.List;

import com.mecanica.oficina_api.application.ordemservico.gateway.OrdemServicoGateway;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServico;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServicoStatus;

public class ListarOrdensServicoUseCase {
    private final OrdemServicoGateway ordemServicoGateway;

    public ListarOrdensServicoUseCase(OrdemServicoGateway ordemServicoGateway) {
        this.ordemServicoGateway = ordemServicoGateway;
    }

    public List<OrdemServico> executar(OrdemServicoStatus status) {
        return ordemServicoGateway.listar(status);
    }
}
