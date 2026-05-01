package com.mecanica.oficina_api.interfaces.dto.request;

import java.util.List;

public class GerarOrcamentoRequest {

    private List<ItemOrcamentoRequest> insumos;
    // TODO: adicionar List<ItemServicoRequest> servicos quando a entidade Servico for implementada
    private String observacoes;

    public GerarOrcamentoRequest() {}

    public List<ItemOrcamentoRequest> getInsumos() { return insumos; }
    public void setInsumos(List<ItemOrcamentoRequest> insumos) { this.insumos = insumos; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
