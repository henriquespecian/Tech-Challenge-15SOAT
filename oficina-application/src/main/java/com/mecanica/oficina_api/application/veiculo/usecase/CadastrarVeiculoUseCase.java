package com.mecanica.oficina_api.application.veiculo.usecase;

import com.mecanica.oficina_api.application.cliente.gateway.ClienteGateway;
import com.mecanica.oficina_api.application.veiculo.gateway.VeiculoGateway;
import com.mecanica.oficina_api.domain.veiculo.Veiculo;

public class CadastrarVeiculoUseCase {
    private final VeiculoGateway veiculoGateway;
    private final ClienteGateway clienteGateway;

    public  CadastrarVeiculoUseCase(VeiculoGateway veiculoGateway, ClienteGateway clienteGateway) {
        this.veiculoGateway = veiculoGateway;
        this.clienteGateway = clienteGateway;
    }
    
    public Veiculo executar(String clienteId, String placa, String marca, String modelo, int ano, String cor) {
        if (clienteGateway.findByIdAtivo(clienteId).isEmpty()) {
            throw new IllegalArgumentException("Cliente não encontrado: " + clienteId);
        }

        if (veiculoGateway.placaExiste(placa)) {
            throw new IllegalArgumentException("Já existe um veículo com a placa: " + placa);
        }

        Veiculo veiculo = Veiculo.criar(clienteId, placa, marca, modelo, ano, cor);

        return veiculoGateway.cadastrar(veiculo);
    }
}
