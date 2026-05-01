package com.mecanica.oficina_api.interfaces.dto.response;

import java.math.BigDecimal;
import java.util.List;

public class OrcamentoResponse {

    private final String status;
    private final List<ItemOrcamentoResponse> itens;
    private final BigDecimal valorTotal;
    private final String observacoes;

    public OrcamentoResponse(String status, List<ItemOrcamentoResponse> itens, BigDecimal valorTotal, String observacoes) {
        this.status = status;
        this.itens = itens;
        this.valorTotal = valorTotal;
        this.observacoes = observacoes;
    }

    public String getStatus() { return status; }
    public List<ItemOrcamentoResponse> getItens() { return itens; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public String getObservacoes() { return observacoes; }
}
