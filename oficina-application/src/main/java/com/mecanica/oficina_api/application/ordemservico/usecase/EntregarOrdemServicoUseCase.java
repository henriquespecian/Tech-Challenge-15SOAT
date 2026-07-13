package com.mecanica.oficina_api.application.ordemservico.usecase;

import com.mecanica.oficina_api.application.ordemservico.gateway.NotificadorClienteGateway;
import com.mecanica.oficina_api.application.ordemservico.gateway.OrdemServicoGateway;
import com.mecanica.oficina_api.application.ordemservico.output.NotificacaoCliente;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServico;

public class EntregarOrdemServicoUseCase {
    private final OrdemServicoGateway ordemServicoGateway;
    private final NotificadorClienteGateway notificadorClienteGateway;

    public EntregarOrdemServicoUseCase(OrdemServicoGateway ordemServicoGateway, NotificadorClienteGateway notificadorClienteGateway) {
        this.ordemServicoGateway = ordemServicoGateway;
        this.notificadorClienteGateway = notificadorClienteGateway;
    }

    public OrdemServico executar(String id) {
        OrdemServico ordemServico = ordemServicoGateway.encontrarOuLancar(id);
        ordemServico.entregar();
        OrdemServico salva = ordemServicoGateway.atualizar(ordemServico);

        notificadorClienteGateway.notificar(NotificacaoCliente.entrega(salva));

        return salva;
    }

}
