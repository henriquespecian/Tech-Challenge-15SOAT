package com.mecanica.oficina_api.application.veiculo.usecase;

import com.mecanica.oficina_api.application.veiculo.gateway.VeiculoGateway;

public class InativarVeiculoUseCase {
    private final VeiculoGateway veiculoGateway;

    public  InativarVeiculoUseCase(VeiculoGateway veiculoGateway) {
        this.veiculoGateway = veiculoGateway;
    }

    public void executar(String id) {
        veiculoGateway.buscar(id)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado: " + id));
        veiculoGateway.inativar(id);
    }
}
