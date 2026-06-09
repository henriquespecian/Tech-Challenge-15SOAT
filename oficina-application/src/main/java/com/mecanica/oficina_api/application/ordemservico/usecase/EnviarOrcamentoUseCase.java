package com.mecanica.oficina_api.application.ordemservico.usecase;

import com.mecanica.oficina_api.application.ordemservico.gateway.NotificadorClienteGateway;
import com.mecanica.oficina_api.application.ordemservico.gateway.OrdemServicoGateway;
import com.mecanica.oficina_api.application.ordemservico.output.NotificacaoCliente;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServico;

public class EnviarOrcamentoUseCase {
    private final OrdemServicoGateway ordemServicoGateway;
    private final NotificadorClienteGateway notificadorClienteGateway;

    public EnviarOrcamentoUseCase(OrdemServicoGateway ordemServicoGateway, NotificadorClienteGateway notificadorClienteGateway) {
        this.ordemServicoGateway = ordemServicoGateway;
        this.notificadorClienteGateway = notificadorClienteGateway;
    }

    public OrdemServico executar(String id) {
        OrdemServico os = ordemServicoGateway.encontrarOuLancar(id);
        os.enviarOrcamento();
        OrdemServico salva = ordemServicoGateway.atualizar(os);
        notificadorClienteGateway.notificar(NotificacaoCliente.envioOrcamento(salva));
        return salva;
    }
}
