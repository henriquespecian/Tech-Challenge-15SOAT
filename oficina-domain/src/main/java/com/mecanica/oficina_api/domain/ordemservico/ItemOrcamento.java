package com.mecanica.oficina_api.domain.ordemservico;

import java.math.BigDecimal;
import java.util.Objects;
import lombok.Getter;

@Getter
public class ItemOrcamento {
    private final String insumoId;
    private final String servicoId;
    private final String descricao;
    private final int quantidade;
    private final BigDecimal precoUnitario;

    public ItemOrcamento(String insumoId, String servicoId, String descricao, int quantidade, BigDecimal precoUnitario) {
        this.insumoId = insumoId;
        this.servicoId = servicoId;
        this.descricao = Objects.requireNonNull(descricao, "Descrição é obrigatória");
        Objects.requireNonNull(precoUnitario, "Preço unitário é obrigatório");
        if (quantidade <= 0) throw new IllegalArgumentException("Quantidade deve ser maior que zero");
        if (precoUnitario.compareTo(BigDecimal.ZERO) <= 0) throw new IllegalArgumentException("Preço unitário deve ser maior que zero");
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    public BigDecimal getValorTotal() {
        return precoUnitario.multiply(BigDecimal.valueOf(quantidade));
    }
}
