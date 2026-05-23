package com.mecanica.oficina_api.adapters.web.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class OrcamentoResponse {

    private final String status;
    private final List<ItemOrcamentoResponse> itens;
    private final BigDecimal valorTotal;
    private final String observacoes;
    private final LocalDateTime respondidoEm;

    public OrcamentoResponse(String status, List<ItemOrcamentoResponse> itens, BigDecimal valorTotal,
                              String observacoes, LocalDateTime respondidoEm) {
        this.status = status;
        this.itens = itens;
        this.valorTotal = valorTotal;
        this.observacoes = observacoes;
        this.respondidoEm = respondidoEm;
    }

    public String getStatus() { return status; }
    public List<ItemOrcamentoResponse> getItens() { return itens; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public String getObservacoes() { return observacoes; }
    public LocalDateTime getRespondidoEm() { return respondidoEm; }
}
