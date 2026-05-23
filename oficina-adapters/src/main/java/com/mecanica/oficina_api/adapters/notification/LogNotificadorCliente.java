package com.mecanica.oficina_api.adapters.notification;

import com.mecanica.oficina_api.application.ordemservico.NotificacaoCliente;
import com.mecanica.oficina_api.application.ordemservico.NotificadorCliente;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Log em uma única linha com campos fixos (parseável por grep/Loki).
 * O padrão default do Spring/Logback não imprime {@code addKeyValue}; por isso os dados vão na mensagem.
 */
@Component
public class LogNotificadorCliente implements NotificadorCliente {

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
