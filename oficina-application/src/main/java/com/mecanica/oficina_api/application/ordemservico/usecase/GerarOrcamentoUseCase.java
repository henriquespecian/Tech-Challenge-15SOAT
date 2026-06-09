package com.mecanica.oficina_api.application.ordemservico.usecase;

import com.mecanica.oficina_api.application.ordemservico.MontadorItensOrcamento;
import com.mecanica.oficina_api.application.ordemservico.gateway.OrdemServicoGateway;
import com.mecanica.oficina_api.application.ordemservico.input.GerarOrcamentoInput;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServico;

public class GerarOrcamentoUseCase {
    private final OrdemServicoGateway ordemServicoGateway;
    private final MontadorItensOrcamento montadorItensOrcamento;

    public GerarOrcamentoUseCase(OrdemServicoGateway ordemServicoGateway, MontadorItensOrcamento montadorItensOrcamento) {
        this.ordemServicoGateway = ordemServicoGateway;
        this.montadorItensOrcamento = montadorItensOrcamento;
    }

    public OrdemServico executar(String id, GerarOrcamentoInput input) {
        OrdemServico os = ordemServicoGateway.encontrarOuLancar(id);
        os.gerarOrcamento(montadorItensOrcamento.montar(input), input.observacoes());
        return ordemServicoGateway.atualizar(os);
    }
}
