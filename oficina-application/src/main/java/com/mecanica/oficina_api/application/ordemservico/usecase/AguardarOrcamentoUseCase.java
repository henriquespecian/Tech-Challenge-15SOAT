package com.mecanica.oficina_api.application.ordemservico.usecase;

import com.mecanica.oficina_api.application.ordemservico.gateway.OrdemServicoGateway;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServico;

public class AguardarOrcamentoUseCase {
    private final OrdemServicoGateway ordemServicoGateway;

    public AguardarOrcamentoUseCase(OrdemServicoGateway ordemServicoGateway) {
        this.ordemServicoGateway = ordemServicoGateway;
    }

    public OrdemServico executar(String id) {
        OrdemServico os = ordemServicoGateway.encontrarOuLancar(id);
        os.aguardarOrcamento();
        return ordemServicoGateway.atualizar(os);
    }
}

