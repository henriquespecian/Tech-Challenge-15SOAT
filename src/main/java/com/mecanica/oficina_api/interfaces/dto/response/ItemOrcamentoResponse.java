package com.mecanica.oficina_api.interfaces.dto.response;

import java.math.BigDecimal;

public class ItemOrcamentoResponse {

    private final String descricao;
    private final int quantidade;
    private final BigDecimal precoUnitario;
    private final BigDecimal valorTotal;

    public ItemOrcamentoResponse(String descricao, int quantidade, BigDecimal precoUnitario, BigDecimal valorTotal) {
        this.descricao = descricao;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
        this.valorTotal = valorTotal;
    }

    public String getDescricao() { return descricao; }
    public int getQuantidade() { return quantidade; }
    public BigDecimal getPrecoUnitario() { return precoUnitario; }
    public BigDecimal getValorTotal() { return valorTotal; }
}
