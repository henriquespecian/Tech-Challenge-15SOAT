package com.mecanica.oficina_api.interfaces.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OrdemServicoResponse {

    private final String id;
    private final String veiculoId;
    private final String clienteId;
    private final String status;
    private final BigDecimal valorFinal;
    private final LocalDateTime dataFinal;
    private final OrcamentoResponse orcamento;

    public OrdemServicoResponse(String id, String veiculoId, String clienteId, String status,
                                BigDecimal valorFinal, LocalDateTime dataFinal, OrcamentoResponse orcamento) {
        this.id = id;
        this.veiculoId = veiculoId;
        this.clienteId = clienteId;
        this.status = status;
        this.valorFinal = valorFinal;
        this.dataFinal = dataFinal;
        this.orcamento = orcamento;
    }

    public String getId() { return id; }
    public String getVeiculoId() { return veiculoId; }
    public String getClienteId() { return clienteId; }
    public String getStatus() { return status; }
    public BigDecimal getValorFinal() { return valorFinal; }
    public LocalDateTime getDataFinal() { return dataFinal; }
    public OrcamentoResponse getOrcamento() { return orcamento; }
}
