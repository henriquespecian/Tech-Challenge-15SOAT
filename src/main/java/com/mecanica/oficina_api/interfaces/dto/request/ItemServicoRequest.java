package com.mecanica.oficina_api.interfaces.dto.request;

public class ItemServicoRequest {
    private String servicoId;
    private int quantidade;

    public ItemServicoRequest() {}

    public String getServicoId() { return servicoId; }
    public void setServicoId(String servicoId) { this.servicoId = servicoId; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
}
