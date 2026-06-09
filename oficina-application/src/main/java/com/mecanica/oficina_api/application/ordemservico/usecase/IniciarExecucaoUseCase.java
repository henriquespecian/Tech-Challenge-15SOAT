package com.mecanica.oficina_api.application.ordemservico.usecase;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.mecanica.oficina_api.application.ordemservico.gateway.OrdemServicoGateway;
import com.mecanica.oficina_api.application.servico.gateway.StatusServicoGateway;
import com.mecanica.oficina_api.domain.ordemservico.ItemOrcamento;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServico;
import com.mecanica.oficina_api.domain.ordemservico.StatusServico;

public class IniciarExecucaoUseCase {
    private final OrdemServicoGateway ordemServicoGateway;
    private final StatusServicoGateway statusServicoGateway;
    
    public IniciarExecucaoUseCase(OrdemServicoGateway ordemServicoGateway, StatusServicoGateway statusServicoGateway) {
        this.ordemServicoGateway = ordemServicoGateway;
        this.statusServicoGateway = statusServicoGateway;
    }

    public OrdemServico executar(String id) {
        OrdemServico os = ordemServicoGateway.encontrarOuLancar(id);
        os.iniciarExecucao();
        criarStatusIndividuaisPorServico(os.getOrcamento().getItens(), os.getId());
        return ordemServicoGateway.atualizar(os);
    }

    private List<StatusServico> criarStatusIndividuaisPorServico(List<ItemOrcamento> itemOrcamentoList, String ordemServicoId) {
        List<StatusServico> statusServicoList = new ArrayList<>();
        itemOrcamentoList.forEach(item -> {
            if (!Objects.isNull(item.getServicoId())) {
                StatusServico statusServico = StatusServico.criar(ordemServicoId, item.getServicoId());
                statusServicoList.add(statusServico);
            }
        });

        return statusServicoGateway.salvarLista(statusServicoList);
    }
}
