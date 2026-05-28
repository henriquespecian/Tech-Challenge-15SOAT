package com.mecanica.oficina_api.application.ordemservico.gateway;

import java.util.List;
import java.util.Optional;

import com.mecanica.oficina_api.application.ordemservico.output.MinhaOrdemServicoOutput;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServico;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServicoStatus;

public interface OrdemServicoGateway {
    OrdemServico cadastrar(String veiculoId, String clienteId);
    OrdemServico atualizar(OrdemServico ordemServico);
    Optional<OrdemServico> buscar(String id);
    List<OrdemServico> listar(OrdemServicoStatus status);
    List<OrdemServico> listarPorVeiculo(String veiculoId);
    List<MinhaOrdemServicoOutput> buscarPorCliente(String clienteId);
}
