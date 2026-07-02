package com.mecanica.oficina_api.adapters.web.dto.request;

import jakarta.validation.Valid;
import java.util.List;

public class GerarOrcamentoRequest {

    private List<@Valid ItemOrcamentoRequest> insumos;
    private List<@Valid ItemServicoRequest> servicos;
    private String observacoes;

    public GerarOrcamentoRequest() {}

    public List<ItemOrcamentoRequest> getInsumos() { return insumos; }
    public void setInsumos(List<ItemOrcamentoRequest> insumos) { this.insumos = insumos; }
    public List<ItemServicoRequest> getServicos() { return servicos; }
    public void setServicos(List<ItemServicoRequest> servicos) { this.servicos = servicos; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
