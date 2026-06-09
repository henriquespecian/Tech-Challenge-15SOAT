package com.mecanica.oficina_api.application.ordemservico.usecase;

import java.util.List;

import com.mecanica.oficina_api.application.ordemservico.gateway.OrdemServicoGateway;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServico;

public class ListarPorVeiculoOrdemServicoUseCase {
    private final OrdemServicoGateway ordemServicoGateway;

    public ListarPorVeiculoOrdemServicoUseCase(OrdemServicoGateway ordemServicoGateway) {
        this.ordemServicoGateway = ordemServicoGateway;
    }

    public List<OrdemServico> executar(String veiculoId) {
        return ordemServicoGateway.listarPorVeiculo(veiculoId);
    }
}
