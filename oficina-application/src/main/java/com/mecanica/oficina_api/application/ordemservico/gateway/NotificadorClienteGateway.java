package com.mecanica.oficina_api.application.ordemservico.gateway;

import com.mecanica.oficina_api.application.ordemservico.output.NotificacaoCliente;

public interface NotificadorClienteGateway {

    void notificar(NotificacaoCliente notificacao);
}
