package com.mecanica.oficina_api.interfaces.dto.response;

public class OrdemServicoResponse {

    private final String id;
    private final String veiculoId;
    private final String clienteId;
    private final String status;
    private final OrcamentoResponse orcamento;

    public OrdemServicoResponse(String id, String veiculoId, String clienteId, String status, OrcamentoResponse orcamento) {
        this.id = id;
        this.veiculoId = veiculoId;
        this.clienteId = clienteId;
        this.status = status;
        this.orcamento = orcamento;
    }

    public String getId() { return id; }
    public String getVeiculoId() { return veiculoId; }
    public String getClienteId() { return clienteId; }
    public String getStatus() { return status; }
    public OrcamentoResponse getOrcamento() { return orcamento; }
}
