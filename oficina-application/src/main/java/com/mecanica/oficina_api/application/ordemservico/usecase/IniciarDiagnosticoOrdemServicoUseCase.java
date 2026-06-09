package com.mecanica.oficina_api.application.ordemservico.usecase;

import com.mecanica.oficina_api.application.ordemservico.gateway.OrdemServicoGateway;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServico;

public class IniciarDiagnosticoOrdemServicoUseCase {
    private final OrdemServicoGateway ordemServicoGateway;

    public IniciarDiagnosticoOrdemServicoUseCase(OrdemServicoGateway ordemServicoGateway) {
        this.ordemServicoGateway = ordemServicoGateway;
    }

    public OrdemServico executar(String id) {
        OrdemServico os = ordemServicoGateway.encontrarOuLancar(id);
        os.iniciarDiagnostico();
        return ordemServicoGateway.atualizar(os);
    }
}
