package com.mecanica.oficina_api.adapters.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import java.util.List;

public class CriarOrdemServicoRequest {

    @NotBlank(message = "O ID do veículo é obrigatório")
    private String veiculoId;

    @NotBlank(message = "O ID do cliente é obrigatório")
    private String clienteId;

    private List<ItemOrcamentoRequest> insumos;

    private List<ItemServicoRequest> servicos;

    private String observacoes;

    public CriarOrdemServicoRequest() {}

    public String getVeiculoId() { return veiculoId; }
    public void setVeiculoId(String veiculoId) { this.veiculoId = veiculoId; }
    public String getClienteId() { return clienteId; }
    public void setClienteId(String clienteId) { this.clienteId = clienteId; }
    public List<ItemOrcamentoRequest> getInsumos() { return insumos; }
    public void setInsumos(List<ItemOrcamentoRequest> insumos) { this.insumos = insumos; }
    public List<ItemServicoRequest> getServicos() { return servicos; }
    public void setServicos(List<ItemServicoRequest> servicos) { this.servicos = servicos; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
}
