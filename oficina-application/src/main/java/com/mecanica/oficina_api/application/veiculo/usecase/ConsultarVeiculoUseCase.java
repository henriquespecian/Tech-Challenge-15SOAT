package com.mecanica.oficina_api.application.veiculo.usecase;

import com.mecanica.oficina_api.application.veiculo.gateway.VeiculoGateway;
import com.mecanica.oficina_api.domain.veiculo.Veiculo;

public class ConsultarVeiculoUseCase {
    private final VeiculoGateway veiculoGateway;

    public ConsultarVeiculoUseCase(VeiculoGateway veiculoGateway) {
        this.veiculoGateway = veiculoGateway;
    }

    public Veiculo executar(String id) {
        return veiculoGateway.buscar(id)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado: " + id));
    }
}
