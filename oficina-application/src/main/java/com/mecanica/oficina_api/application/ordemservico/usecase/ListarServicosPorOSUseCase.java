package com.mecanica.oficina_api.application.ordemservico.usecase;

import java.util.List;

import com.mecanica.oficina_api.application.servico.gateway.StatusServicoGateway;
import com.mecanica.oficina_api.domain.ordemservico.StatusServico;

public class ListarServicosPorOSUseCase {
    private final StatusServicoGateway statusServicoGateway;

    public ListarServicosPorOSUseCase(StatusServicoGateway statusServicoGateway) {
        this.statusServicoGateway = statusServicoGateway;
    }

    public List<StatusServico> executar(String id) {
        return statusServicoGateway.listarServicosPorOS(id);
    }
}
