package com.mecanica.oficina_api.adapters.web.dto.request;

public class ItemOrcamentoRequest {

    private String insumoId;
    private int quantidade;

    public ItemOrcamentoRequest() {}

    public String getInsumoId() { return insumoId; }
    public void setInsumoId(String insumoId) { this.insumoId = insumoId; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
}
