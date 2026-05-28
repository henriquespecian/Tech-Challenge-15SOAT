package com.mecanica.oficina_api.application.servico.gateway;

import java.util.List;
import java.util.Optional;

import com.mecanica.oficina_api.domain.ordemservico.ServicoStatus;
import com.mecanica.oficina_api.domain.ordemservico.StatusServico;

public interface StatusServicoGateway {
    double calcularTempoMedioMinutos(String servicoId);
    List<StatusServico> salvarLista(List<StatusServico> listaStatusServico);
    List<StatusServico> listarServicosPorOS(String ordemServicoId);
    Optional<StatusServico> buscarPorIdEStatus(String id, ServicoStatus status);
    StatusServico atualizar(StatusServico statusServico);
    
}
