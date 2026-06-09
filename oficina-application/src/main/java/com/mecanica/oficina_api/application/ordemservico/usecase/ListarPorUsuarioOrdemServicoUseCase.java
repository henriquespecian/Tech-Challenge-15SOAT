package com.mecanica.oficina_api.application.ordemservico.usecase;

import java.util.List;

import com.mecanica.oficina_api.application.ordemservico.gateway.OrdemServicoGateway;
import com.mecanica.oficina_api.application.ordemservico.output.MinhaOrdemServicoOutput;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServicoStatus;

public class ListarPorUsuarioOrdemServicoUseCase {
    private final OrdemServicoGateway ordemServicoGateway;

    public ListarPorUsuarioOrdemServicoUseCase(OrdemServicoGateway ordemServicoGateway) {
        this.ordemServicoGateway = ordemServicoGateway;
    }

    public List<MinhaOrdemServicoOutput> executar(String clienteId, OrdemServicoStatus status, String placa) {

        List<MinhaOrdemServicoOutput> osList = ordemServicoGateway.buscarPorCliente(clienteId);

        if (status != null) {
            osList = osList.stream()
                .filter(os -> os.getStatus().equals(status.name()))
                .toList();
        }

        if (placa != null && !placa.isBlank()) {
            osList = osList.stream().filter(os -> os.getPlaca().equals(placa)).toList();
        }

        return osList;
    }

    
}
