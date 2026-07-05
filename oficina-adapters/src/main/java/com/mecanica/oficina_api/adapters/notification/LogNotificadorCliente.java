package com.mecanica.oficina_api.adapters.notification;

import com.mecanica.oficina_api.application.ordemservico.gateway.NotificadorClienteGateway;
import com.mecanica.oficina_api.application.ordemservico.output.NotificacaoCliente;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Log em uma única linha com campos fixos (parseável por grep/Loki).
 * Ativo em todos os perfis exceto prod (em prod, EmailNotificadorCliente assume o papel).
 */
@Component
@Profile("!prod")
public class LogNotificadorCliente implements NotificadorClienteGateway {

    private static final Logger log = LoggerFactory.getLogger(LogNotificadorCliente.class);

    @Override
    public void notificar(NotificacaoCliente n) {
        log.info(
                "[NOTIFICACAO_CLIENTE] tipo_evento={} ordem_servico_id={} cliente_id={} veiculo_id={}",
                n.tipo().chaveLog(),
                n.ordemServicoId(),
                n.clienteId(),
                n.veiculoId());
    }
}
