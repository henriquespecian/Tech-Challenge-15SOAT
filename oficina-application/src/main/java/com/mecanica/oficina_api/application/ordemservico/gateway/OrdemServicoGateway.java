package com.mecanica.oficina_api.application.ordemservico.gateway;

import java.util.List;
import java.util.Optional;

import com.mecanica.oficina_api.application.ordemservico.output.MinhaOrdemServicoOutput;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServico;

public interface OrdemServicoGateway {
    OrdemServico cadastrar(String veiculoId, String clienteId);
    Optional<OrdemServico> buscar(String id);
    List<MinhaOrdemServicoOutput> buscarPorCliente(String clienteId);
}
