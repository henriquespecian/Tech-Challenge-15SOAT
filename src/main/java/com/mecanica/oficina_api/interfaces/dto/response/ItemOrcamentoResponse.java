package com.mecanica.oficina_api.interfaces.dto.response;

import java.math.BigDecimal;

public class ItemOrcamentoResponse {
    private final String insumo_id;
    private final String servico_id;
    private final String descricao;
    private final int quantidade;
    private final BigDecimal precoUnitario;
    private final BigDecimal valorTotal;

    public ItemOrcamentoResponse(String insumo_id, String servico_id,String descricao, int quantidade, BigDecimal precoUnitario, BigDecimal valorTotal) {
        this.insumo_id = insumo_id;
        this.servico_id = servico_id;
        this.descricao = descricao;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.valorTotal = valorTotal;
    }

    public String getInsumo_id() {
        return insumo_id;
    }
    public String getServico_id() {
        return servico_id;
    }
    public String getDescricao() { return descricao; }
    public int getQuantidade() { return quantidade; }
    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public BigDecimal getValorTotal() { return valorTotal; }
}
