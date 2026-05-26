package com.mecanica.oficina_api.application.ordemservico.gateway;

import com.mecanica.oficina_api.application.ordemservico.NotificacaoCliente;

public interface NotificadorCliente {

    void notificar(NotificacaoCliente notificacao);
}
