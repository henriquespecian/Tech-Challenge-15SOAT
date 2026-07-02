package com.mecanica.oficina_api.adapters.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class ItemServicoRequest {
    @NotBlank(message = "O ID do serviço é obrigatório")
    private String servicoId;

    @Positive(message = "A quantidade de serviço deve ser maior que zero")
    private int quantidade;

    public ItemServicoRequest() {}

    public String getServicoId() { return servicoId; }
    public void setServicoId(String servicoId) { this.servicoId = servicoId; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
}
