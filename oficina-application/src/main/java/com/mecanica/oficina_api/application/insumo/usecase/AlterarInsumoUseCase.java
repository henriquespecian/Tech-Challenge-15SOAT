package com.mecanica.oficina_api.application.insumo.usecase;

import java.math.BigDecimal;

import com.mecanica.oficina_api.application.insumo.gateway.InsumosGateway;
import com.mecanica.oficina_api.application.insumo.gateway.NotificarEstoqueBaixoGateway;
import com.mecanica.oficina_api.application.insumo.output.AlertaEstoqueBaixo;
import com.mecanica.oficina_api.domain.insumo.Insumos;
import com.mecanica.oficina_api.domain.insumo.OrigemNotificacaoEstoque;

public class AlterarInsumoUseCase {
    private final InsumosGateway insumosGateway;
    private final NotificarEstoqueBaixoGateway notificarEstoqueBaixoGateway;

    public AlterarInsumoUseCase(InsumosGateway insumosGateway, NotificarEstoqueBaixoGateway notificarEstoqueBaixoGateway) {
        this.insumosGateway = insumosGateway;
        this.notificarEstoqueBaixoGateway = notificarEstoqueBaixoGateway;
    }

    public void executar(String id, String nome, BigDecimal precoUnitario,  Integer estoqueAtual, Integer estoqueMinimo, String unidade) {

        if(!insumosGateway.existePorId(id)) {
            throw new IllegalArgumentException("Insumo não encontrado");
        }

        int estoqueAnterior = insumosGateway.obterEstoqueAtual(id);

        var insumos = Insumos.reconstituir(
            id,
            nome,
            precoUnitario,
            estoqueAtual,
            estoqueMinimo,
            unidade
        );

        insumosGateway.alterar(id, insumos);

        var notificarEstoqueBaixo = Insumos.deveNotificarAlteracaoInsumo(estoqueAtual, estoqueMinimo);

        if (notificarEstoqueBaixo) {

            notificarEstoqueBaixoGateway.notificar(new AlertaEstoqueBaixo(
                id,
                nome,
                estoqueAnterior,
                estoqueAtual,
                estoqueMinimo,
                OrigemNotificacaoEstoque.ALTERACAO_INSUMO));
        }    
    }
}
