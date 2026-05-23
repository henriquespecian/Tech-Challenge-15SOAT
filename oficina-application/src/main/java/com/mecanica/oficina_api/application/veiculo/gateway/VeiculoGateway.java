package com.mecanica.oficina_api.application.veiculo.gateway;

import java.util.List;
import java.util.Optional;

import com.mecanica.oficina_api.domain.veiculo.Veiculo;

public interface VeiculoGateway {
    boolean placaExiste(String placa);
    Veiculo cadastrar(Veiculo request);
    Optional<Veiculo> buscar(String id);
    List<Veiculo> buscarVeiculoPorCliente(String id);
    void inativar(String id);
    boolean buscarPorPlaca(String placa);
    Veiculo alterar(String id, Veiculo request);
}
