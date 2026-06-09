package com.mecanica.oficina_api.application.ordemservico.usecase;

import com.mecanica.oficina_api.application.cliente.gateway.ClienteGateway;
import com.mecanica.oficina_api.application.ordemservico.gateway.OrdemServicoGateway;
import com.mecanica.oficina_api.application.veiculo.gateway.VeiculoGateway;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServico;

public class CriarOrdemServicoUseCase {
    private final OrdemServicoGateway ordemServicoGateway;
    private final VeiculoGateway veiculoGateway;
    private final ClienteGateway clienteGateway;

    public CriarOrdemServicoUseCase(OrdemServicoGateway ordemServicoGateway, VeiculoGateway veiculoGateway, ClienteGateway clienteGateway) {
        this.ordemServicoGateway = ordemServicoGateway;
        this.veiculoGateway = veiculoGateway;
        this.clienteGateway = clienteGateway;
    }

    public OrdemServico executar(String veiculoId, String clienteId) {
        veiculoGateway.buscar(veiculoId)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado: " + veiculoId));
        clienteGateway.findByIdAtivo(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente não encontrado: " + clienteId));

        return ordemServicoGateway.cadastrar(veiculoId, clienteId);
    }
}
