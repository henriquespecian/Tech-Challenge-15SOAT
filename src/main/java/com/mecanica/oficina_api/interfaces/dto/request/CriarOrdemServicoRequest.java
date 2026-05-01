package com.mecanica.oficina_api.interfaces.dto.request;

public class CriarOrdemServicoRequest {

    private String veiculoId;
    private String clienteId;

    public CriarOrdemServicoRequest() {}

    public String getVeiculoId() { return veiculoId; }
    public void setVeiculoId(String veiculoId) { this.veiculoId = veiculoId; }
    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }
}
