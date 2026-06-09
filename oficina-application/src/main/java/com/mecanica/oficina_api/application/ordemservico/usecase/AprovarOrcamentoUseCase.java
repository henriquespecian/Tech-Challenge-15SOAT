package com.mecanica.oficina_api.application.ordemservico.usecase;

import com.mecanica.oficina_api.application.insumo.gateway.InsumosGateway;
import com.mecanica.oficina_api.application.insumo.gateway.NotificarEstoqueBaixoGateway;
import com.mecanica.oficina_api.application.insumo.output.AlertaEstoqueBaixo;
import com.mecanica.oficina_api.application.ordemservico.gateway.OrdemServicoGateway;
import com.mecanica.oficina_api.domain.insumo.Insumos;
import com.mecanica.oficina_api.domain.insumo.OrigemNotificacaoEstoque;
import com.mecanica.oficina_api.domain.ordemservico.ItemOrcamento;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServico;

public class AprovarOrcamentoUseCase {
    private final OrdemServicoGateway ordemServicoGateway;
    private final InsumosGateway insumosGateway;
    private final NotificarEstoqueBaixoGateway notificadorEstoqueBaixo;

    public AprovarOrcamentoUseCase(OrdemServicoGateway ordemServicoGateway, InsumosGateway insumosGateway, NotificarEstoqueBaixoGateway notificadorEstoqueBaixo) {
        this.ordemServicoGateway = ordemServicoGateway;
        this.insumosGateway = insumosGateway;
        this.notificadorEstoqueBaixo = notificadorEstoqueBaixo;
    }

    public OrdemServico executar(String id) {
        OrdemServico os = ordemServicoGateway.encontrarOuLancar(id);
        os.aprovarOrcamento();
        darBaixaInsumos(os);
        return ordemServicoGateway.atualizar(os);
    }

    private void darBaixaInsumos(OrdemServico ordemServico) {
        for (ItemOrcamento item : ordemServico.getOrcamento().getItens()) {

            if (item.getInsumoId() == null) {
                continue;
            }

            Insumos insumo = insumosGateway.buscar(item.getInsumoId()).orElse(null);

            if (insumo == null) {
                continue;
            }

            //Calcula o estoque
            int estoqueAnterior = insumo.getEstoqueAtual();
            int estoqueMinimoAnterior = insumo.getEstoqueMinimo();
            int novoEstoque = insumo.getEstoqueAtual() - item.getQuantidade();

            if (novoEstoque < 0) {
                throw new IllegalStateException("Estoque insuficiente para o insumo: " + insumo.getNome());
            }

            insumo.setEstoqueAtual(novoEstoque);
            insumosGateway.alterar(insumo.getId(), insumo);

            if (Insumos.deveEmitirAlerta(estoqueAnterior, estoqueMinimoAnterior, novoEstoque, insumo.getEstoqueMinimo())) {
                notificadorEstoqueBaixo.notificar(new AlertaEstoqueBaixo(
                        insumo.getId(),
                        insumo.getNome(),
                        estoqueAnterior,
                        novoEstoque,
                        insumo.getEstoqueMinimo(),
                        OrigemNotificacaoEstoque.BAIXA_ORDEM_SERVICO));
            }
        }
    }
}
