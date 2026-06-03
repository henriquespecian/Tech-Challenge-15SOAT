package com.mecanica.oficina_api.application.veiculo.usecase;

import com.mecanica.oficina_api.application.veiculo.gateway.VeiculoGateway;
import com.mecanica.oficina_api.domain.veiculo.Veiculo;

public class AlterarVeiculoUseCase {
    private final VeiculoGateway veiculoGateway;

    public AlterarVeiculoUseCase(VeiculoGateway veiculoGateway) {
        this.veiculoGateway = veiculoGateway;
    }

    public Veiculo executar(String id, String placa, String marca, String modelo, int ano, String cor) {
        Veiculo existente = veiculoGateway.buscar(id)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado: " + id));

        Veiculo atualizado = Veiculo.reconstituir(id, existente.getClienteId(), placa, marca, modelo, ano, cor, true);

        if (!existente.getPlaca().equals(atualizado.getPlaca()) && veiculoGateway.buscarPorPlaca(atualizado.getPlaca())) {
            throw new IllegalStateException("Já existe um veículo com a placa: " + placa);
        }

        return veiculoGateway.alterar(id, atualizado);
    }
}
