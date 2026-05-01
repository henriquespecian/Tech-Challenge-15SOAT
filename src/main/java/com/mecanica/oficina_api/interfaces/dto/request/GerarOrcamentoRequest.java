package com.mecanica.oficina_api.interfaces.dto.request;

import java.util.List;

public class GerarOrcamentoRequest {

    private List<ItemOrcamentoRequest> insumos;
    private List<ItemServicoRequest> servicos;
    private String observacoes;

    public GerarOrcamentoRequest() {}

    public List<ItemOrcamentoRequest> getInsumos() { return insumos; }
    public void setInsumos(List<ItemOrcamentoRequest> insumos) { this.insumos = insumos; }
    public List<ItemServicoRequest> getServicos() { return servicos; }
    public void setServicos(List<ItemServicoRequest> servicos) { this.servicos = servicos; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
