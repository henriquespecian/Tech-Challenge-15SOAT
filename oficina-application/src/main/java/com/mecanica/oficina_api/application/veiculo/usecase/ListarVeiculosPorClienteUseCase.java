package com.mecanica.oficina_api.application.veiculo.usecase;

import java.util.List;

import com.mecanica.oficina_api.application.veiculo.gateway.VeiculoGateway;
import com.mecanica.oficina_api.domain.veiculo.Veiculo;

public class ListarVeiculosPorClienteUseCase {
    private final VeiculoGateway veiculoGateway;

    public ListarVeiculosPorClienteUseCase(VeiculoGateway veiculoGateway) {
        this.veiculoGateway = veiculoGateway;
    }

    public List<Veiculo> executar(String clienteId) {
        return veiculoGateway.buscarVeiculoPorCliente(clienteId);
    }
}
