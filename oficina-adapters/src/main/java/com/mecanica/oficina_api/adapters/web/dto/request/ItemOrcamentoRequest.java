package com.mecanica.oficina_api.adapters.web.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class ItemOrcamentoRequest {

    @NotBlank(message = "O ID do insumo é obrigatório")
    private String insumoId;

    @Positive(message = "A quantidade de insumo deve ser maior que zero")
    private int quantidade;

    public ItemOrcamentoRequest() {}

    public String getInsumoId() { return insumoId; }
    public void setInsumoId(String insumoId) { this.insumoId = insumoId; }
    public int getQuantidade() { return quantidade; }
    public void setQuantidade(int quantidade) { this.quantidade = quantidade; }
}
