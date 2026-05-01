package com.mecanica.oficina_api.interfaces.dto.request;

import java.util.List;

public class GerarOrcamentoRequest {

    private List<ItemOrcamentoRequest> itens;
    private String observacoes;

    public GerarOrcamentoRequest() {}

    public List<ItemOrcamentoRequest> getItens() { return itens; }
    public void setItens(List<ItemOrcamentoRequest> itens) { this.itens = itens; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
