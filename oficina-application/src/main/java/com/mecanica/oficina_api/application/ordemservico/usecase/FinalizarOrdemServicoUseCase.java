package com.mecanica.oficina_api.application.ordemservico.usecase;

import java.util.List;

import com.mecanica.oficina_api.application.ordemservico.gateway.NotificadorClienteGateway;
import com.mecanica.oficina_api.application.ordemservico.gateway.OrdemServicoGateway;
import com.mecanica.oficina_api.application.ordemservico.output.NotificacaoCliente;
import com.mecanica.oficina_api.application.servico.gateway.StatusServicoGateway;
import com.mecanica.oficina_api.domain.ordemservico.OrdemServico;
import com.mecanica.oficina_api.domain.ordemservico.ServicoStatus;
import com.mecanica.oficina_api.domain.ordemservico.StatusServico;

public class FinalizarOrdemServicoUseCase {
    private final OrdemServicoGateway ordemServicoGateway;
    private final StatusServicoGateway statusServicoGateway;
    private final NotificadorClienteGateway notificadorClienteGateway;

    public FinalizarOrdemServicoUseCase(OrdemServicoGateway ordemServicoGateway, StatusServicoGateway statusServicoGateway, NotificadorClienteGateway notificadorClienteGateway) {
        this.ordemServicoGateway = ordemServicoGateway;
        this.statusServicoGateway = statusServicoGateway;
        this.notificadorClienteGateway = notificadorClienteGateway;
    }

    public OrdemServico executar(String id) {
        OrdemServico os = ordemServicoGateway.encontrarOuLancar(id);
        os.finalizar();
        verificarServicosFinalizados(os.getId());

        OrdemServico salva = ordemServicoGateway.atualizar(os);
        notificadorClienteGateway.notificar(NotificacaoCliente.finalizacao(salva));
        return salva;
    }

   private void verificarServicosFinalizados(String ordemServicoId) {
        List<StatusServico> servicoEntityList = statusServicoGateway.listarServicosPorOS(ordemServicoId);

        int servicoFinalizadoContador = 0;
        for (StatusServico servicoEntity : servicoEntityList) {
            if (servicoEntity.getStatus().equals(ServicoStatus.FINALIZADO)) {
                servicoFinalizadoContador++;
            }
        }

        if (servicoFinalizadoContador != servicoEntityList.size()) {
            throw new IllegalStateException("Ainda há serviços para serem realizados na OS");
        }
    }
}
