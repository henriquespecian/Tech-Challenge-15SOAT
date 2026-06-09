package com.mecanica.oficina_api.application.ordemservico;

import java.util.ArrayList;
import java.util.List;

import com.mecanica.oficina_api.application.insumo.gateway.InsumosGateway;
import com.mecanica.oficina_api.application.ordemservico.input.GerarOrcamentoInput;
import com.mecanica.oficina_api.application.servico.gateway.ServicoGateway;
import com.mecanica.oficina_api.domain.insumo.Insumos;
import com.mecanica.oficina_api.domain.ordemservico.ItemOrcamento;
import com.mecanica.oficina_api.domain.servico.Servico;

public class MontadorItensOrcamento {
 
    private final InsumosGateway insumosGateway;
    private final ServicoGateway servicoGateway;
    
    public MontadorItensOrcamento(InsumosGateway insumosGateway, ServicoGateway servicoGateway) {
        this.insumosGateway = insumosGateway;
        this.servicoGateway = servicoGateway;
     }

    public List<ItemOrcamento> montar(GerarOrcamentoInput input) {
        List<ItemOrcamento> itens = new ArrayList<>();

        if (input.insumos() != null) {
            for (var i : input.insumos()) {
                Insumos insumo = insumosGateway.buscar(i.insumoId())
                        .orElseThrow(() -> new IllegalArgumentException("Insumo não encontrado: " + i.insumoId()));
                itens.add(new ItemOrcamento(insumo.getId(), null, insumo.getNome(), i.quantidade(), insumo.getPrecoUnitario()));
            }
        }

        if (input.servicos() != null) {
            for (var s : input.servicos()) {
                Servico servico = servicoGateway.buscar(s.servicoId())
                        .orElseThrow(() -> new IllegalArgumentException("Serviço não encontrado: " + s.servicoId()));
                itens.add(new ItemOrcamento(null, servico.getId(), servico.getNome(), s.quantidade(), servico.getPreco()));
            }
        }

        if (itens.isEmpty()) {
            throw new IllegalArgumentException("Orçamento deve ter ao menos um item");
        }

        return itens;
    }
}
