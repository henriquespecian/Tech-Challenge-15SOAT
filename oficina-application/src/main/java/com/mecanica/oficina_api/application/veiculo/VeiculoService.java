package com.mecanica.oficina_api.application.veiculo;

import com.mecanica.oficina_api.application.cliente.gateway.ClienteGateway;
import com.mecanica.oficina_api.application.veiculo.gateway.VeiculoGateway;
import com.mecanica.oficina_api.domain.veiculo.Veiculo;
import java.util.List;

public class VeiculoService {

    private final VeiculoGateway veiculoGateway;
    private final ClienteGateway clienteGateway;

    public VeiculoService(VeiculoGateway veiculoGateway,
                          ClienteGateway clienteGateway) {
        this.veiculoGateway = veiculoGateway;
        this.clienteGateway = clienteGateway;
    }

    public Veiculo cadastrar(String clienteId, String placa, String marca, String modelo, int ano, String cor) {
        if (clienteGateway.findByIdAtivo(clienteId).isEmpty()) {
            throw new IllegalArgumentException("Cliente não encontrado: " + clienteId);
        }

        Veiculo veiculo = Veiculo.criar(clienteId, placa, marca, modelo, ano, cor);

        if (veiculoGateway.placaExiste(veiculo.getPlaca())) {
            throw new IllegalStateException("Já existe um veículo com a placa: " + placa);
        }

        return veiculoGateway.cadastrar(veiculo);
    }

    public Veiculo buscarPorId(String id) {
        return veiculoGateway.buscar(id)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado: " + id));
    }

    public List<Veiculo> listarPorCliente(String clienteId) {
        return veiculoGateway.buscarVeiculoPorCliente(clienteId);
    }

    public Veiculo alterar(String id, String placa, String marca, String modelo, int ano, String cor) {
        Veiculo existente = veiculoGateway.buscar(id)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado: " + id));

        Veiculo atualizado = Veiculo.reconstituir(id, existente.getClienteId(), placa, marca, modelo, ano, cor, true);

        if (!existente.getPlaca().equals(atualizado.getPlaca()) && veiculoGateway.buscarPorPlaca(atualizado.getPlaca())) {
            throw new IllegalStateException("Já existe um veículo com a placa: " + placa);
        }

        return veiculoGateway.alterar(id, atualizado);
    }

    public void deletar(String id) {
        veiculoGateway.buscar(id)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado: " + id));
        veiculoGateway.inativar(id);
    }
}
