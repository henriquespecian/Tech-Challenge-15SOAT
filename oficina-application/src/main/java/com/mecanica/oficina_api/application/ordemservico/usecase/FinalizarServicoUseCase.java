package com.mecanica.oficina_api.application.ordemservico.usecase;

import com.mecanica.oficina_api.application.servico.gateway.StatusServicoGateway;
import com.mecanica.oficina_api.domain.ordemservico.ServicoStatus;
import com.mecanica.oficina_api.domain.ordemservico.StatusServico;

public class FinalizarServicoUseCase {
    private final StatusServicoGateway statusServicoGateway;

    public FinalizarServicoUseCase(StatusServicoGateway statusServicoGateway) {
        this.statusServicoGateway = statusServicoGateway;
    }

    public StatusServico executar(String servico_id) {
        StatusServico servico = statusServicoGateway.buscarPorIdEStatus(servico_id, ServicoStatus.INICIADO)
            .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado"));
        servico.finalizarServico();
        return statusServicoGateway.atualizar(servico);
    }
}
