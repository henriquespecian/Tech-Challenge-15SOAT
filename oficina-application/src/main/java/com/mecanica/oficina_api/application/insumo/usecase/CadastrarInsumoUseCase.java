package com.mecanica.oficina_api.application.insumo.usecase;

import java.math.BigDecimal;

import com.mecanica.oficina_api.application.insumo.gateway.InsumosGateway;
import com.mecanica.oficina_api.domain.insumo.Insumos;

public class CadastrarInsumoUseCase {
    private final InsumosGateway insumosGateway;

    public CadastrarInsumoUseCase(InsumosGateway insumosGateway) {
        this.insumosGateway = insumosGateway;
    }

    public Insumos executar(String nome, BigDecimal precoUnitario,  Integer estoqueAtual, Integer estoqueMinimo, String unidade) {

        if (insumosGateway.existePorNome(nome)){
            throw new IllegalArgumentException("O Insumo "+ nome +" já está cadastrado");
        }

        Insumos insumo = Insumos.criar(nome, precoUnitario, estoqueAtual, estoqueMinimo, unidade);

        return insumosGateway.criar(insumo);
    }
}
